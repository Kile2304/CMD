package it.cm.cmd.core.terminal.panel

import it.cm.ui.swing.predefined.component.DecorationBar
import org.pushingpixels.radiance.animation.api.Timeline
import org.pushingpixels.radiance.animation.api.TimelinePropertyBuilder
import org.pushingpixels.radiance.animation.api.TimelinePropertyBuilder.PropertySetter
import org.pushingpixels.radiance.animation.api.callback.TimelineCallbackAdapter
import org.pushingpixels.radiance.animation.api.ease.Spline
import java.awt.*
import java.util.concurrent.TimeUnit
import javax.swing.*


class CLITopPanel(
    root: JFrame
) : JPanel() {

    private val decorationBar: DecorationBar = DecorationBar(root, "/icons/cmd-terminal.png", Dimension(20, 20))
    private val menuBar: JPanel = createMenuBarPanel().apply {
        isVisible = false
    }

    init {
//        layout = GridLayout(2, 1)
        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 1.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        layout = GridBagLayout()
        add(decorationBar, gbc)
        gbc.gridy = 2
        add(menuBar, gbc)
    }

    private fun createMenuBarPanel(): JPanel = JPanel().apply {
        layout = GridLayout()
        border = BorderFactory.createMatteBorder(1, 0, 0, 0, Color(255, 255, 255, 30))
        add(createMenuBar())
        isVisible = true
    }

    private fun createMenuBar(): JMenuBar = JMenuBar().apply {
        add(
            JMenu("Settings").apply {
                add(
                    JMenuItem("Open").apply {
                        addActionListener {
                            showMenuBar()
                        }
                    }
                )
            }
        )
        add(
            JMenu("Exit").apply {
                addActionListener {
                    println("Arresto il processo")
                    System.exit(1)
                }
            }
        )
    }

    fun showMenuBar() {
        val toKeep = !menuBar.isVisible
        if (!menuBar.isVisible)
            menuBar.isVisible = true


        println("Preferred size: ${menuBar.preferredSize.height}, size: ${menuBar.size.height}")

        Timeline.builder(menuBar)
//            .addPropertyToInterpolate(
//                "location",
//                menuBar.location,
//                Point(0, if (toKeep) menuBar.y + menuBar.height else menuBar.y - menuBar.height)
//            )
            .addPropertyToInterpolate(
                "size"
                , menuBar.size
                , Dimension(menuBar.width, if (toKeep) menuBar.preferredSize.height else 0)
            )
            .setDuration(500)
            .setRepeatCount(1)
            .setRepeatBehavior(Timeline.RepeatBehavior.REVERSE)
            .addCallback(object : TimelineCallbackAdapter() {
                override fun onTimelineStateChanged(
                    oldState: Timeline.TimelineState,
                    newState: Timeline.TimelineState,
                    duration: Float,
                    timeLinePosition: Float
                ) {
                    if (newState == Timeline.TimelineState.DONE) {
                        if (!toKeep)
                            menuBar.isVisible = false
                        repaint()
                        revalidate()
                    }
                }
            }).setEase(Spline(0.7f))
            .play()
    }

    fun changeTitle(title: String) {
        decorationBar.changeTitle(title)
    }

}

class AnimatedBoxLayout(
    val container: JPanel
    , axis: Int
    , val component: JPanel
): BoxLayout(container, axis) {

    var isHidden = false

    override fun layoutContainer(target: Container?) {
        super.layoutContainer(target)
        if (isHidden) {
            val height = component.height
            for (component in container.components)
                if (component == this.component) {
                    component.bounds = Rectangle(0, component.y, component.width, height)
                } else if (component.y > this.component.y) {
                    component.bounds = Rectangle(0, component.y - height, component.width, component.height)
//                    container.bounds = Rectangle(container.x, container.y, container.width, container.height - height)
                }
        }
    }

    fun showComponent() {
        isHidden = false
        component.repaint()
        component.revalidate()
    }
    fun hideComponent() {
        isHidden = true
        component.repaint()
        component.revalidate()
    }

}
