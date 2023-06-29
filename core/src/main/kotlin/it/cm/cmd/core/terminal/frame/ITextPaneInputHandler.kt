package it.cm.cmd.core.terminal.frame

interface ITextPaneInputHandler {

    fun getUserInput(): String
    fun clearText()
    fun getCommand(updateOldOffset: Boolean): String
    fun clearUserInput()
    fun updateCaretPosition()
    fun removeLastCommand(lastCommand: String, toInsert: String)
}