package it.cm.cmd.core.terminal.frame

import it.cm.cmd.core.terminal.component.TerminalTextPane

interface IBaseCLI {

    fun getLinePrefix(): String
    fun printNewLine()
    val _consoleOffset: Int
    val _textPane: TerminalTextPane?

}