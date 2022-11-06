package it.cm.cmd.core.ui.cli.util

import java.awt.Color
import javax.swing.text.MutableAttributeSet
import javax.swing.text.StyleConstants

object AnsiUtil {

    val ANSI_COLOR_AND_STYLE_REGEX = Regex("\u001b\\[(?:(\\d+);?)+m")
    val NUMBE_REGEX = Regex("(\\d+)")

    val D_Black = Color(12,12,12)
    val D_Red = Color(197,15,31)
    val D_Blue = Color(0, 55, 218)
    val D_Magenta = Color(136,23,152)
    val D_Green = Color(19,161,14)
    val D_Yellow = Color(193,156,0)
    val D_Cyan = Color(58,150,221)
    val D_White = Color(204,204,204)
    val B_Black = Color(118,118,118)
    val B_Red = Color(231,72,86)
    val B_Blue = Color(59,120,255)
    val B_Magenta = Color(180,0,158)
    val B_Green = Color(22,198,12)
    val B_Yellow = Color(249,241,165)
    val B_Cyan = Color(97,214,214)
    val B_White = Color(242,242,242)
    val cReset = B_White

    val ANSI_BLACK = "\u001B[30m"
    val ANSI_RED = "\u001B[31m"
    val ANSI_GREEN = "\u001B[32m"
    val ANSI_YELLOW = "\u001B[33m"
    val ANSI_BLUE = "\u001B[34m"
    val ANSI_MAGENTA = "\u001B[35m"
    val ANSI_CYAN = "\u001B[36m"
    val ANSI_WHITE = "\u001B[37m"

    val ANSI_BG_BLACK = "\u001B[40m"
    val ANSI_BG_RED = "\u001B[41m"
    val ANSI_BG_GREEN = "\u001B[42m"
    val ANSI_BG_YELLOW = "\u001B[43m"
    val ANSI_BG_BLUE = "\u001B[44m"
    val ANSI_BG_MAGENTA = "\u001B[45m"
    val ANSI_BG_CYAN = "\u001B[46m"
    val ANSI_BG_WHITE = "\u001B[47m"

    val ANSI_BOLD = "\u001B[1m"
    val ANSI_UNDERLINE = "\u001B[2m"

    val ANSI_RESET = "\u001B[0m"

    const val NEW_LINE = "\u001B[0K"  //E' uno zero, non una o

    fun isReset(value: Int) = value == 0
    fun getForegroundFromNumber(colorValue: Int) =
        when (colorValue) {
            30, 90 -> D_Black
            31, 91 -> D_Red
            32, 92 -> D_Green
            33, 93 -> D_Yellow
            34, 94 -> D_Blue
            35, 95 -> D_Magenta
            36, 96 -> D_Cyan
            37, 97 -> D_White
            else -> null
        }
    fun getBackgroundFromNumber(colorValue: Int) =
        when (colorValue) {
            40, 100 -> D_Black
            41, 101 -> D_Red
            42, 102 -> D_Green
            43, 103 -> D_Yellow
            44, 104 -> D_Blue
            45, 105 -> D_Magenta
            46, 106 -> D_Cyan
            47, 107 -> D_White
            else -> null
        }
    fun getStyleFromNumber(fontValue: Int) =
        when(fontValue) {
            1, in 90..97, in 100..107 -> 1
            4 -> 2
            7 -> 3
            else -> null
        }
    fun ansiFormatTojava(attr: MutableAttributeSet, format: StringBuilder) {
       extractValueFromAnsiFormat(format).forEach { elaborateValue(attr, it) }
    }
    fun ansiFormatTojava(attr: MutableAttributeSet, format: String) {
        format.split(';').forEach { elaborateValue(attr, it.toInt()) }
//            extractValueFromAnsiFormat(format.groupValues[1]).forEach { elaborateValue(attr, it) }
    }

    private fun elaborateValue(attr: MutableAttributeSet, value: Int) {
        if (isReset(value))
            reset(attr)
        else {
            getForegroundFromNumber(value)?.let {
                StyleConstants.setForeground(attr, it)
            } ?: getBackgroundFromNumber(value)?.let {
                StyleConstants.setBackground(attr, it)
            }
            getStyleFromNumber(value)?.let {
                if (it == 1)
                    StyleConstants.setBold(attr,  true)
                else if (it == 2)
                    StyleConstants.setUnderline(attr, true)
            }
        }
    }

    private fun extractValueFromAnsiFormat(format: StringBuilder): List<Int> {
        val toReturn = mutableListOf<Int>()

        if (format.toString().matches(ANSI_COLOR_AND_STYLE_REGEX)) {
            val matches = NUMBE_REGEX.findAll(format.toString())
            toReturn.addAll(matches.map { Integer.parseInt(it.groupValues[1]) }.toList())
        }

        return toReturn
    }
    private fun extractValueFromAnsiFormat(format: String): List<Int> {
        val toReturn = mutableListOf<Int>()

        if (format.matches(ANSI_COLOR_AND_STYLE_REGEX)) {
            val matches = NUMBE_REGEX.findAll(format)
            toReturn.addAll(matches.map { Integer.parseInt(it.groupValues[1]) }.toList())
        }

        return toReturn
    }
    //[?25l hide cursor
    //[?25h show cursor

    fun reset(attr: MutableAttributeSet) {
        StyleConstants.setBold(attr,  false)
        StyleConstants.setUnderline(attr,  false)
        //TEMP
        StyleConstants.setForeground(attr, cReset)
        StyleConstants.setBackground(attr, Color(0, 0, 0, 0))
    }

    fun temporaryNullify(text: String) =
        text.replace("\u001B[?25l", "").replace("\u001B[?25h", "")


}