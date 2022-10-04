package it.cm.cmd.core.ui.cli

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GraphicsEnvironment

object Costants {

    val TOP_BACKGROUND = Color(0, 0, 0, 250)
    val CMD_BACKGROUND = Color(-0x6a000000, true)
    val CMD_DIMENSION = Dimension(1120, 630)

    object Fonts {

        val SOURCE_PRO_LIGHT = Font.createFont(
            Font.TRUETYPE_FONT,
            this::class.java.getResourceAsStream("/font/source_code_pro/SourceCodePro-Light.ttf")
        ).deriveFont(Font.BOLD, 17f)

        init {
            val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
            ge.registerFont(SOURCE_PRO_LIGHT)
        }

    }

}