package it.cm.cmd.core.ui.cli._interface

import it.cm.cmd.core.ui.cli.component.CLITextPane
import javax.swing.text.AttributeSet
import javax.swing.text.DocumentFilter

interface IBaseCLI {

    fun getLinePrefix(): String
    fun printNewLine()
    val _consoleOffset: Int
    fun fireOnRemove(fb: DocumentFilter.FilterBypass, offset: Int, length: Int)
    fun fireOnInsert(fb: DocumentFilter.FilterBypass, offset: Int, string: String?, attr: AttributeSet?)
    fun fireOnReplace(fb: DocumentFilter.FilterBypass, offset: Int, length: Int, text: String?, attrs: AttributeSet?)
    val _textPane: CLITextPane?

}