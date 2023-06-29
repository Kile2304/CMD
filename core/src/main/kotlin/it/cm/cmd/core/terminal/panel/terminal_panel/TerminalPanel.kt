package it.cm.cmd.core.terminal.panel.terminal_panel

import it.cm.cmd.core.terminal.frame.ICaretListener
import it.cm.cmd.core.terminal.frame.ITextPaneOutputHandler
import it.cm.cmd.core.terminal.component.TerminalTextPane
import it.cm.cmd.core.util.HistoryHandler
import javax.swing.text.AttributeSet

class TerminalPanel(
    private val sessionKey: String
    , private val iCaretListener: ICaretListener
    , private val textPaneOutputHandler: ITextPaneOutputHandler,
): AbstractTerminalPanel(sessionKey, iCaretListener, textPaneOutputHandler) {

    override val attr: AttributeSet
        get() = TODO("Not yet implemented")
    override var currentPath: String = ""
    override val _textPane: TerminalTextPane = textPane(true)
    override fun history(): HistoryHandler = HistoryHandler()

    override fun appendAnsi(toAppend: String, tabID: String) {
        _textPane.appendANSI(toAppend)
    }

    init {
        add(scrollPane(_textPane))
    }

}