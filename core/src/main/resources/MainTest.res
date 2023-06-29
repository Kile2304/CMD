import com.google.common.base.Predicate
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.jediterm.pty.PtyMain.LoggingPtyProcessTtyConnector
import com.jediterm.terminal.*
import com.jediterm.terminal.SubstringFinder.FindResult
import com.jediterm.terminal.emulator.ColorPalette
import com.jediterm.terminal.emulator.ColorPaletteImpl
import com.jediterm.terminal.model.*
import com.jediterm.terminal.ui.*
import com.jediterm.terminal.ui.JediTermWidget.*
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
import com.jediterm.terminal.ui.settings.DefaultTabbedSettingsProvider
import com.jediterm.terminal.ui.settings.SettingsProvider
import com.pty4j.PtyProcess
import it.cm.cmd.core.terminal.cli.CLIFrame2
import it.cm.cmd.core.terminal.cli.CTextPane
import it.cm.cmd.core.terminal.CLIKeyListener
import it.cm.cmd.core.terminal.cli.util.AnsiUtil
import it.cm.common.cmd.CmdCommon
import it.cm.common.ui.UIHandler
import it.cm.parser.RuntimeCommand
import it.cm.ui.swing.utils.installTheme
import jdk.internal.org.jline.utils.ColorPalette
import java.awt.*
import java.awt.event.ItemListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.*
import java.nio.charset.Charset
import java.time.format.TextStyle
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.swing.*
import javax.swing.WindowConstants.EXIT_ON_CLOSE
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.plaf.basic.BasicScrollBarUI


class CliNow(sessionKey: String) : CLIFrame2(false, sessionKey)

val txtStyle = TextStyle(TerminalColor(255, 255, 0), TerminalColor(0, 0, 0), EnumSet.of(TextStyle.Option.BOLD))
val styleState = StyleState().apply { setDefaultStyle(txtStyle) }
val s = object:DefaultSettingsProvider() {
    override fun getTerminalColorPalette(): ColorPalette {
        return ColorPaletteImpl.WINDOWS_PALETTE
    }
}
//val myTextProcessing = TextProcessing(s.getHyperlinkColor(), s.getHyperlinkHighlightingMode())
var buffer = TerminalTextBuffer(800, 600, styleState, 2000, null)
lateinit var myFindComponent: SearchPanel
lateinit var term: TerminalPanel
lateinit var scrollBar: JScrollBar
lateinit var layerPane: JLayeredPane
lateinit var jediTerminal: JediTerminal

fun main() {
    installTheme("/themes/Dracula.theme.json")
    initCLIFrame(File("C:\\Sviluppo\\Software\\Runner\\"))
    RuntimeCommand.COMMANDS.entries
    UIHandler.setDefaultCMD(CliNow::class.java)
    SwingUtilities.invokeLater {
        UIHandler.newCMDRequest("${System.currentTimeMillis()}", null)?.apply {
            isVisible = true
        }
    }
}

fun main2() {
    installTheme("/themes/Dracula.theme.json")
//    initCLIFrame(File("C:\\Sviluppo\\Software\\Runner\\"))
//    RuntimeCommand.COMMANDS.entries
//    UIHandler.setDefaultCMD(CliNow::class.java)
//    SwingUtilities.invokeLater {
//        UIHandler.newCMDRequest("${System.currentTimeMillis()}", null)?.apply {
//            isVisible = true
//        }
//    }

    JFrame("Titulo menus cazzum").apply {
        layout = GridLayout()
        size = Dimension(800, 600)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
        layerPane = JLayeredPane().apply { layout = it.cm.cmd.core.terminal.test.TerminalLayout() }
        val terminalPanel = CTextPane()
    }

    JFrame("Titolo at cazzum").apply {
        layout = BorderLayout(0, 0)
        size = Dimension(800, 600)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

//        val term = JediTermWidget(Dimension(600, 600), s)
//        term.start()
//        buffer = term.terminalTextBuffer
//        val terminal = createTabbedTerminalWidget()
//        contentPane.add("center", terminal.component)
//        getSession(terminal)
        layerPane = JLayeredPane().apply { layout = it.cm.cmd.core.terminal.test.TerminalLayout() }
        term = TerminalPanel(s, buffer, styleState)
        jediTerminal = JediTerminal(term, buffer, styleState);
//        jediTerminal.setModeEnabled(TerminalMode.AltSendsEscape, s.altSendsEscape())
//        val myPreConnectHandler = PreConnectHandler(jediTerminal)
//        term.addCustomKeyListener(myPreConnectHandler)

//        term.setCoordAccessor(jediTerminal)
//        term.size = Dimension(800, 600)
        scrollBar = createScrollBar(term)
        scrollBar.isFocusable = false
        isFocusable = false
//        term.add(bar)
        layerPane.add(term, "TERMINAL")
        layerPane.add(scrollBar, "SCROLL")
        add(layerPane, BorderLayout.CENTER)
        scrollBar.model = term.boundedRangeModel

        term.init(scrollBar)
        isVisible = true
        term.isVisible = true

//        term.addTerminalMouseListener(jediTerminal)
    }
//    buffer.addLine(TerminalLine(TerminalLine.TextEntry(txtStyle, CharBuffer("Testo da scrivere"))))
    jediTerminal.writeCharacters("Testo da scrivere\n")
    runStandardCMDTask("dir /b /S")


//    SwingTerminalFrame(TerminalEmulatorAutoCloseTrigger.CloseOnEscape).apply {
//        title = "Titolo random"
//        isVisible = true
//    }

//    val sw = SwingTerminal().apply { preferredSize = Dimension(900, 600) }
//    JFrame().apply {
//        title = "Random"
//        layout = GridLayout()
//        setSize(900, 600)
//        setLocationRelativeTo(null)
//        add(sw)
//        isVisible = true
//    }

//    val terminal: Terminal = DefaultTerminalFactory().createTerminalEmulator()
//    val screen: Screen = TerminalScreen(terminal)
//
//    val s = "Hello World!"
//    val tGraphics: TextGraphics = terminal.newTextGraphics()
//
//    screen.startScreen()
//    screen.clear()
//
//    tGraphics.putString(10, 10, s)
//    screen.refresh()
//
////    screen.readInput()
//    screen.stopScreen()

}

fun createScrollBar(terminalPanel: TerminalPanel): JScrollBar {
    val scrollBar = JScrollBar()
    scrollBar.setUI(FindResultScrollBarUI(terminalPanel, s))
    return scrollBar
}

fun createTabbedTerminalWidget(): AbstractTabbedTerminalWidget<*> {
    return object : TabbedTerminalWidget(DefaultTabbedSettingsProvider(), ::getSession) {
        override fun createInnerTerminalWidget(): JediTermWidget {
            return JediTermWidget(settingsProvider)
        }
    }
}

fun getSession(terminal: AbstractTabbedTerminalWidget<*>?): JediTermWidget? {
    val session = terminal!!.createTerminalSession(createTtyConnector())
    session.start()
    return session
}

fun createTtyConnector(): TtyConnector? {
    return try {
        val envs: MutableMap<String, String> = Maps.newHashMap(System.getenv())
        val command: Array<String>
        if (UIUtil.isWindows) {
            command = arrayOf("cmd.exe")
        } else {
            command = arrayOf("/bin/bash", "--login")
            envs["TERM"] = "xterm"
        }
        val process = PtyProcess.exec(command, envs, null)
        LoggingPtyProcessTtyConnector(process, Charset.forName("UTF-8"))
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}

private fun runStandardCMDTask(userInput: String) {   //Gestire la disabilitazione dell'user input
    CLIKeyListener.IS_RUNNING.set(true)
    val process = CmdCommon.execCMDCommand("c:\\Sviluppo\\Maggioli\\Workspace", userInput)

    val infos: Thread =
        TempReaderThreadKotlin(
            InputStreamReader(process!!.inputStream, Charsets.UTF_8)
            , System.out
        )
    infos.start()
    val onExit: CompletableFuture<Process> = process!!.onExit()
    onExit.whenComplete { process: Process?, _: Throwable? ->
//            println("Tempo: ${System.currentTimeMillis() - tempo}")
        infos.join()
        println("Finito")
//            errors.join()
        process?.destroyForcibly()
        CLIKeyListener.IS_RUNNING.set(false)
    }
}
class FindResultScrollBarUI(
    private val myTerminalPanel: TerminalPanel
    , private val mySettingsProvider: SettingsProvider
) : BasicScrollBarUI() {

    override fun paintTrack(g: Graphics, c: JComponent?, trackBounds: Rectangle) {
        super.paintTrack(g, c, trackBounds)
        val result: FindResult? = myTerminalPanel.getFindResult()
        if (result != null) {
            val modelHeight: Int = scrollbar.getModel().getMaximum() - scrollbar.getModel().getMinimum()
            val anchorHeight = Math.max(2, trackBounds.height / modelHeight)
            val color: Color = mySettingsProvider.getTerminalColorPalette()
                .getBackground(
                    Objects.requireNonNull<TerminalColor>(
                        mySettingsProvider.getFoundPatternColor().getBackground()
                    )
                )
            g.color = color
            for (r in result.items) {
                val where = trackBounds.height * r.start.y / modelHeight
                g.fillRect(trackBounds.x, trackBounds.y + where, trackBounds.width, anchorHeight)
            }
        }
    }
}

class TempReaderThreadKotlin (
    _in: Reader
    , out: PrintStream
) : Thread() {
    private val myIn: Reader
    private val myOut: PrintStream

    init {
        myIn = _in
        myOut = out
    }

    override fun run() {
        try {
            val buf = CharArray(32 * 1024)
            while (true) {
                val count: Int = myIn.read(buf)
                if (count < 0)
                    return
                val string = String(buf, 0, count)
//                buffer.addLine(TerminalLine(TerminalLine.TextEntry(txtStyle, CharBuffer(string.replace(AnsiUtil.NEW_LINE, "\n")))))
                jediTerminal.writeString(string.replace(AnsiUtil.NEW_LINE, "\n"))
                string.split(AnsiUtil.NEW_LINE).forEach {
                    jediTerminal.writeCharacters(it)
                }
//                jediTerminal.
//                cliFrame2.appendAnsiString(string, Color.WHITE)
//                jediTerminal.scrollY()
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    class TempTerminalActionProvider : TerminalActionProvider {
        override fun getActions(): MutableList<TerminalAction> {
            return Lists.newArrayList(TerminalAction(s.getFindActionPresentation(),
                Predicate {
                    showFindText()
                    true
                }).withMnemonicKey(KeyEvent.VK_F)
            )
        }

        override fun getNextProvider(): TerminalActionProvider {
            return this
        }

        override fun setNextProvider(provider: TerminalActionProvider?) {

        }
        private fun showFindText() {
            if (myFindComponent == null) {
                myFindComponent = createSearchComponent().apply {
                    val component: JComponent = getComponent()
                    layerPane.add(component, "FIND")
                    layerPane.moveToFront(component)
                    layerPane.revalidate()
                    layerPane.repaint()
                    component.requestFocus()
                    addDocumentChangeListener(object : DocumentListener {
                        override fun insertUpdate(e: DocumentEvent) {
                            textUpdated()
                        }

                        override fun removeUpdate(e: DocumentEvent) {
                            textUpdated()
                        }

                        override fun changedUpdate(e: DocumentEvent) {
                            textUpdated()
                        }

                        private fun textUpdated() {
                            findText(getText(), ignoreCase())
                        }
                    })
                    addIgnoreCaseListener(ItemListener {
                        findText(
                            getText(),
                            ignoreCase()
                        )
                    })
                    addKeyListener(object : KeyAdapter() {
                        override fun keyPressed(keyEvent: KeyEvent) {
                            if (keyEvent.keyCode == KeyEvent.VK_ESCAPE) {
                                layerPane.remove(component)
                                layerPane.revalidate()
                                layerPane.repaint()
                                //myFindComponent = null
                                term.setFindResult(null)
                                term.requestFocusInWindow()
                            } else if (keyEvent.keyCode == KeyEvent.VK_ENTER || keyEvent.keyCode == KeyEvent.VK_UP) {
                                nextFindResultItem(term.selectNextFindResultItem())
                            } else if (keyEvent.keyCode == KeyEvent.VK_DOWN) {
                                prevFindResultItem(term.selectPrevFindResultItem())
                            } else {
                                super.keyPressed(keyEvent)
                            }
                        }
                    })
                }
            } else {
                myFindComponent.getComponent().requestFocusInWindow()
            }
        }

        private fun findText(text: String, ignoreCase: Boolean) {
            val results: FindResult = jediTerminal.searchInTerminalTextBuffer(text, ignoreCase)
            term.setFindResult(results)
            myFindComponent.onResultUpdated(results)
            scrollBar.repaint()
        }
        protected fun createSearchComponent() = JediTermWidget(s).SearchPanel()


    }
}

