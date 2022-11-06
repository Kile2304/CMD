package it.cm.cmd.core.ui.cli.component

import it.cm.cmd.core.ui.cli._interface.ICaretListener
import it.cm.cmd.core.ui.cli._interface.ITextPaneOutputHandler
import it.cm.cmd.core.ui.util.HistoryHandler

class Tab(
    val textPane: CLITextPane
    , val sessionKey: String
    , val isOnlyOut: Boolean
    , private val iCaretListener: ICaretListener
    , private val textPaneOutputHandler: ITextPaneOutputHandler
    , val tabID: String
) {

    val history: HistoryHandler = HistoryHandler()

    var currentPath: String = System.getProperty("user.home")

    fun init() {
        if (!isOnlyOut) {
            textPaneOutputHandler.print("")
            iCaretListener._filter.updateCaretPosition()
        }
    }

}