package it.cm.cmd.core.terminal.panel.terminal_panel

import it.cm.cmd.core.constant.Costants
import it.cm.cmd.core.terminal.frame.ICaretListener
import it.cm.cmd.core.terminal.frame.ITextPaneOutputHandler
import it.cm.cmd.core.terminal.component.TerminalTextPane
import it.cm.cmd.core.util.HistoryHandler
import java.awt.GridLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.net.InetAddress
import java.net.UnknownHostException
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants
import javax.swing.text.AttributeSet

abstract class AbstractTerminalPanel(
    private val sessionkey: String
    , private val iCaretListener: ICaretListener
    , private val textPaneOutputHandler: ITextPaneOutputHandler //TODO Temporaneo, logica da spostare qui
) : JPanel() {

    protected abstract val attr: AttributeSet
    abstract var currentPath: String
    abstract val _textPane: TerminalTextPane?
    abstract fun history(): HistoryHandler
    private val _consoleOffset: Int
        get() = (_textPane?.let { it.styledDocument.endPosition.offset - 1 }) ?: 0

    init {
        layout = GridLayout(1, 1)
        isOpaque = false
        size = Costants.CMD_DIMENSION
    }

    fun print(processed: String): Unit = print(processed, attr, false)
    //Aggiungere il bold e rimuoverlo
    fun printInplace(toPrint: String): Unit = print(toPrint, attr, true)

    fun print(output: String, style: AttributeSet, inplace: Boolean) {
        val toPrint =
            if (!inplace)  {
                if (output.endsWith("\n"))
                    output + getLinePrefix()
                else
                    getLinePrefix() + output
            } else
                output
        _textPane?.styledDocument?.insertString(_consoleOffset, toPrint, attr)
    }

    fun disableTextPane() {
        _textPane?.let {
            it.isFocusable = false
            it.addKeyListener(object: KeyListener {
                override fun keyTyped(e: KeyEvent): Unit = e.consume()
                override fun keyPressed(e: KeyEvent): Unit = e.consume()
                override fun keyReleased(e: KeyEvent): Unit = e.consume()
            })
        }
    }

    /**
     * Creo lo JScrollPane
     */
    protected fun scrollPane(textPane: TerminalTextPane) =
        JScrollPane(textPane).apply {
            size = Costants.CMD_DIMENSION
            background = Costants.CMD_BACKGROUND
            isOpaque = false
            viewport.background = Costants.CMD_BACKGROUND
            viewport.isOpaque = false
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            border = null
        }

    abstract fun appendAnsi(toAppend: String, tabID: String = "")

    /**
     * Creo il TerminalTextPane (JTextPane)
     */
    protected fun textPane(isOnlyOut: Boolean) =
        TerminalTextPane(
            sessionkey = sessionkey
            , textPaneInputHandler = iCaretListener._filter
            , textPaneOutputHandler = textPaneOutputHandler
            , isOnlyOut = isOnlyOut
            , iCaretListener = iCaretListener
        )

    private fun getLinePrefix() = "[${getUsername()}@${getHostname()} ${currentPath}]$>"

    private fun getUsername() = System.getProperty("user.name")
    private fun getHostname() =
        try {
            InetAddress.getLocalHost().hostName
        } catch (ex: UnknownHostException) {
            "~"
        }

}