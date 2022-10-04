package it.cm.cmd.core.ui.cli

import it.cm.cmd.core.HAS_BEEN_INITIALIZED
import it.cm.cmd.core.configuration
import it.cm.common.ui.FrameOut
import it.cm.cmd.core.ui.cli.Costants.CMD_DIMENSION
import it.cm.cmd.core.ui.cli._interface.IBaseCLI
import it.cm.cmd.core.ui.cli._interface.ICaretListener
import it.cm.cmd.core.ui.cli._interface.IDispose
import it.cm.cmd.core.ui.cli._interface.ITextPaneOutputHandler
import it.cm.cmd.core.ui.cli.component.CLITextPane
import it.cm.cmd.core.ui.cli.listener.CLIDocumentFilter
import it.cm.cmd.core.ui.cli.listener.actionMap.FrameActionMap
import it.cm.cmd.core.ui.cli.panel.CLIMiddlePanel
import it.cm.cmd.core.ui.cli.panel.CLITopPanel
import it.cm.shortcut.ui.panel.TopPanel.Companion.getImageIcon
import org.apache.commons.lang3.StringUtils
import java.awt.*
import java.awt.event.AWTEventListener
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.InetAddress
import java.net.UnknownHostException
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.KeyStroke
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.DocumentFilter
import javax.swing.text.SimpleAttributeSet


//Controllare che non stia creando infiniti actionmap ed in caso spostare qui quelli non necessario sul keylistener
abstract class CLIFrame2(
    private val isOnlyOut: Boolean = false
    , sessionKey: String
) : FrameOut(), IDispose, ICaretListener, IBaseCLI, ITextPaneOutputHandler {

    private val TITLE = "Java CMD"

    private val filter = CLIDocumentFilter(this)
    private var pasting: Boolean = false
    private val attr: AttributeSet
        get() = _textPane?.characterAttributes ?: SimpleAttributeSet()
    private lateinit var textPane: CLITextPane
    private lateinit var middlePanel: CLIMiddlePanel
    private lateinit var topPanel: CLITopPanel

    override val _filter: CLIDocumentFilter
        get() = filter
    override val _pasting: Boolean
        get() = pasting
    override val _textPane: CLITextPane?
        get() = middlePanel._textPane
    override val _consoleOffset: Int
        get() = (_textPane?.let { it.styledDocument.endPosition.offset - 1 }) ?: 0;

    init {
        if (!HAS_BEEN_INITIALIZED)
            throw RuntimeException("E' necessario inizializzare il framework prima di utilizzarlo")
        this.sessionKey = sessionKey
//        inputContext.selectInputMethod(Locale.ITALIAN)
        initMainLayout()
        addContents()
        if (_textPane != null) {
            if (isOnlyOut)
                print("")
            else
                middlePanel.disableTextPane()

            filter.updateCaretPosition()
        }
        Toolkit.getDefaultToolkit().addAWTEventListener(AWTEventListener {
                if (isFocused)
                    this@CLIFrame2.getRootPane().border = BorderFactory.createLineBorder(Color(255, 255, 255, 100))
                else
                    this@CLIFrame2.getRootPane().border = null
            }, AWTEvent.WINDOW_FOCUS_EVENT_MASK
        )

        changeTitle(TITLE, true)
        addHotKeys()
        contentPane.requestFocus()
    }

    override fun closeTab() {
        middlePanel.closeTab()
    }
    override fun newTab() {
        middlePanel.newTab()
    }
    override fun runCommand(command: String) {
        _textPane?.runCommand(command)
    }


    private fun addContents() {
        topPanel = CLITopPanel(this)
        contentPane.add(topPanel, BorderLayout.NORTH)
        val cliMiddlePanel = CLIMiddlePanel(sessionKey, this, this, isOnlyOut, contentPane)
        middlePanel = cliMiddlePanel
        contentPane.add(cliMiddlePanel)
//        Per qualche ragione se creo il tab così, me lo rende inutilizzabile (non editabile)
//        if (!isOnlyOut) newTab()
    }

    private fun initMainLayout() {
        title = TITLE
        isUndecorated = true
        background = Color(0, 0, 0, 0)
        layout = BorderLayout()
        iconImage = getImageIcon("/icons/cmd-terminal.png", null, Dimension(30, 30))?.image
        setDefaultLookAndFeelDecorated(false)
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

    final override fun print(processed: String) {
        print(processed, attr, false)
    }
    override fun printInplace(content: String) {
        //Aggiungere il bold e rimuoverlo
        print(content, attr, true)
    }

    fun print(output: String, style: AttributeSet, inplace: Boolean) {
        var output = output
        try {
            var pre = ""
            if (!inplace)  {
                if (StringUtils.endsWith(output, "\n")) {
                    pre = output
                    output = getLinePrefix()
                } else
                    pre = getLinePrefix()
            }
            _textPane?.let {
                it.styledDocument.insertString(_consoleOffset, pre + output, attr)
            }
        } catch (ex: BadLocationException) {
            ex.printStackTrace()
        }
    }

//    override fun getLinePrefix() = "[" + getUsername() + "@" + CLIFrame.getHostname() + " " + folder + "]$>"
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

    override fun fireOnRemove(fb: DocumentFilter.FilterBypass, offset: Int, length: Int) {
        //Nothing for now
    }

    override fun fireOnInsert(fb: DocumentFilter.FilterBypass, offset: Int, string: String?, attr: AttributeSet?) {
        //Nothing for now
    }

    override fun fireOnReplace(
        fb: DocumentFilter.FilterBypass,
        offset: Int,
        length: Int,
        text: String?,
        attrs: AttributeSet?
    ) {
        //Nothing for now
    }

    override fun repaint() {
        if (!isDisposed)
            repaint()
    }

    override fun hasToBeAlwaysOnTop() = configuration?.enabledAlwaysOnTop == true

    override fun append(toAppend: String, color: Color) {
        _textPane?.append(color, toAppend)
    }
    public override fun appendAnsi(toAppend: String, foregroundColor: Color) {
        _textPane?.appendANSI(toAppend)
    }

    override fun clear() {
        _textPane?.let {
            filter.clearText()
            filter.updateCaretPosition()
        }
    }

    override fun changePath(path: String) {
        middlePanel._tab?.currentPath = path
    }
    override fun getCurrentPath() = middlePanel._tab?.currentPath ?: "Not provided"

    final override fun changeTitle(title: String, toWindow: Boolean) {
        if (toWindow) {
            this.title = title
            topPanel.changeTitle(title)
        } else
            middlePanel.changeTitle(title)
    }

    fun getCurrentTab() = middlePanel._tab

}