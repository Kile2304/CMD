package it.cm.cmd.core.ui.cli._interface

import it.cm.cmd.core.ui.cli.listener.CLIDocumentFilter

interface ICaretListener {

    val _filter: CLIDocumentFilter
    val _pasting: Boolean

}