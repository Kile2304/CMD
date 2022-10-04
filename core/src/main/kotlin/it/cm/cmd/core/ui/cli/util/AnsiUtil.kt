package it.cm.cmd.core.ui.cli.util

import java.awt.Color
import javax.swing.text.MutableAttributeSet
import javax.swing.text.StyleConstants

class AnsiUtil {

    companion object {

        val ANSI_COLOR_AND_STYLE_REGEX = Regex("\u001b\\[(?:(\\d+);?)+m")
        val NUMBE_REGEX = Regex("(\\d+)")

        val D_Black = Color.getHSBColor(0.000f, 0.000f, 0.000f)
        val D_Red = Color.getHSBColor(0.000f, 1.000f, 0.502f)
        val D_Blue = Color.getHSBColor(0.667f, 1.000f, 0.502f)
        val D_Magenta = Color.getHSBColor(0.833f, 1.000f, 0.502f)
        val D_Green = Color.getHSBColor(0.333f, 1.000f, 0.502f)
        val D_Yellow = Color.getHSBColor(0.167f, 1.000f, 0.502f)
        val D_Cyan = Color.getHSBColor(0.500f, 1.000f, 0.502f)
        val D_White = Color.getHSBColor(0.000f, 0.000f, 0.753f)
        val B_Black = Color.getHSBColor(0.000f, 0.000f, 0.502f)
        val B_Red = Color.getHSBColor(0.000f, 1.000f, 1.000f)
        val B_Blue = Color.getHSBColor(0.667f, 1.000f, 1.000f)
        val B_Magenta = Color.getHSBColor(0.833f, 1.000f, 1.000f)
        val B_Green = Color.getHSBColor(0.333f, 1.000f, 1.000f)
        val B_Yellow = Color.getHSBColor(0.167f, 1.000f, 1.000f)
        val B_Cyan = Color.getHSBColor(0.500f, 1.000f, 1.000f)
        val B_White = Color.getHSBColor(0.000f, 0.000f, 1.000f)
        val cReset = Color.getHSBColor(0.000f, 0.000f, 1.000f)

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

}