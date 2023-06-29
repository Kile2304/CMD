package it.cm.cmd.core.terminal.panel.terminal_panel

import it.cm.cmd.core.constant.Costants
import it.cm.cmd.core.terminal.frame.ICaretListener
import it.cm.cmd.core.terminal.frame.ITextPaneOutputHandler
import it.cm.cmd.core.terminal.component.TerminalTextPane
import it.cm.cmd.core.terminal.component.model.Tab
import it.cm.cmd.core.component.DraggableTabbedPane
import it.cm.cmd.core.util.HistoryHandler
import java.awt.Color
import java.awt.Graphics
import java.util.*
import javax.swing.*
import javax.swing.text.AttributeSet
import javax.swing.text.SimpleAttributeSet

class TabbedTerminalPanel(
    val sessionkey: String
    , private val iCaretListener: ICaretListener
    , private val textPaneOutputHandler: ITextPaneOutputHandler
): AbstractTerminalPanel(sessionkey, iCaretListener, textPaneOutputHandler) {

    companion object {
        const val NEW_TAB_STRING: String = " + "
    }

    override val attr: AttributeSet
        get() = _textPane?.characterAttributes ?: SimpleAttributeSet()
    override var currentPath: String
        get() = _selectedTab?.currentPath ?: "Not provided"
        set(value) { _selectedTab!!.currentPath = value }

    /**
     * Selected tab pane
     */
    override val _textPane: TerminalTextPane?
        get() = _selectedTab?.textPane

    override fun history(): HistoryHandler = _selectedTab?.history ?: HistoryHandler()

    override fun appendAnsi(toAppend: String, tabID: String) {
        tabs.first { it.tabID == tabID }.textPane.appendANSI(toAppend)
    }

    private val tabs: MutableList<Tab> = mutableListOf()
    private var tabbedPane: JTabbedPane = tabbedPane()

    val _selectedTab: Tab?
        get() = if (tabs.size > 0 && tabbedPane.selectedIndex >= 0) tabs[tabbedPane.selectedIndex] else null

    private var addedTab = false
    private var isNotUpdatedByClick = false

    //TODO Devo settarlo da fuori
    //Cambio il titolo al tab
    var title: String = ""
        set(value) =
            tabbedPane.let {
                it.setTitleAt(it.selectedIndex, value)
            }


    init {
        UIManager.put("TabbedPane.selected", Color.WHITE)
        UIManager.put("TabbedPane.contentAreaColor", Color(0, 0, 0, 0))

        add(tabbedPane)
    }

    private fun tabbedPane() =
        DraggableTabbedPane(tabs) { component, tab, index ->
            if (component is JScrollPane) {
                val s = component.viewport.view
                if (s is TerminalTextPane)
                    s.regenerateKeyListener(
                        sessionkey = sessionkey,
                        textPaneInputHandler = iCaretListener._filter,
                        textPaneOutputHandler = textPaneOutputHandler
                    )
            }
//            if (component is TerminalTextPane)
//                component.regenerateKeyListener(
//                    sessionkey = sessionkey,
//                    textPaneInputHandler = iCaretListener._filter,
//                    textPaneOutputHandler = textPaneOutputHandler
//                )
            tabs.add(index, tab.clone(sessionkey, iCaretListener, textPaneOutputHandler))
        }.apply {
            isOpaque = false
            background = Costants.CMD_BACKGROUND

            val panelForBackground = JPanel().apply {
                isOpaque = false
                background = Costants.CMD_BACKGROUND
            }
            addTab(NEW_TAB_STRING, null, panelForBackground, "Allows you to add another CMD tab")

            selectedIndex = -1

            addChangeListener { e ->
                if (!isNotUpdatedByClick) {   //Se true vuol dire che l'azione è stata la cancellazione di un tab
                    val tabbedPane = e!!.source as DraggableTabbedPane
                    if (!tabbedPane.dragUpdate && tabbedPane.selectedIndex == tabbedPane.indexOfTab(NEW_TAB_STRING)) {
                        if (addedTab)
                            addedTab = false
                        else
                            createTab(tabbedPane, tabbedPane.selectedIndex)
                    }
                }
            }
        }


    /**
     * Necessario per la trasparenza senza glitch grafici con swing
     */
    override fun paintComponent(g: Graphics) {
        g.color = Costants.CMD_BACKGROUND
        g.fillRect(0, 0, width, height)
    }

    //TODO Taccone, gestirlo su DraggableTabbedPane
    fun closeTab() {
        if (tabs.size > 0) {
            isNotUpdatedByClick = true    //Il change listener non ha uno stato, quindi, gestisco lo stato a mano
            tabs.removeAt(tabbedPane.selectedIndex)
            tabbedPane.removeTabAt(tabbedPane.selectedIndex)
            if (tabs.size > 0) {
                if (tabs.size == tabbedPane.selectedIndex) //Se cancello l'ultimo e ho ancora tab, metto il focus sul precedente
                    tabbedPane.selectedIndex = tabbedPane.selectedIndex - 1
                _textPane?.requestFocus()   //Richiedo il focus, così posso continuare ad utilizzare actionmap
            } else
                tabbedPane.selectedIndex = -1   //Nessun tab
            isNotUpdatedByClick = false
        }
    }

    /**
     * Permette di creare nuovi tab
     */
    fun newTab() {
        isNotUpdatedByClick = true
        createTab(tabbedPane, tabbedPane.tabCount - 1)
        isNotUpdatedByClick = false
    }

    infix fun focusTab(index: Int) {
        tabbedPane.selectedIndex = index
    }

    private fun createTab(tab: JTabbedPane, index: Int) {
        addedTab = true
        with(textPane(false)) {
            val tabID = UUID.randomUUID().toString()
            //Inserisco il nuovo tab generico
            tab.insertTab("New Tab", null, scrollPane(this), "Created a new TAB", index)
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

    fun changeTitle(title: String) {
        tabbedPane.let {
            it.setTitleAt(it.selectedIndex, title)
        }
    }

}