package it.cm.cmd.core.terminal.frame

import it.cm.cmd.core.configuration
import it.cm.cmd.core.constant.Costants.CMD_DIMENSION
import it.cm.cmd.core.terminal.component.TerminalTextPane
import it.cm.cmd.core.terminal.listener.CLIDocumentFilter
import it.cm.cmd.core.terminal.listener.actionMap.FrameActionMap
import it.cm.cmd.core.terminal.panel.CLITopPanel
import it.cm.cmd.core.terminal.panel.terminal_panel.AbstractTerminalPanel
import it.cm.cmd.core.terminal.panel.terminal_panel.TabbedTerminalPanel
import it.cm.cmd.core.terminal.panel.terminal_panel.TerminalPanel
import it.cm.cmd.core.util.HistoryHandler
import it.cm.common.ui.FrameOut
import it.cm.shortcut.ui.panel.TopPanel.Companion.getImageIcon
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.geom.RoundRectangle2D
import java.net.InetAddress
import java.net.UnknownHostException
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.KeyStroke
import javax.swing.text.AttributeSet
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext


//Controllare che non stia creando infiniti actionmap ed in caso spostare qui quelli non necessario sul keylistener
open class TerminalFrame(
    private val isOnlyOut: Boolean = false
    , sessionKey: String
) : FrameOut(), IDispose, ICaretListener, IBaseCLI, ITextPaneOutputHandler {

    companion object {
        const val TITLE = "CM Terminal"
    }

    private val filter = CLIDocumentFilter(this)
    private var pasting: Boolean = false
    private val attr: AttributeSet
        get() = _textPane?.attr ?: SimpleAttributeSet()
    lateinit var middlePanel: AbstractTerminalPanel
    private lateinit var topPanel: CLITopPanel

    override var currentPath: String
        get() = middlePanel.currentPath ?: "Not provided"
        set(value) { middlePanel.currentPath = value }

    override val _filter: CLIDocumentFilter
        get() = filter
    override val _pasting: Boolean
        get() = pasting
    final override val _textPane: TerminalTextPane?
        get() = middlePanel._textPane
    override val _consoleOffset: Int
        get() = (_textPane?.let { it.styledDocument.endPosition.offset - 1 }) ?: 0
    val _history: HistoryHandler
        get() = middlePanel.history()

//    val _currentTab: Tab?
//        get() = middlePanel.selectedTab

    init {
        this.sessionKey = sessionKey
        initMainLayout()
        addContents()
        _textPane?.let {
            if (isOnlyOut)
                print("")
            else
                middlePanel.disableTextPane()

            filter.updateCaretPosition()
        }
        Toolkit.getDefaultToolkit().addAWTEventListener({
                if (isFocused)
                    this@TerminalFrame.getRootPane().border = BorderFactory.createLineBorder(Color(255, 255, 255, 100))
                else
                    this@TerminalFrame.getRootPane().border = null
            }, AWTEvent.WINDOW_FOCUS_EVENT_MASK
        )

        changeTitle(TITLE, true)
        addHotKeys()
        contentPane.requestFocus()

        addKeyListener(object: KeyListener {
            override fun keyTyped(e: KeyEvent) { }
            override fun keyPressed(e: KeyEvent) { }
            override fun keyReleased(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ALT)
                    topPanel.showMenuBar()
            }
        })
    }

//    override fun closeTab(): Unit = middlePanel.closeTab()
//    override fun newTab(): Unit = middlePanel.newTab()
    override fun runCommand(command: String) {
        _textPane?.runCommand(command)
    }

    override fun append(toAppend: String, foregroundColor: Color) {
        _textPane?.append(foregroundColor, toAppend)
    }

    override fun appendAnsi(toAppend: String, foregroundColor: Color) {
        _textPane?.appendANSI(toAppend)
    }
    override fun appendLine(toAppend: String) {
        _textPane?.append(toAppend = toAppend + "\n")
    }


    private fun addContents() {
        topPanel = CLITopPanel(this)
        contentPane.add(topPanel, BorderLayout.NORTH)
        middlePanel = if (isOnlyOut)
            TerminalPanel(sessionKey, this, this)
        else
            TabbedTerminalPanel(sessionKey, this, this)
        contentPane.add(middlePanel)
    }

    private fun initMainLayout() {
        title = TITLE
        isUndecorated = true
        background = Color(0, 0, 0, 0)
        layout = BorderLayout()
        iconImage = getImageIcon("/icons/cmd-terminal.png", null, Dimension(30, 30))?.image
        setDefaultLookAndFeelDecorated(false)
//        addComponentListener(object : ComponentAdapter() {
//            override fun componentResized(e: ComponentEvent) {
//                shape = RoundRectangle2D.Double(0.0, 0.0, width.toDouble(), height.toDouble(), 20.0, 20.0)
//            }
//        })
//        shape = RoundRectangle2D.Double(0.0, 0.0, getWidth().toDouble(), getHeight().toDouble(), 80.0, 80.0)
        size = CMD_DIMENSION
        setLocationRelativeTo(null)
        defaultCloseOperation = DISPOSE_ON_CLOSE
    }

    private fun addHotKeys() {
        val contentPane = contentPane as JPanel
        contentPane.apply {
            val key1: KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK)
            actionMap.put("CLOSE_TAB", FrameActionMap.CLOSE_TAB(sessionKey))
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key1, "CLOSE_TAB")

            val key3: KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK)
            actionMap.put("NEW_TAB", FrameActionMap.NEW_TAB(sessionKey))
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key3, "NEW_TAB")
        }
    }

    override fun print(processed: String): Unit = print(processed, false) {
        val sc = StyleContext.getDefaultStyleContext()
        sc.addAttribute(it, StyleConstants.Bold, true)
    }
    //Aggiungere il bold e rimuoverlo
    override fun printInplace(toPrint: String): Unit = print(toPrint, true)

    /**
     * Taccone, sistemare gli Attributes
     */
    fun print(output: String, inplace: Boolean, tempAttrChanges: (AttributeSet)->AttributeSet = { it }) {
        val toPrint =
            if (!inplace)  {
                if (output.endsWith("\n"))
                    output + getLinePrefix()
                else
                    getLinePrefix() + output
            } else
                output
//        attr.getAttribute(Bold)
        _textPane?.styledDocument?.insertString(_consoleOffset, toPrint, tempAttrChanges(attr))
        val sc = StyleContext.getDefaultStyleContext()
        sc.addAttribute(attr, StyleConstants.Bold, false)
        _textPane?.styledDocument?.insertString(_consoleOffset, " ", attr)
    }

    override fun getLinePrefix() = "[${getUsername()}@${getHostname()} ${currentPath}]$>"

    private fun getUsername() = System.getProperty("user.name")
    private fun getHostname() =
        try {
            InetAddress.getLocalHost().hostName
        } catch (ex: UnknownHostException) {
            "~"
        }

    override fun printNewLine() {
        print("\n")
        filter.updateCaretPosition()
    }

    override fun repaint() {
        //TODO Controllare
//        if (!isDisposed)
//            repaint()
    }

    override fun hasToBeAlwaysOnTop() = configuration?.enabledAlwaysOnTop == true
    override fun newTab() {
        if (middlePanel is TabbedTerminalPanel)
            (middlePanel as TabbedTerminalPanel).newTab()
    }

//    fun appendAnsi(toAppend: String, tabID: String = "") {
//        middlePanel.tabs.first { it.tabID == tabID }.textPane.appendANSI(toAppend)
//    }

    override fun clear() {
        _textPane?.let {
            filter.clearText()
            filter.updateCaretPosition()
        }
    }

    override fun closeTab() {
        if (middlePanel is TabbedTerminalPanel)
            (middlePanel as TabbedTerminalPanel).closeTab()
    }

    final override fun changeTitle(title: String, toWindow: Boolean) {
        if (toWindow) {
            this.title = title
            topPanel.changeTitle(title)
        } else if (middlePanel is TabbedTerminalPanel)
            (middlePanel as TabbedTerminalPanel).changeTitle(title)
    }

    fun barMenuVisibility() {

    }

}