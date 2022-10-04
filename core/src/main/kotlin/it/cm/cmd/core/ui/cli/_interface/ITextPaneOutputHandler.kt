package it.cm.cmd.core.ui.cli._interface

interface ITextPaneOutputHandler {

    fun print(toPrint: String)
    fun printNewLine()
    fun printInplace(toPrint: String)


}