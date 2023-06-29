package it.cm.cmd.core.util

interface IHistory {

    val previousCommand: String?
    val nextCommand: String?

    fun addCommand(toAdd: String)

    operator fun plusAssign(command: String)

}