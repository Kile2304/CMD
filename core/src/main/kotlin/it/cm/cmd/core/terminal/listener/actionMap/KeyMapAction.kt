package it.cm.cmd.core.terminal.listener.actionMap

import it.cm.common.cmd.CmdCommon
import it.cm.common.ui.UIHandler
import it.cm.parser.CommandParser
import it.cm.parser.CommandRunner
import it.cm.cmd.core.terminal.frame.TerminalFrame
import it.cm.cmd.core.terminal.frame.ITextPaneInputHandler
import it.cm.cmd.core.terminal.frame.ITextPaneOutputHandler
import it.cm.cmd.core.terminal.listener.CLIKeyListener
import it.cm.cmd.core.terminal.panel.terminal_panel.TabbedTerminalPanel
import it.cm.cmd.core.util.ReaderThreadKotlin
import java.awt.event.ActionEvent
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.AbstractAction
import javax.swing.SwingWorker

abstract class KeyMapAction(
    private val sessionKey: String
    , private val textPaneInputHandler: ITextPaneInputHandler
    , private val textPaneOutputHandler: ITextPaneOutputHandler
) {
    private var process: Process? = null

    val STOP_ACTION = object: AbstractAction() {
        override fun actionPerformed(e: ActionEvent?) {
            process?.destroyForcibly()
        }
    }

    fun runCommand(userInput: String) {
        CommandParser.commandFromLine(userInput)?.let {
            CommandRunner.runCommand(it, sessionKey)
            textPaneOutputHandler.printNewLine()
            textPaneInputHandler.updateCaretPosition()
        } ?: runStandardCMDTask(userInput)
    }

    private fun runStandardCMDTask(userInput: String) {   //Gestire la disabilitazione dell'user input
        CLIKeyListener.IS_RUNNING.set(true)
        textPaneOutputHandler.printInplace("\n")
//        tempo = System.currentTimeMillis()
        process = CmdCommon.execCMDCommand(UIHandler.getFrameFromSession(sessionKey)!!.currentPath, userInput)
        val tab = ((UIHandler.getFrameFromSession(sessionKey) as TerminalFrame).middlePanel as TabbedTerminalPanel)._selectedTab
        val infos: SwingWorker<Unit, String> =
            ReaderThreadKotlin(
                BufferedReader(InputStreamReader(process!!.inputStream, Charsets.UTF_8))
                , System.out
                , tab!!
            ) {
                process = null
                textPaneOutputHandler.print("")
                textPaneInputHandler.updateCaretPosition()
                CLIKeyListener.IS_RUNNING.set(false)
            }
        infos.execute()
//        infos.start()
//        val onExit: CompletableFuture<Process> = process!!.onExit()
//        onExit.whenComplete { process: Process?, _: Throwable? ->
////            println("Tempo: ${System.currentTimeMillis() - tempo}")
////            infos.join()
////            errors.join()
//            this.process = null
//            textPaneOutputHandler.print("")
//            process?.destroyForcibly()
//            textPaneInputHandler.updateCaretPosition()
//            CLIKeyListener.IS_RUNNING.set(false)
//        }
    }


}