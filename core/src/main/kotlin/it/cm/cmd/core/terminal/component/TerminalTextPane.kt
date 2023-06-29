package it.cm.cmd.core.terminal.component

import it.cm.cmd.core.terminal.frame.ITextPaneInputHandler
import it.cm.cmd.core.terminal.frame.ITextPaneOutputHandler
import it.cm.cmd.core.terminal.listener.CLIKeyListener
import it.cm.cmd.core.terminal.listener.DisabledCLITextPaneKeyMap
import it.cm.cmd.core.terminal.listener.actionMap.KeyMapAction
import it.cm.cmd.common.AnsiUtil
import it.cm.cmd.common.AnsiUtil.ansiFormatTojava
import it.cm.cmd.core.constant.Costants
import it.cm.cmd.core.terminal.frame.ICaretListener
import it.cm.cmd.core.terminal.listener.CLICaretListener
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JComponent
import javax.swing.JTextPane
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import javax.swing.text.*


/**
 * Represents a custom text pane used for terminal output and input.
 * The TerminalTextPane extends the JTextPane class and provides additional functionality specific to a terminal interface.
 *
 * @property sessionkey The session key associated with the terminal.
 * @property textPaneInputHandler The input handler for the text pane.
 * @property textPaneOutputHandler The output handler for the text pane.
 * @property isOnlyOut Determines if the text pane is only used for output.
 * @property iCaretListener The listener for caret events.
 */
class TerminalTextPane(
    var sessionkey: String
    , private val textPaneInputHandler: ITextPaneInputHandler
    , textPaneOutputHandler: ITextPaneOutputHandler
    , isOnlyOut: Boolean
    , iCaretListener: ICaretListener
) : JTextPane(DefaultStyledDocument()) {

    /**
     * Represents a key listener for handling key events.
     * The key listener is responsible for mapping key events to corresponding actions.
     *
     * @property keyListener The instance of the key listener.
     */
    var keyListener: KeyMapAction

    /**
     * Represents the range where the current line starts and ends.
     *
     * @property {number} newLineOffset - The value represents the offset of the new line.
     *                                   It indicates the range of where the current line starts and ends.
     *                                   The offset is calculated in number of characters.
     */

    var newLineOffset = 0  //range of where the current line starts and ends

    /**
     * Represents an attribute set with mutable properties.
     *
     * The `attr` variable is of type `MutableAttributeSet` which is an interface
     * defining a collection of attributes that can be modified. It is initialized as
     * a new instance of `SimpleAttributeSet` which is a concrete implementation of `MutableAttributeSet`.
     *
     * Example of usage:
     * ```
     * val characterAttributes: AttributeSet = ...
     *
     * val attr: MutableAttributeSet = SimpleAttributeSet().apply { addAttributes(characterAttributes) }
     * ```
     */
    val attr: MutableAttributeSet = SimpleAttributeSet().apply { addAttributes(characterAttributes) }

    init {
        resetStyle()
        isDoubleBuffered = true
        font = Costants.Fonts.SOURCE_PRO_LIGHT
        background = Costants.CMD_BACKGROUND
        isOpaque = false
        foreground = Color.WHITE
        caretColor = Color(192 , 122, 182)
        (caret as DefaultCaret).updatePolicy = DefaultCaret.ALWAYS_UPDATE
        //Tacconato aggiungendolo fuori
//        addCaretListener(CLICaretListener(iCaretListener))

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
        addCaretListener(CLICaretListener(iCaretListener))
        (styledDocument as AbstractDocument).documentFilter = iCaretListener._filter
//        highlighter.removeAllHighlights()
    }

    //Da portare in qualche modo su CLIFrame2 per gestire il caso: appena aperto, o, aprire un tab di default
    /**
     * Adds a hotkey for killing the process.
     *
     * This method adds a hotkey combination that when pressed, triggers the action to kill the process.
     * The hotkey combination is CTRL + Q.
     *
     * @see KeyEvent
     * @see KeyStroke
     */
    private fun addKillProcessHotKey() {
        //Tacconissimo
        val key2: KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK)
        actionMap.put("STOP", keyListener.STOP_ACTION)
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key2, "STOP")
    }

    /**
     * Executes the specified terminal command.
     *
     * @param command the command to be executed
     */
    fun runCommand(command: String): Unit = keyListener.runCommand(command)

    /**
     * Appends the given string to the styled document with the specified color.
     *
     * @param color the color to use for the appended string (default is Color.WHITE)
     * @param toAppend the string to append to the styled document
     */
    fun append(color: Color = Color.WHITE, toAppend: String) {
        StyleConstants.setForeground(attr, color)
        setCharacterAttributes(attr, false)
        if (caretPosition + toAppend.length <= styledDocument.length) {
            // Rimuove i caratteri esistenti alla posizione attuale del caret
            styledDocument.remove(caretPosition, toAppend.length)
        }
//        styledDocument.insertString(styledDocument.length, toAppend, attr)
        styledDocument.insertString(caretPosition, toAppend, attr)
    }

    /**
     * Regular expression pattern for ANSI escape sequences.
     *
     * ANSI_SEQUENCE matches the escape sequences used in ANSI terminal
     * emulators to format text with colors and styles.
     *
     * The pattern is a regular expression represented as a string, and
     * it is stored in the ANSI_SEQUENCE constant.
     *
     * The regular expression is designed to match ANSI escape sequences
     * consisting of the following components:
     * - An escape character (\u001B)
     * - An opening square bracket ([)
     * - One or more parameter bytes, separated by semicolons (;)
     * - An optional intermediate byte (?)
     * - Zero or more private mode bytes, separated by semicolons (;)
     * - A final byte specifying the control sequence command (A-Za-z)
     *
     * The ANSI_SEQUENCE pattern supports various combinations of
     * parameters and control sequence commands, allowing for a wide
     * range of formatting options.
     */
    private val ANSI_SEQUENCE = Regex("(?i)\\u001B\\[(?:(\\d{1,3}(?:;\\d{0,3})*)?|[?])(?:[;?]\\d{1,2})*[A-Za-z\\d]")

    val newLine = NewLine()

    /**
     * Appends ANSI-formatted text to the textpane area.
     *
     * @param toAppend the ANSI-formatted text to append
     */
    fun appendANSI(toAppend: String) {
        SwingUtilities.invokeLater {
            findMatchesAndNonMatches(
                ANSI_SEQUENCE, toAppend, {
                    println(it + "with new line: ${newLine.newLine}")
                    if (newLine.newLine) {
//                    println("I'm newliining the string")
                        append(StyleConstants.getForeground(attr), "\n$it")
                        newLine.newLine = false
                    } else {
                        append(StyleConstants.getForeground(attr), it)
                    }
                }, { elaborateAnsiSequence(it, newLine) }
            )
        }
    }

    class NewLine(
        var newLine: Boolean = false
    )


    /**
     * Finds matches and non-matches in a given input string using a regular expression.
     *
     * @param regex The regular expression pattern to match against the input string.
     * @param input The input string to search for matches.
     * @param doNotMatch A callback function to be called for each non-match found in the input string.
     *                   The non-match is provided as a parameter to the callback function.
     * @param doMatch A callback function to be called for each match found in the input string.
     *                The match is provided as a parameter to the callback function.
     */
    private fun findMatchesAndNonMatches(
        regex: Regex
        , input: String
        , doNotMatch: (String)->Unit
        , doMatch: (String)->Unit
    ) {
        val matches = regex.findAll(input)
        var currentPosition = 0

        for (match in matches) {
            val nonMatch = input.substring(currentPosition, match.range.first)
            if (nonMatch.isNotEmpty()) {
//                println("Non match: $nonMatch")
                doNotMatch(nonMatch)
            }

            val matchedText = match.value
            doMatch(matchedText)
//            println("Match: $matchedText")

            currentPosition = match.range.last + 1
        }

        val remainingText = input.substring(currentPosition)
        if (remainingText.isNotEmpty()) {
            doNotMatch(remainingText)
//            println("Non match: $remainingText")
        }
    }


    /**
     * Elaborates an ANSI sequence by determining whether it is a style sequence or an action sequence and processing it accordingly.
     *
     * @param sequence The ANSI sequence to be elaborated.
     */
    private fun elaborateAnsiSequence(sequence: String, newLine: NewLine) {
        if (sequence.endsWith("m"))
            elaborateAnsiStyle(sequence)
        else
            elaborateAnsiAction(sequence, newLine)
    }

    /**
     * Elaborates the ANSI style sequence.
     *
     * @param sequence the ANSI style sequence to be elaborated
     */
    private fun elaborateAnsiStyle(sequence: String) {
        ansiFormatTojava(
            attr
            , sequence.replace("\u001B[", "").replace("m", "")
        )
    }

    /**
     * Elaborates the given ANSI action sequence.
     *
     * @param sequence the ANSI action sequence to be elaborated
     */
    private fun elaborateAnsiAction(sequence: String, newLine: NewLine) {
        val pureSequence = sequence.drop(2)
//            println("With: " + pureSequence.drop(1))
        when (pureSequence) {
            "?25l" -> cursorVisibility(false)  // Nascondi il cursore
            "?25h" -> cursorVisibility(true)  // Mostra il cursore
            else -> {
                when (pureSequence.last()) {
                    'A' -> moveCursorUp(pureSequence.dropLast(1).toIntOrNull() ?: 1)
                    'B' -> moveCursorDown(pureSequence.dropLast(1).toIntOrNull() ?: 1)
                    'C' -> moveCursorForward(pureSequence.dropLast(1).toIntOrNull() ?: 1)
                    'D' -> moveCursorBackward(pureSequence.dropLast(1).toIntOrNull() ?: 1)
                    'K' -> with(pureSequence.dropLast(1).toIntOrNull()) {
                        this?.let {
                            if (it == 0) {
                                newLine.newLine = true
                            } else
                                eraseLines(it)
                        }
                    }
                    'J' -> eraseScreen()
                    'G' -> processSequenceG(pureSequence.dropLast(1).toIntOrNull() ?: 1)

                    else -> println(pureSequence.drop(1))
                }
            }
        }
    }

    /**
     * Sets the visibility of the cursor.
     *
     * @param visible true if the cursor should be visible, false otherwise.
     */
    private fun cursorVisibility(visible: Boolean) {
        caret.isVisible = visible
    }

    /**
     * Moves the caret to a specific column in the given line number.
     *
     * @param colNumber the line number in which the caret should be moved
     */
    private fun processSequenceG(colNumber: Int) {
        try {
            // Calcola la posizione di inizio della riga corrente
            newLine.newLine = false
            val lineStart = Utilities.getRowStart(this, caret.dot)
            // Calcola la posizione della colonna desiderata
            val targetColumn = minOf(colNumber, document.getText(lineStart, document.length - lineStart).length)
            val targetPosition = lineStart + targetColumn - 1
            // Sposta il cursore alla posizione della colonna desiderata
            caret.dot = 206
            caretPosition = 206
            println("Target position: $targetPosition")
        } catch (e: BadLocationException) {
            e.printStackTrace()
        }
    }

    /**
     * Resets the style attributes of the text.
     * This method sets the bold and underline attributes to false,
     * and resets the foreground and background colors to their default values.
     */
    fun resetStyle() {
        StyleConstants.setBold(attr,  false)
        StyleConstants.setUnderline(attr,  false)
        //TEMP
        StyleConstants.setForeground(attr, AnsiUtil.cReset)
        StyleConstants.setBackground(attr, Color(0, 0, 0, 0))
    }

    /**
     * Moves the cursor up by the specified number of lines.
     *
     * @param count The number of lines to move the cursor up. Must be a non-negative integer.
     */
    private fun moveCursorUp(count: Int) {
        val currentLineStartOffset = styledDocument.defaultRootElement.getElementIndex(caretPosition)
        val targetLine = maxOf(currentLineStartOffset - count, 0)
        val targetOffset = styledDocument.defaultRootElement.getElement(targetLine).startOffset
        caretPosition = targetOffset
    }

    /**
     * Moves the cursor down by the specified number of lines.
     *
     * @param count the number of lines to move the cursor down.
     */
    private fun moveCursorDown(count: Int) {
        val currentLineStartOffset = styledDocument.defaultRootElement.getElementIndex(caretPosition)
        val lastLine = styledDocument.defaultRootElement.elementCount - 1
        val targetLine = minOf(currentLineStartOffset + count, lastLine)
        val targetOffset = styledDocument.defaultRootElement.getElement(targetLine).startOffset
        caretPosition = targetOffset
    }

    /**

     * Moves the cursor forward by the specified count.
     *
     * @param count The number of positions to move the cursor forward.
     */
    private fun moveCursorForward(count: Int) {
        val newCaretPosition = minOf(caretPosition + count, styledDocument.length)
        caretPosition = newCaretPosition
    }

    /**
     * Moves the cursor backward in the text input by the specified count.
     *
     * @param count The number of positions to move the cursor back. Must be a positive integer.
     */
    private fun moveCursorBackward(count: Int) {
        val newCaretPosition = maxOf(caretPosition - count, 0)
        caretPosition = newCaretPosition
    }

    /**
     * Removes the specified number of lines from the text document starting from the current caret position.
     *
     * @param numberOfLines The number of lines to be removed.
     */
    private fun eraseLines(numberOfLines: Int) {
        try {
            val document = styledDocument
            val caretPos = caretPosition
            var startOffset: Int = 0
            var endOffset: Int

            for (line in 0 until numberOfLines) {
                val elementIndex = document.defaultRootElement.getElementIndex(caretPos)
                startOffset = document.defaultRootElement.getElement(elementIndex - line).startOffset
                endOffset = document.defaultRootElement.getElement(elementIndex - line).endOffset
                document.remove(startOffset, endOffset - startOffset)
            }

            caretPosition = startOffset
        } catch (e: BadLocationException) {
            e.printStackTrace()
        }
    }

    /**
     * Erases the screen by clearing the text in the text pane and resetting the caret position.
     */
    private fun eraseScreen() {
        println("Erase screen")
        textPaneInputHandler.clearText()
//        styledDocument.remove(0, styledDocument.length)
//        caretPosition = 0
    }

    /**
     * Sets up antialiasing for the given Graphics object.
     * If the Graphics object is an instance of Graphics2D, it sets the rendering hints to enable antialiasing for text.
     *
     * @param graphics The Graphics object to set up antialiasing for.
     */
    private fun setupAntialiasing(graphics: Graphics) {
        if (graphics is Graphics2D)
            graphics.setRenderingHints(RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            ))
    }

    /**
     * Sets up antialiasing for the graphics object and then invokes
     * the super class's paintComponent method.
     *
     * @param g The Graphics object to paint on.
     */
    override fun paintComponent(g: Graphics) {
        setupAntialiasing(g)
        super.paintComponent(g)
    }

    /**
     * Regenerates the key listener for accepting keyboard inputs for the CLI.
     * Used when dragging a tab from a terminal to another.
     *
     * @param sessionkey The session key associated with the CLI.
     * @param textPaneInputHandler The input handler for the CLI text pane.
     * @param textPaneOutputHandler The output handler for the CLI text pane.
     */
    fun regenerateKeyListener(
        sessionkey: String
        , textPaneInputHandler: ITextPaneInputHandler
        , textPaneOutputHandler: ITextPaneOutputHandler
    ) {
        removeKeyListener(keyListener as KeyListener)
        keyListener = CLIKeyListener(
            keymap, actions, sessionkey, textPaneInputHandler, textPaneOutputHandler
        )
        addKeyListener(keyListener as KeyListener)
    }

}