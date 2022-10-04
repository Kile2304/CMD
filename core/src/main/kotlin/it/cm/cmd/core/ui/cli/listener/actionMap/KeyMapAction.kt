package it.cm.cmd.core.ui.cli.listener.actionMap

import it.cm.common.cmd.CmdCommon
import it.cm.common.ui.UIHandler
import it.cm.parser.BaseArgument
import it.cm.parser.CommandParser
import it.cm.parser.CommandRunner
import it.cm.cmd.core.ui.cli.CLIFrame2
import it.cm.cmd.core.ui.cli._interface.ITextPaneInputHandler
import it.cm.cmd.core.ui.cli._interface.ITextPaneOutputHandler
import it.cm.cmd.core.ui.cli.listener.CLIKeyListener
import it.cm.cmd.core.ui.cli.util.ReaderThreadKotlin
import java.awt.event.ActionEvent
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture
import javax.swing.AbstractAction

abstract class KeyMapAction(
    private val sessionKey: String
    , private val textPaneInputHandler: ITextPaneInputHandler
    , private val textPaneOutputHandler: ITextPaneOutputHandler
) {
    private var process: Process? = null

    val STOP_ACTION = object: AbstractAction() {
        override fun actionPerformed(e: ActionEvent?) {
            process?.let { pr ->
                pr.destroyForcibly()
            }
        }
    }

    fun runCommand(userInput: String) {
        val command: BaseArgument? =
            CommandParser.commandFromLine(userInput)
        if (command != null) {
            CommandRunner.runCommand(command, sessionKey)
            textPaneOutputHandler.printNewLine()
            textPaneInputHandler.updateCaretPosition()
        } else
            runStandardCMDTask(userInput)
    }

    private fun runStandardCMDTask(userInput: String) {   //Gestire la disabilitazione dell'user input
        CLIKeyListener.IS_RUNNING.set(true)
        textPaneOutputHandler.printInplace("\n")
//        tempo = System.currentTimeMillis()
        process = CmdCommon.execCMDCommand(UIHandler.getFrameFromSession(sessionKey).currentPath, userInput)
        val infos: Thread =
            ReaderThreadKotlin(
                InputStreamReader(process!!.inputStream, Charsets.UTF_8)
                , System.out
                , UIHandler.getFrameFromSession(sessionKey) as CLIFrame2
            )
        infos.start()
        val onExit: CompletableFuture<Process> = process!!.onExit()
        onExit.whenComplete { process: Process?, _: Throwable? ->
//            println("Tempo: ${System.currentTimeMillis() - tempo}")
            infos.join()
//            errors.join()
            this.process = null
            textPaneOutputHandler.print("")
            process?.destroyForcibly()
            textPaneInputHandler.updateCaretPosition()
            CLIKeyListener.IS_RUNNING.set(false)
        }
    }


}