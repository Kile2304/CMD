package it.cm.cmd.commands.runner

import it.cm.cmd.commands.CommandSettings
import it.cm.common.ui.UIHandler
import it.cm.cmd.commands.declared.ETCommand
import it.cm.common.cmd.CmdCommon
import it.cm.common.utils.ReaderThread
import it.cm.parser.BaseArgument
import it.cm.parser.CommandRunner
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture

class ETRunner : CommandRunner() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(arg: BaseArgument, sessionKey: String) {
        val etCommand = (arg as ETCommand)
        val eventCreator = File(CommandSettings.INSTALLATION_FOLDER, "script/events/eventCreator.bat")

        val frame = UIHandler.getFrameFromSession(sessionKey)
        val process = CmdCommon.execCMDCommand(
            frame!!.currentPath
            , "${eventCreator.absolutePath} ${etCommand.type} ${etCommand.path}")
        val infos: Thread =
            ReaderThread(
                InputStreamReader(process!!.inputStream, Charsets.UTF_8)
                , System.out
                , frame
            )
        infos.start()
        val onExit: CompletableFuture<Process> = process.onExit()
        onExit.whenComplete { process: Process?, _: Throwable? ->
//            println("Tempo: ${System.currentTimeMillis() - tempo}")
            infos.join()
//            errors.join()
            process?.destroyForcibly()
        }

    }

}