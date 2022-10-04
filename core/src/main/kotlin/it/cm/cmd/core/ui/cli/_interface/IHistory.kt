package it.cm.cmd.core.ui.cli._interface

interface IHistory {

    val previousCommand: String?
    val nextCommand: String?

    fun addCommand(toAdd: String)

//    public getPreviousCommand()
}