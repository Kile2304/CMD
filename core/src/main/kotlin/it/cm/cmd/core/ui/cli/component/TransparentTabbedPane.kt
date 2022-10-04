package it.cm.cmd.core.ui.cli.component

import it.cm.cmd.core.ui.cli.Costants.CMD_BACKGROUND
import java.awt.Graphics
import javax.swing.JTabbedPane


class TransparentTabbedPane : JTabbedPane() {

    private val alpha = 1f

    init {
        isOpaque = false
        background = CMD_BACKGROUND
    }

    override fun paintComponent(g: Graphics) {
//        super.paintComponent(g)
        g.color = CMD_BACKGROUND
        g.fillRect(0, 0, width, height)
        super.paintBorder(g)
    }

}