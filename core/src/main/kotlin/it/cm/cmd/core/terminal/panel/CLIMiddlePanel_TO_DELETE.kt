package it.cm.cmd.core.terminal.panel

import it.cm.cmd.core.constant.Costants
import it.cm.cmd.core.constant.Costants.CMD_BACKGROUND
import it.cm.cmd.core.constant.Costants.CMD_DIMENSION
import it.cm.cmd.core.terminal.frame.ICaretListener
import it.cm.cmd.core.terminal.frame.ITextPaneOutputHandler
import it.cm.cmd.core.terminal.component.TerminalTextPane
import it.cm.cmd.core.component.DraggableTabbedPane
import it.cm.cmd.core.terminal.component.model.Tab
import it.cm.cmd.core.terminal.listener.CLICaretListener
import java.awt.Color
import java.awt.Container
import java.awt.Graphics
import java.awt.GridLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.lang.UnsupportedOperationException
import java.util.*
import javax.swing.*
import javax.swing.text.AbstractDocument
import javax.swing.text.DefaultCaret


class CLIMiddlePanel_TO_DELETE(
    private val sessionkey: String
    , private val iCaretListener: ICaretListener
    , private val textPaneOutputHandler: ITextPaneOutputHandler
    , private val isOnlyOut: Boolean
    , private val contentPane: Container
): JPanel() {

    private var tabbedPane: JTabbedPane? = null
    val tabs: MutableList<Tab> = mutableListOf()
    var selectedTab: Tab? = null
        get() = if (tabs.size > 0) tabs[tabbedPane?.selectedIndex ?: 0] else null
        private set

    var selectedTabPane: TerminalTextPane? = null
        get() = if (tabs.size > 0) tabs[tabbedPane?.selectedIndex ?: 0].textPane else null
        private set

    private var addedTab = false
    private var isNotUpdatedByClick = false


    init {
//        UIManager.put("TabbedPane.selected", Color.RED);
        layout = GridLayout(1, 1)
        isOpaque = false
        size = CMD_DIMENSION
        UIManager.put("TabbedPane.selected", Color.WHITE)
        UIManager.put("TabbedPane.contentAreaColor", Color(0, 0, 0, 0))
//        UIManager.put("TabbedPane.selected",ColorUIResource.GREEN);
//        UIManager.put("TabbedPane.background",ColorUIResource.GREEN);
//        UIManager.put("TabbedPane.shadow",ColorUIResource.GREEN);

        if (!isOnlyOut) {
            tabbedPane = tabbedPane()
            add(tabbedPane)
        } else {
            val tabID = UUID.randomUUID().toString()
            val textpane = textPane(tabID)
            tabs.add(Tab(textpane, sessionkey, iCaretListener, textPaneOutputHandler, tabID))
            add(scrollPane(textpane))
        }
    }

    private fun tabbedPane() =
        DraggableTabbedPane(tabs) { component, tab, i ->
            throw UnsupportedOperationException("Questa operazione non è supportata")
        }.apply {
            isOpaque = false
            background = CMD_BACKGROUND

            val p = JPanel()
            p.isOpaque = false
            p.background = CMD_BACKGROUND
            addTab(" + ", null, p, "Allows you to add another CMD tab")
//            setBackgroundAt(1, Color.RED)
//            setBackgroundAt(0, Color.WHITE)
//            putClientProperty("TabbedPane.selected", Color.WHITE)
//            putClientProperty("TabbedPane.contentAreaColor", Color(0, 0, 0, 0))

            selectedIndex = -1

            addChangeListener { e ->
                if (!isNotUpdatedByClick) {   //Se true vuol dire che l'azione è stata la cancellazione di un tab
                    val tabbedPane = e!!.source as DraggableTabbedPane
                    if (!tabbedPane.dragUpdate && tabbedPane.selectedIndex == tabbedPane.indexOfTab(" + ")) {
                        if (addedTab)
                            addedTab = false
                        else
                            createTab(tabbedPane, tabbedPane.selectedIndex)
                    }
                }
            }
        }


    /**
     * Creo lo JScrollPane
     */
    private fun scrollPane(textPane: TerminalTextPane) =
        JScrollPane(textPane).apply {
            size = CMD_DIMENSION
            background = CMD_BACKGROUND
            isOpaque = false
            viewport.background = CMD_BACKGROUND
            viewport.isOpaque = false
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            border = null
//            verticalScrollBar.background = Color.BLACK
//            verticalScrollBar.setUI(object : BasicScrollBarUI() {
//                override fun configureScrollBarColors() {
//                    thumbColor = Color(166, 166, 166)
//                }
//            })
//            verticalScrollBar.background = Color(64, 64, 64)
        }

    /**
     * Creo il CLITextPane (JTextPane)
     */
    private fun textPane(tabID: String) =
        TerminalTextPane(
            sessionkey = sessionkey
            , textPaneInputHandler = iCaretListener._filter
            , textPaneOutputHandler = textPaneOutputHandler
            , isOnlyOut = isOnlyOut
            , iCaretListener = iCaretListener
        ).apply {
            background = CMD_BACKGROUND
            isOpaque = false
            foreground = Color.WHITE
            caretColor = Color.GREEN
            (caret as DefaultCaret).updatePolicy = DefaultCaret.ALWAYS_UPDATE
            (styledDocument as AbstractDocument).documentFilter = iCaretListener._filter
            font = Costants.Fonts.SOURCE_PRO_LIGHT

            addCaretListener(CLICaretListener(iCaretListener))
        }

    /**
     * Necessario per la trasparenza senza glitch grafici con swing
     */
    override fun paintComponent(g: Graphics) {
        g.color = CMD_BACKGROUND
        g.fillRect(0, 0, width, height)
    }

    fun changeTitle(title: String) {
        //Cambio il titolo al tab
        tabbedPane?.let {
            it.setTitleAt(it.selectedIndex, title)
        }
    }

    fun closeTab() {
        if (tabbedPane != null && tabs.size > 0)
            tabbedPane!!.let {
                isNotUpdatedByClick = true    //Il change listener non ha uno stato, quindi, gestisco lo stato a mano
                tabs.removeAt(it.selectedIndex)
                it.removeTabAt(it.selectedIndex)
                if (tabs.size > 0) {
                    if (tabs.size == it.selectedIndex) //Se cancello l'ultimo e ho ancora tab, metto il focus sul precedente
                        it.selectedIndex = it.selectedIndex - 1
                    selectedTabPane?.requestFocus()   //Richiedo il focus, così posso continuare ad utilizzare actionmap
                } else
                    it.selectedIndex = -1   //Nessun tab
                isNotUpdatedByClick = false
            }
    }

    /**
     * Permette di creare nuovi tab
     */
    fun newTab() {
        tabbedPane?.let {
            isNotUpdatedByClick = true
            createTab(it, it.tabCount-1)
            isNotUpdatedByClick = false
        }
    }

    fun focusTab(index: Int) {
        tabbedPane?.let {
            it.selectedIndex = index
        }
    }

    private fun createTab(tab: JTabbedPane, index: Int) {
        addedTab = true
        val tabID = UUID.randomUUID().toString()
        textPane(tabID).run {
            //Inserisco il nuovo tab generico
            tab.insertTab("New Tab", null, scrollPane(this), "Tooltip", index)
            //Selezioni il nuovo tab
            tab.selectedIndex = index
            //Creo un'istanza che mi rappresenta il tab ed il suo contenuto
            Tab(this, sessionkey, iCaretListener, textPaneOutputHandler, tabID).apply {
                tabs.add(this)
                init()
            }
            //Mi mette il focus sul textpane, in questo modo posso scrivere e utilizzare shortcut da subito
            requestFocus()

        }
    }

    fun disableTextPane() {
        selectedTabPane?.let {
            it.isFocusable = false
            it.addKeyListener(object: KeyListener {
                override fun keyTyped(e: KeyEvent?) {
                    e?.consume()
                }
                override fun keyPressed(e: KeyEvent?) {
                    e?.consume()
                }
                override fun keyReleased(e: KeyEvent?) {
                    e?.consume()
                }
            })
        }
    }

}