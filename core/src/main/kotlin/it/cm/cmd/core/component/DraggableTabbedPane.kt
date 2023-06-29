package it.cm.cmd.core.component

import it.cm.cmd.core.terminal.component.model.Tab
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.*
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.SwingUtilities


/**
 * Problema quando si dragga l'ultimo tab.
 * Fare in modo che il "+" non sia draggabile
 * Fare in modo che venga creato il tab in "onreleased"
 */
class DraggableTabbedPane(
    val textPanes: MutableList<Tab>
    , private val transferBetweenWindow: (Component, Tab, Int)->Unit
) : JTabbedPane() {
    private val FLAVOR = DataFlavor(
        DataFlavor.javaJVMLocalObjectMimeType, NAME
    )
    private var m_isDrawRect = false
    private val m_lineRect: Rectangle2D = Rectangle2D.Double()
    private val m_lineColor = Color(0, 100, 255)
    var acceptor: TabAcceptor? = null

    var dragUpdate = false

    private fun getTabTransferData(a_event: DropTargetDropEvent): TabTransferData? {
        try {
            return a_event.transferable.getTransferData(FLAVOR) as TabTransferData
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getTabTransferData(a_event: DropTargetDragEvent): TabTransferData? {
        try {
            return a_event.transferable.getTransferData(FLAVOR) as TabTransferData
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getTabTransferData(a_event: DragSourceDragEvent): TabTransferData? {
        try {
            return a_event.dragSourceContext
                .transferable.getTransferData(FLAVOR) as TabTransferData
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    internal inner class TabTransferable(a_tabbedPane: DraggableTabbedPane?, a_tabIndex: Int) :
        Transferable {
        private var m_data: TabTransferData? = null

        init {
            m_data = TabTransferData(this@DraggableTabbedPane, a_tabIndex)
        }

        override fun getTransferData(flavor: DataFlavor): Any {
            return m_data!!
            // return DraggableTabbedPane.this;
        }

        override fun getTransferDataFlavors(): Array<DataFlavor> = arrayOf(FLAVOR)

        override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
            return flavor.humanPresentableName == NAME
        }
    }

    internal inner class TabTransferData {
        var tabbedPane: DraggableTabbedPane? = null
        var tabIndex = -1

        constructor() {}
        constructor(a_tabbedPane: DraggableTabbedPane?, a_tabIndex: Int) {
            tabbedPane = a_tabbedPane
            tabIndex = a_tabIndex
        }
    }

    private fun buildGhostLocation(a_location: Point): Point {
        var retval = Point(a_location)
        when (getTabPlacement()) {
            TOP -> {
                retval.y = 1
                retval.x -= s_glassPane.ghostWidth / 2
            }
            BOTTOM -> {
                retval.y = height - 1 - s_glassPane.ghostHeight
                retval.x -= s_glassPane.ghostWidth / 2
            }
            LEFT -> {
                retval.x = 1
                retval.y -= s_glassPane.ghostHeight / 2
            }
            RIGHT -> {
                retval.x = width - 1 - s_glassPane.ghostWidth
                retval.y -= s_glassPane.ghostHeight / 2
            }
        }
        retval = SwingUtilities.convertPoint(
            this@DraggableTabbedPane,
            retval, s_glassPane
        )
        return retval
    }

    internal inner class CDropTargetListener : DropTargetListener {
        override fun dragEnter(e: DropTargetDragEvent) {
            // System.out.println("DropTarget.dragEnter: " + DraggableTabbedPane.this);
            if (isDragAcceptable(e))
                e.acceptDrag(e.dropAction)
            else
                e.rejectDrag()
        }

        override fun dragExit(e: DropTargetEvent) {
            // System.out.println("DropTarget.dragExit: " + DraggableTabbedPane.this);
            m_isDrawRect = false
        }

        override fun dropActionChanged(e: DropTargetDragEvent) { }
        override fun dragOver(e: DropTargetDragEvent) {
            val data = getTabTransferData(e)
            if (getTabPlacement() == TOP || getTabPlacement() == BOTTOM)
                initTargetLeftRightLine(getTargetTabIndex(e.location), data)
            else
                initTargetTopBottomLine(getTargetTabIndex(e.location), data)
            repaint()
            if (hasGhost()) {
                s_glassPane.setPoint(buildGhostLocation(e.location))
                s_glassPane.repaint()
            }
        }

        override fun drop(a_event: DropTargetDropEvent) {
            // System.out.println("DropTarget.drop: " + DraggableTabbedPane.this);
            if (isDropAcceptable(a_event)) {
                convertTab(
                    getTabTransferData(a_event),
                    getTargetTabIndex(a_event.location)
                )
                a_event.dropComplete(true)
            } else {
                a_event.dropComplete(false)
            } // if-else
            m_isDrawRect = false
            repaint()
        }

        fun isDragAcceptable(e: DropTargetDragEvent): Boolean {
            val t = e.transferable ?: return false
            // if
            val flavor = e.currentDataFlavors
            if (!t.isDataFlavorSupported(flavor[0])) {
                return false
            } // if
            val data = getTabTransferData(e)
            if (this@DraggableTabbedPane === data!!.tabbedPane
                && data!!.tabIndex >= 0
            ) {
                return true
            } // if
            // if
            if (this@DraggableTabbedPane !== data!!.tabbedPane && acceptor != null) {
                return acceptor!!.isDropAcceptable(data!!.tabbedPane, data.tabIndex)
            } // if
            return false
        }

        fun isDropAcceptable(e: DropTargetDropEvent): Boolean {
            val t = e.transferable ?: return false
            // if
            val flavor = e.currentDataFlavors
            if (!t.isDataFlavorSupported(flavor[0])) {
                return false
            } // if
            val data = getTabTransferData(e)
            if (this@DraggableTabbedPane === data!!.tabbedPane
                && data!!.tabIndex >= 0
            ) {
                return true
            } // if
            // if
            if (this@DraggableTabbedPane !== data!!.tabbedPane && acceptor != null) {
                return acceptor!!.isDropAcceptable(data!!.tabbedPane, data.tabIndex)
            } // if
            return false
        }
    }

    private var m_hasGhost = true

    init {
        val dsl: DragSourceListener = object : DragSourceListener {
            override fun dragEnter(e: DragSourceDragEvent) {
                e.dragSourceContext.cursor = DragSource.DefaultMoveDrop
            }

            override fun dragExit(e: DragSourceEvent) {
                e.dragSourceContext.cursor = DragSource.DefaultMoveNoDrop
                m_lineRect.setRect(0.0, 0.0, 0.0, 0.0)
                m_isDrawRect = false
                s_glassPane.setPoint(Point(-1000, -1000))
                s_glassPane.repaint()
            }

            override fun dragOver(e: DragSourceDragEvent) {
                //e.getLocation()
                //This method returns a Point indicating the cursor location in screen coordinates at the moment
                val data = getTabTransferData(e)
                if (data == null) {
                    e.dragSourceContext.cursor = DragSource.DefaultMoveNoDrop
                    return
                } // if

                /*
                Point tabPt = e.getLocation();
                SwingUtilities.convertPointFromScreen(tabPt, DraggableTabbedPane.this);
                if (DraggableTabbedPane.this.contains(tabPt)) {
                    int targetIdx = getTargetTabIndex(tabPt);
                    int sourceIndex = data.getTabIndex();
                    if (getTabAreaBound().contains(tabPt)
                            && (targetIdx >= 0)
                            && (targetIdx != sourceIndex)
                            && (targetIdx != sourceIndex + 1)) {
                        e.getDragSourceContext().setCursor(
                                DragSource.DefaultMoveDrop);

                        return;
                    } // if

                    e.getDragSourceContext().setCursor(
                            DragSource.DefaultMoveNoDrop);
                    return;
                } // if
                */e.dragSourceContext.cursor = DragSource.DefaultMoveDrop
            }

            override fun dragDropEnd(e: DragSourceDropEvent) {
                m_isDrawRect = false
                m_lineRect.setRect(0.0, 0.0, 0.0, 0.0)
                // m_dragTabIndex = -1;
                if (hasGhost()) {
                    s_glassPane.isVisible = false
                    s_glassPane.m_draggingGhost = null
                }
            }

            override fun dropActionChanged(e: DragSourceDragEvent) {}
        }
        val dgl = DragGestureListener { e -> // System.out.println("dragGestureRecognized");
            val tabPt: Point = e.dragOrigin
            val dragTabIndex = indexAtLocation(tabPt.x, tabPt.y)
            if (dragTabIndex < 0) {
                return@DragGestureListener
            } // if
            initGlassPane(e.component, e.dragOrigin, dragTabIndex)
            try {
                e.startDrag(
                    DragSource.DefaultMoveDrop,
                    TabTransferable(this@DraggableTabbedPane, dragTabIndex), dsl
                )
            } catch (idoe: InvalidDnDOperationException) {
                idoe.printStackTrace()
            }
        }

        //dropTarget =
        DropTarget(
            this, DnDConstants.ACTION_COPY_OR_MOVE,
            CDropTargetListener(), true
        )
        DragSource().createDefaultDragGestureRecognizer(
            this,
            DnDConstants.ACTION_COPY_OR_MOVE, dgl
        )
        acceptor = object : TabAcceptor {
            override fun isDropAcceptable(a_component: DraggableTabbedPane?, a_index: Int): Boolean {
                return true
            }
        }
    }

    fun setPaintGhost(flag: Boolean) {
        m_hasGhost = flag
    }

    fun hasGhost(): Boolean {
        return m_hasGhost
    }

    /**
     * returns potential index for drop.
     * @param a_point point given in the drop site component's coordinate
     * @return returns potential index for drop.
     */
    private fun getTargetTabIndex(a_point: Point): Int {
        val isTopOrBottom = (getTabPlacement() == TOP
                || getTabPlacement() == BOTTOM)

        // if the pane is empty, the target index is always zero.
        if (tabCount == 0 || tabCount == 1)
            return 0
        for (i in 0 until tabCount-1) {
            val r = getBoundsAt(i)
            if (isTopOrBottom)
                r.setRect((r.x - r.width / 2).toDouble(), r.y.toDouble(), r.width.toDouble(), r.height.toDouble())
            else
                r.setRect(r.x.toDouble(), (r.y - r.height / 2).toDouble(), r.width.toDouble(), r.height.toDouble())
            if (r.contains(a_point))
                return i
        } // for
        val r = getBoundsAt(tabCount-1)
        if (isTopOrBottom) {
            val x = r.x + r.width / 2
            r.setRect(x.toDouble(), r.y.toDouble(), (width - x).toDouble(), r.height.toDouble())
        } else {
            val y = r.y + r.height / 2
            r.setRect(r.x.toDouble(), y.toDouble(), r.width.toDouble(), (height - y).toDouble())
        } // if-else
        return if (r.contains(a_point)) tabCount-1 else -1
    }

    /**
     * Probabilmente a causa di questo metodo e deò changelistener mi viene creato un tab in più
     */
    private fun convertTab(a_data: TabTransferData?, a_targetIndex: Int) {
        var a_targetIndex = a_targetIndex
        val source = a_data!!.tabbedPane
        val sourceIndex = a_data.tabIndex
        if (sourceIndex < 0)
            return

        val cmp: Component = source!!.getComponentAt(sourceIndex)
        val icon = source.getIconAt(sourceIndex)
        val tooltip = source.getToolTipTextAt(sourceIndex)
        val str = source.getTitleAt(sourceIndex)
        if (this !== source) {
            val tab = source.textPanes.removeAt(sourceIndex)
            source.removeTabAt(sourceIndex)
            source.selectedIndex = -1
            if (a_targetIndex == tabCount) {
                addTab(str, cmp)
            } else {
                if (a_targetIndex < 0) {
                    a_targetIndex = 0
                } // if
                insertTab(str, null, cmp, null, a_targetIndex)
            } // if

            transferBetweenWindow(cmp, tab, a_targetIndex)
//            textPanes.add(a_targetIndex, tab)
            //Ricostruire tab e listener
            selectedComponent = cmp
            // System.out.println("press="+sourceIndex+" next="+a_targetIndex);
            return
        } // if
        if (a_targetIndex < 0 || sourceIndex == a_targetIndex)
            //System.out.println("press="+prev+" next="+next);
            return

        dragUpdate = true
        selectedIndex = if (a_targetIndex == tabCount) {
            //System.out.println("last: press="+prev+" next="+next);
            source.remove(sourceIndex)
            addTab(str, icon, cmp, tooltip)
            tabCount - 1
        } else if (sourceIndex > a_targetIndex) {
            //System.out.println("   >: press="+prev+" next="+next);
            source.remove(sourceIndex)
            insertTab(str, icon, cmp, tooltip, a_targetIndex)
            val tab = textPanes.removeAt(sourceIndex)
            textPanes.add(a_targetIndex, tab)
            a_targetIndex
        } else {
            //System.out.println("   <: press="+prev+" next="+next);
            source.remove(sourceIndex)
            insertTab(str, icon, cmp, tooltip, a_targetIndex - 1)
            val tab = textPanes.removeAt(sourceIndex)
            textPanes.add(a_targetIndex-1, tab)
            a_targetIndex - 1
        }
        dragUpdate = false
    }

    private fun initTargetLeftRightLine(next: Int, a_data: TabTransferData?) {
        if (next < 0) {
            m_lineRect.setRect(0.0, 0.0, 0.0, 0.0)
            m_isDrawRect = false
            return
        } // if
        if (a_data!!.tabbedPane === this
            && (a_data!!.tabIndex == next
                    || next - a_data.tabIndex == 1)
        ) {
            m_lineRect.setRect(0.0, 0.0, 0.0, 0.0)
            m_isDrawRect = false
        } else if (tabCount == 0) {
            m_lineRect.setRect(0.0, 0.0, 0.0, 0.0)
            m_isDrawRect = false
            return
        } else if (next == 0) {
            val rect = getBoundsAt(0)
            m_lineRect.setRect(
                (-LINEWIDTH / 2).toDouble(),
                rect.y.toDouble(),
                LINEWIDTH.toDouble(),
                rect.height.toDouble()
            )
            m_isDrawRect = true
        } else if (next == tabCount) {
            val rect = getBoundsAt(tabCount - 1)
            m_lineRect.setRect(
                (rect.x + rect.width - LINEWIDTH / 2).toDouble(), rect.y.toDouble(),
                LINEWIDTH.toDouble(), rect.height.toDouble()
            )
            m_isDrawRect = true
        } else {
            val rect = getBoundsAt(next - 1)
            m_lineRect.setRect(
                (rect.x + rect.width - LINEWIDTH / 2).toDouble(), rect.y.toDouble(),
                LINEWIDTH.toDouble(), rect.height.toDouble()
            )
            m_isDrawRect = true
        }
    }

    private fun initTargetTopBottomLine(next: Int, a_data: TabTransferData?) {
        if (next < 0) {
            m_lineRect.setRect(0.0, 0.0, 0.0, 0.0)
            m_isDrawRect = false
            return
        } // if
        if (a_data!!.tabbedPane === this
            && (a_data!!.tabIndex == next
                    || next - a_data.tabIndex == 1)
        ) {
            m_lineRect.setRect(0.0, 0.0, 0.0, 0.0)
            m_isDrawRect = false
        } else if (tabCount == 0) {
            m_lineRect.setRect(0.0, 0.0, 0.0, 0.0)
            m_isDrawRect = false
            return
        } else if (next == tabCount) {
            val rect = getBoundsAt(tabCount - 1)
            m_lineRect.setRect(
                rect.x.toDouble(), (rect.y + rect.height - LINEWIDTH / 2).toDouble(),
                rect.width.toDouble(), LINEWIDTH.toDouble()
            )
            m_isDrawRect = true
        } else if (next == 0) {
            val rect = getBoundsAt(0)
            m_lineRect.setRect(
                rect.x.toDouble(),
                (-LINEWIDTH / 2).toDouble(),
                rect.width.toDouble(),
                LINEWIDTH.toDouble()
            )
            m_isDrawRect = true
        } else {
            val rect = getBoundsAt(next - 1)
            m_lineRect.setRect(
                rect.x.toDouble(), (rect.y + rect.height - LINEWIDTH / 2).toDouble(),
                rect.width.toDouble(), LINEWIDTH.toDouble()
            )
            m_isDrawRect = true
        }
    }

    private fun initGlassPane(c: Component, tabPt: Point, a_tabIndex: Int) {
        //Point p = (Point) pt.clone();
        rootPane.glassPane = s_glassPane
        if (hasGhost()) {
            val rect = getBoundsAt(a_tabIndex)
            var image = BufferedImage(
                c.width,
                c.height, BufferedImage.TYPE_INT_ARGB
            )
            val g = image.graphics
            c.paint(g)
            image = image.getSubimage(rect.x, rect.y, rect.width, rect.height)
            s_glassPane.m_draggingGhost = image
        } // if
        s_glassPane.setPoint(buildGhostLocation(tabPt))
        s_glassPane.isVisible = true
    }

    private val tabAreaBound: Rectangle
        get() {
            val lastTab = getUI().getTabBounds(this, tabCount - 1)
            return Rectangle(0, 0, width, lastTab.y + lastTab.height)
        }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if (m_isDrawRect) {
            val g2 = g as Graphics2D
            g2.paint = m_lineColor
            g2.fill(m_lineRect)
        } // if
    }

    interface TabAcceptor {
        fun isDropAcceptable(a_component: DraggableTabbedPane?, a_index: Int): Boolean
    }

    companion object {
        private const val LINEWIDTH = 3
        private const val NAME = "TabTransferData"
        private val s_glassPane = GhostGlassPane()
    }
}

internal class GhostGlassPane : JPanel() {
    private val m_composite: AlphaComposite
    private val m_location: Point = Point(0, 0)
    var m_draggingGhost: BufferedImage? = null

    init {
        isOpaque = false
        m_composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)
    }

    fun setPoint(a_location: Point) {
        m_location.x = a_location.x
        m_location.y = a_location.y
    }

    // if
    val ghostWidth: Int
        get() = if (m_draggingGhost == null) {
            0
        } else m_draggingGhost!!.getWidth(this) // if

    // if
    val ghostHeight: Int
        get() = if (m_draggingGhost == null) {
            0
        } else m_draggingGhost!!.getHeight(this) // if

    override fun paintComponent(g: Graphics) {
        if (m_draggingGhost == null) {
            return
        } // if
        val g2 = g as Graphics2D
        g2.composite = m_composite
        g2.drawImage(m_draggingGhost, m_location.getX().toInt(), m_location.getY().toInt(), null)
    }

}