package it.cm.cmd.commands.runner

import it.cm.parser.BaseArgument
import it.cm.parser.CommandRunner
import it.cm.cmd.commands.CommandConstants
import it.cm.cmd.commands.CommandSettings
import it.cm.cmd.commands.declared.JSetCommand
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class JSetRunner : CommandRunner() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(arg: BaseArgument, sessionKey: String) {
        val setCommand = (arg as JSetCommand)

        val (name, value, javaVariable) = setCommand

        if (!javaVariable) {
            val oldValue = CommandConstants.CMD_VARIABLES[name]
            CommandConstants.CMD_VARIABLES[name] = value
            writeVariableIntoFile(oldValue, name, value)
        } else {
            //Fai cose custom per esempio customizzare il percorso di default delle shortcut
        }
    }

    @Synchronized
    fun writeVariableIntoFile(oldValue: String?, name: String, value: String) {
        if (StringUtils.isEmpty(oldValue)) {
            try {
                Files.newBufferedWriter(CommandSettings.getVariableFile().toPath(), StandardOpenOption.APPEND).use { bw ->
                    bw.append(name + "=" + value + System.lineSeparator())
                }
            } catch (e: IOException) {
                log.error("Error: ", e)
            }
        } else {
            val temp = File("${CommandSettings.getVariableFile().absolutePath}.temp")
            try {
                Files.newBufferedReader(CommandSettings.getVariableFile().toPath()).use { br ->
                    Files.newBufferedWriter(temp.toPath()).use { bw ->
                        var line: String? = null
                        while (br.readLine().also { line = it } != null) {
                            var toWrite = line
                            if (line!!.startsWith("$name=")) toWrite = "$name=$value"
                            bw.append(toWrite + System.lineSeparator())
                        }
                    }
                }
            } catch (e: IOException) {
                log.error("Error: ", e)
            }
            CommandSettings.getVariableFile().delete()
            temp.renameTo(CommandSettings.getVariableFile())
        }
    }


}