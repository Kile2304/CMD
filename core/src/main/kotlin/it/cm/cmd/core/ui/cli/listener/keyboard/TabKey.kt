package it.cm.cmd.core.ui.cli.listener.keyboard

import it.cm.cmd.core.ui.cli._interface.ITextPaneInputHandler
import it.cm.cmd.core.ui.cli._interface.ITextPaneOutputHandler
import it.cm.cmd.core.ui.util.CircularIndex
import it.cm.common.ui.UIHandler
import it.cm.parser.LineParser
import java.io.File

class TabKey {

    private var filesFilteredIndex: CircularIndex = CircularIndex(0)
    private val filesFilteredNames = mutableListOf<String>()

    fun handleTab(
        textPaneInputHandler: ITextPaneInputHandler, textPaneOutputHandler: ITextPaneOutputHandler, sessionKey: String
    ) {
        val userInput = textPaneInputHandler.getCommand(false)
        val commands = LineParser.parseLine(userInput)
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
        if (filesFilteredNames.isNotEmpty()) {
            val path = commands.lastOrNull() ?: ""
            val currFileName = filesFilteredNames[filesFilteredIndex.getAndIncrease()]
            val slashLastIndex = path.lastIndexOf("/")
            val backSlashLastIndex = path.lastIndexOf("\\")
            val parentPath = if (backSlashLastIndex > slashLastIndex)
                path.substring(0, backSlashLastIndex + 1)
            else if (slashLastIndex > backSlashLastIndex)
                path.substring(0, slashLastIndex + 1)
            else
                path

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

    private val containSpace = Regex("\\s+")

    private fun hasSpaceInThePath(parentPath: String, newPath: String) =
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

    private fun getCommandFilePath(path: String, sessionKey: String): File {
        val toReturn = File(path.replace("\"", ""))
        return if (toReturn.isAbsolute)
            toReturn
        else
            File(UIHandler.getFrameFromSession(sessionKey).currentPath, path.replace("\"", ""))
    }

    fun clear() {
        filesFilteredNames.clear()
    }

}