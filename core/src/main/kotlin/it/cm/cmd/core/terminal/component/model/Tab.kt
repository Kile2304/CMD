package it.cm.cmd.core.terminal.component.model

import it.cm.cmd.core.terminal.frame.ICaretListener
import it.cm.cmd.core.terminal.frame.ITextPaneOutputHandler
import it.cm.cmd.core.terminal.component.TerminalTextPane
import it.cm.cmd.core.util.HistoryHandler

class Tab(
    val textPane: TerminalTextPane
    , val sessionKey: String
    , private val iCaretListener: ICaretListener
    , private val textPaneOutputHandler: ITextPaneOutputHandler
    , val tabID: String
) {

    val history: HistoryHandler = HistoryHandler()

    var currentPath: String = System.getProperty("user.home")

    fun init() {
        textPaneOutputHandler.print("")
        iCaretListener._filter.updateCaretPosition()
    }

    fun appendNewLine() {
        textPane.appendANSI("\n")
    }

    fun appendANSI(toAppend: String) {
        textPane.appendANSI(toAppend)
        //TODO: Da controllare se devo aggiornare la posizione del caret
    }

    fun clone(
        sessionKey: String
        , iCaretListener: ICaretListener
        , textPaneOutputHandler: ITextPaneOutputHandler
    ): Tab = Tab(
        textPane
        , sessionKey
        , iCaretListener
        , textPaneOutputHandler
        , tabID
    )


}