package it.cm.cmd.core.terminal.listener.keyboard

import it.cm.cmd.core.terminal.frame.ITextPaneInputHandler
import it.cm.cmd.core.terminal.frame.ITextPaneOutputHandler
import it.cm.cmd.core.util.CircularIndex
import it.cm.common.ui.UIHandler
import it.cm.parser.LineParser
import java.io.File

class TabKey {

    private var filesFilteredIndex: CircularIndex = CircularIndex(0)
    private val filesFilteredNames = mutableListOf<String>()
    private val containSpace = Regex("\\s+")

    fun handleTab(
        textPaneInputHandler: ITextPaneInputHandler
        , textPaneOutputHandler: ITextPaneOutputHandler
        , sessionKey: String
    ) {
        val userInput = textPaneInputHandler.getCommand(false)
        val commands = LineParser.parseLine(userInput)

        //Caso: Prima volta che lo premo
        if (filesFilteredNames.isEmpty()) {
            val pathCommand = commands.lastOrNull() ?: ""
            getCommandFilePath(pathCommand, sessionKey).run {
                if (!exists()) {
                    val isFileNameInRegex = Regex("(?i)$name.*")
                    parentFile.listFiles { file ->
                        file.length() > 0 && file.name.matches(isFileNameInRegex)
                    }?.forEach { filesFilteredNames.add(it.name) }
                    filesFilteredIndex = CircularIndex(filesFilteredNames.size)
                }
            }
        }
        //Caso: L'ho già premuto e devo cambiare percorso
        if (filesFilteredNames.isNotEmpty()) {
            val path = commands.lastOrNull() ?: ""
            val currFileName = filesFilteredNames[filesFilteredIndex.getAndIncrease()]
            val slashLastIndex = path.lastIndexOf("/")
            val backSlashLastIndex = path.lastIndexOf("\\")
            val parentPath = when  {
                    backSlashLastIndex < slashLastIndex -> path.substring(0, slashLastIndex + 1)
                    backSlashLastIndex > slashLastIndex -> path.substring(0, backSlashLastIndex + 1)
                    else -> path
                }

            if (!hasSpaceInThePath(parentPath, currFileName)) {
                val currFileNameToAdd = if (path.contains("\"")) "$currFileName\"" else currFileName
                textPaneInputHandler.removeLastCommand(
                    getPathToDelete(path),
                    currFileNameToAdd
                )
            } else {
                textPaneInputHandler.clearUserInput()
                textPaneOutputHandler.printInplace("\"${parentPath}${currFileName}\"")
            }
        }
    }

    private fun hasSpaceInThePath(parentPath: String, newPath: String): Boolean =
        parentPath.contains(containSpace) || newPath.contains(containSpace)

    private fun getPathToDelete(path: String): String {
        val slashLastIndex = path.lastIndexOf("/")
        val backSlashLastIndex = path.lastIndexOf("\\")
        return if (backSlashLastIndex > slashLastIndex)
            path.substring(backSlashLastIndex + 1, path.length)
        else if (slashLastIndex > backSlashLastIndex)
            path.substring(slashLastIndex + 1, path.length)
        else
            path
    }

    private fun getCommandFilePath(path: String, sessionKey: String): File =
        File(path.replace("\"", "")).let {
            if (it.isAbsolute) it
            else File(UIHandler.getFrameFromSession(sessionKey)!!.currentPath, path.replace("\"", ""))
        }

    fun clear() {
        filesFilteredNames.clear()
    }

}