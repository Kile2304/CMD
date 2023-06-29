package it.cm.cmd.core.terminal.frame

import it.cm.cmd.core.terminal.listener.CLIDocumentFilter

interface ICaretListener {

    val _filter: CLIDocumentFilter
    val _pasting: Boolean

}