package it.cm.cmd.core.ui.cli.component

import it.cm.cmd.core.ui.cli._interface.ITextPaneInputHandler
import it.cm.cmd.core.ui.cli._interface.ITextPaneOutputHandler
import it.cm.cmd.core.ui.cli.listener.BatchDocument
import it.cm.cmd.core.ui.cli.listener.CLIKeyListener
import it.cm.cmd.core.ui.cli.listener.DisabledCLITextPaneKeyMap
import it.cm.cmd.core.ui.cli.listener.actionMap.KeyMapAction
import it.cm.cmd.core.ui.cli.util.AnsiUtil
import it.cm.cmd.core.ui.cli.util.AnsiUtil.Companion.cReset
import it.cm.cmd.core.ui.cli.util.AnsiUtil.Companion.temporaryNullify
import java.awt.Color
import java.awt.Container
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.*
import javax.swing.JComponent
import javax.swing.JTextPane
import javax.swing.KeyStroke
import javax.swing.text.*


class CLITextPane(
    doc: StyledDocument = BatchDocument()
    , val sessionkey: String
    , textPaneInputHandler: ITextPaneInputHandler
    , textPaneOutputHandler: ITextPaneOutputHandler
    , isOnlyOut: Boolean
    , private val contentPane: Container?

) : JTextPane(doc) {

    private var colorCurrent = cReset
    private var remaining = ""
    var keyListener: KeyMapAction

    var newLineOffset = 0  //range of where the current line starts and ends

    val attr: MutableAttributeSet = SimpleAttributeSet().apply { addAttributes(characterAttributes) }

    init {
        isDoubleBuffered = true
        setCharacterAttributes(attr, false)
        if (!isOnlyOut) {
            keyListener =
                CLIKeyListener(
                    keymap, actions, sessionkey, textPaneInputHandler, textPaneOutputHandler
                )
            addKeyListener(keyListener as KeyListener)
        } else
            keyListener =
                DisabledCLITextPaneKeyMap(
                    sessionkey, textPaneInputHandler, textPaneOutputHandler
                )
        addKillProcessHotKey()

        val timer = Timer()
        timer.schedule(object: TimerTask() {
            override fun run() {
                if (styledDocument != null) {
                    (styledDocument as BatchDocument).processBatchUpdates(styledDocument.length)
                }
            }

        }, 0,500)

//        highlighter.removeAllHighlights()
    }

    //Da portare in qualche modo su CLIFrame2 per gestire il caso: appena aperto, o, aprire un tab di default
    private fun addKillProcessHotKey() {
        //Tacconissimo
//        val parentPanel = contentPane as JPanel?
//        parentPanel?.let {
            val key2: KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK)
            actionMap.put("STOP", keyListener.STOP_ACTION)
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key2, "STOP")
//        }
    }

    fun runCommand(command: String) {
        keyListener.runCommand(command)
    }

    fun  append(color: Color = Color.WHITE, toAppend: String) {
        StyleConstants.setForeground(attr, color)

        setCharacterAttributes(attr, false)

//        replaceSelection(toAppend) // there is no selection, so inserts at caret
//        doc.insertString(doc.length, toAppend, null)

//        val docLengthMaximum = 200    //Il rimuovi non stà funzionando
//
//        if (styledDocument.length + toAppend.length > docLengthMaximum) {
//
//            styledDocument.remove(0, toAppend.length)
//        }
//        styledDocument.insertString(styledDocument.length, toAppend, attr)
        (styledDocument as BatchDocument).appendBatchString(toAppend, attr)
        (styledDocument as BatchDocument).appendBatchLineFeed(attr)
//        if (System.currentTimeMillis() % 2 == 0L)
//            (styledDocument as BatchDocument).processBatchUpdates(styledDocument.length)

    }

    private val format = StringBuilder()
    private val untilNextFormat = StringBuilder()
    private val regex = Regex("(?=\u001B\\[[^m]+m)")
    private val regex2 = Regex("\u001B\\[([^m]+)m")
    fun appendANSI(toAppend: String) { // convert ANSI color codes first
        val t1 = System.currentTimeMillis()

//        val addString = temporaryNullify(toAppend).replace("\r\n", "").replace(AnsiUtil.NEW_LINE, "\n")
        val addString = toAppend
//        print(addString)

//        var start = System.currentTimeMillis()


//        println("original: $addString")
        val split = regex.split(toAppend)
        split.forEach {
//            println("splitted: $it")
            var cur = temporaryNullify(it).replace("\r\n", "").replace(AnsiUtil.NEW_LINE, "\n")
            var ansi: String? = null
            if (cur.isNotEmpty()) {
                if (it[0] == '\u001B') {
                    val indexOf = cur.indexOf('m')
                    if (indexOf > -1) {
                        ansi = cur.substring(0, indexOf+1)
                        cur = cur.substring(indexOf+1, cur.length)
                    }
                }
                ansi?.let { regex2.findAll(it).forEach { AnsiUtil.ansiFormatTojava(attr, it.groupValues[1]); } }

                append(StyleConstants.getForeground(attr), cur)
            }
        }


//        var index = 0
//        while (index < addString.length) {
//            val currentChar = addString[index++]
//            if (currentChar == '\u001B' || format.isNotEmpty()) { //Sistemare per fare l'effettivo append di untilNextFormat
//                format.append(currentChar)
//                if (untilNextFormat.isNotEmpty()) {
//                    append(StyleConstants.getForeground(attr), untilNextFormat.toString())
//                    untilNextFormat.clear()
//                } else if (currentChar == 'm') {
//                    AnsiUtil.ansiFormatTojava(attr, format)
//                    format.clear()
//                }
//            } else {
//                untilNextFormat.append(currentChar)
//                if (untilNextFormat.length > 10) {
//                    append(StyleConstants.getForeground(attr), untilNextFormat.toString())
//                    untilNextFormat.clear()
//                }
//            }
//        }
//        if (untilNextFormat.isNotEmpty()) {
//            append(StyleConstants.getForeground(attr), untilNextFormat.toString())
//            untilNextFormat.clear()
//        }
//        val end = System.currentTimeMillis()
//
//        var start2 = System.currentTimeMillis()
//        append(Color.WHITE, toAppend)
//        val end2 = System.currentTimeMillis()

//        println("Parsing: ${end - start}. Normal: ${end2 - start2}")
        println("Time: ${System.currentTimeMillis() - t1}")

    }

//    override fun paintComponent(g: Graphics) {
//
//    }


}