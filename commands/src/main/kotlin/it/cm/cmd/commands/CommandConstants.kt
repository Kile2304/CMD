package it.cm.cmd.commands

import org.apache.commons.lang3.StringUtils
import java.util.regex.Pattern

object CommandConstants {

    val CMD_VARIABLES = mutableMapOf<String, String>()
    private val TEMPLATE_STRING = Pattern.compile("\\{(\\w+)\\}")

    fun replaceWithTemplate(command: String): String {
        val sb = StringBuffer()
        val pattern = TEMPLATE_STRING
        val matcher = pattern.matcher(command)
        while (matcher.find()) {
            val group = matcher.group(1)
            val value: String? = getVariableValue(group)
            if (StringUtils.isNotEmpty(value)) matcher.appendReplacement(sb, value)
        }
        return if (StringUtils.isNotEmpty(sb.toString())) sb.toString() else command
    }
    private fun getVariableValue(group: String): String? {
        return CommandConstants.CMD_VARIABLES[group]
    }

}