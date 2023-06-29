package it.cm.cmd.core.component

import java.awt.Container
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.JComponent
import javax.swing.JFrame

class FrameDragger(private val target: JComponent) : MouseListener, MouseMotionListener {

    private var start_drag: Point? = null
    private var start_loc: Point? = null
    private fun getScreenLocation(e: MouseEvent): Point {
        val cursor = e.point
        val target_location = target.locationOnScreen
        return Point((target_location.getX() + cursor.getX()).toInt(), (target_location.getY() + cursor.getY()).toInt())
    }

    override fun mouseClicked(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}
    override fun mousePressed(e: MouseEvent) {
        start_drag = getScreenLocation(e)
        start_loc = getFrame(target).location
    }

    override fun mouseReleased(e: MouseEvent) {}
    override fun mouseDragged(e: MouseEvent) {
        val current = getScreenLocation(e)
        val offset = Point(
            current.getX().toInt() - start_drag!!.getX().toInt(),
            current.getY().toInt() - start_drag!!.getY().toInt()
        )
        val frame: JFrame = getFrame(target)
        val new_location = Point(
            (start_loc!!.getX() + offset.getX()).toInt(), (start_loc!!
                .getY() + offset.getY()).toInt()
        )
        frame.location = new_location
    }

    override fun mouseMoved(e: MouseEvent) {}

    companion object {
        fun getFrame(target: Container): JFrame {
            return if (target is JFrame) target else getFrame(target.parent)
        }
    }
}