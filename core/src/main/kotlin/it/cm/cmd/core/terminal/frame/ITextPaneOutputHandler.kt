package it.cm.cmd.core.terminal.frame

interface ITextPaneOutputHandler {

    fun print(toPrint: String)
    fun printNewLine()
    fun printInplace(toPrint: String)


}