package it.cm.cmd.core.ui.cli._interface

interface ITextPaneInputHandler {

    fun getUserInput(): String
    fun clearText()
    fun getCommand(updateOldOffset: Boolean): String
    fun clearUserInput()
    fun updateCaretPosition()
    fun removeLastCommand(lastCommand: String, toInsert: String)
}