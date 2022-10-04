package it.cm.cmd.core.ui.cli.panel

import it.cm.ui.swing.predefined.component.DecorationBar
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


class CLITopPanel(
    private val root: JFrame
) : JPanel() {

    private lateinit var decorationBar: DecorationBar

    init {
        layout = GridLayout()
        decorationBar = DecorationBar(root, "/icons/cmd-terminal.png", Dimension(20, 20))
        add(decorationBar)
    }

    fun changeTitle(title: String) {
        decorationBar.changeTitle(title)
    }

}

