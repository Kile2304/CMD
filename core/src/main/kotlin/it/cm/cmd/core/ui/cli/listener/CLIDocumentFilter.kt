package it.cm.cmd.core.ui.cli.listener

import it.cm.cmd.core.ui.cli._interface.IBaseCLI
import it.cm.cmd.core.ui.cli._interface.ITextPaneInputHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.swing.JTextPane
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.DocumentFilter


class CLIDocumentFilter(
    private val console: IBaseCLI
) : DocumentFilter(), ITextPaneInputHandler {

    private val log: Logger = LoggerFactory.getLogger(javaClass)


    //range of where the current line starts and ends
    private var forceClear = false
    private val listeningForCommands = true
    private var newLineOffset  //range of where the current line starts and ends
        get() = console._textPane?.newLineOffset ?: 0
        set(value) { console._textPane?.newLineOffset = value }

    /**
     * Removes all text from the current document
     */
    override fun clearText() {
        try {
            forceClear = true
            val width = console().styledDocument.length
            console().styledDocument.remove(0, width)
//            console.printNewLine()
            forceClear = false
        } catch (ex: Exception) {
            log.error("Error: ", ex)
        }
    }

    /**
     * Gets the command without changing the offset used to calculate the diff
     * between the command and the prefix if updateOldOffset is false if its
     * true then its updated
     *
     * @param updateOldOffset whether to change the offset used by
     * `getInput()`
     * @return
     */
    override fun getCommand(updateOldOffset: Boolean): String {
        val input = getInput(updateOldOffset)
        val prefixStr: String = console.getLinePrefix()
        return if (prefixStr == input) {
            "" //no command just the prefix
        } else input.replace(prefixStr, "").trim { it <= ' ' }
    }

    /**
     * Clear only the text the user has typed since they last hit enter
     */
    override fun clearUserInput() {
        val doc = console().styledDocument
        try {
            doc.remove(newLineOffset, doc.length - newLineOffset)
        } catch (ex: BadLocationException) {
            log.error("Error: ", ex.message)
        }
    }

    /**
     * Gets the user input without affecting the caret or any internal offsets
     *
     * @return the string the user has entered on the current line
     */
    override fun getUserInput(): String {
        return getInput(false)
    }

    private fun getInput(updateOldOffest: Boolean): String {
        val doc = console().styledDocument
        var input = ""
        try {
            //get up to the end of the document from where we last stopped
            val length = doc.length - newLineOffset
            if (length > 0) {
                //start,length
                input = doc.getText(newLineOffset, length)
                if (updateOldOffest) {
                    //next time start from where the previous end was
                    newLineOffset = doc.length
                }
            }
        } catch (ex: BadLocationException) {
            log.error("Error", ex)
        }
        return input
    }

    /**
     * Invoked prior to removal of the specified region in the specified
     * Document. Subclasses that want to conditionally allow removal should
     * override this and only call supers implementation as necessary, or call
     * directly into the
     * `FilterBypass` as necessary.
     *
     * @param fb FilterBypass that can be used to mutate Document
     * @param offset the offset from the beginning >= 0
     * @param length the number of characters to remove >= 0
     * @exception BadLocationException some portion of the removal range was not
     * a valid part of the document. The location in the exception is the first
     * bad position encountered.
     */
    @Throws(BadLocationException::class)
    override fun remove(fb: FilterBypass, offset: Int, length: Int) {
        console.fireOnRemove(fb, offset, length)
        if (forceClear) {
            newLineOffset = -1
            fb.remove(0, length)
        } else if (offset >= newLineOffset) { // + console.getLinePrefix().length()))) {
            fb.remove(offset, length)
        }
    }

    /**
     * Invoked prior to insertion of text into the specified Document.
     * Subclasses that want to conditionally allow insertion should override
     * this and only call supers implementation as necessary, or call directly
     * into the FilterBypass.
     *
     * @param fb FilterBypass that can be used to mutate Document
     * @param offset the offset into the document to insert the content >= 0.
     * All positions that track change at or after the given location will move.
     * @param string the string to insert
     * @param attr the attributes to associate with the inserted content. This
     * may be null if there are no attributes.
     * @exception BadLocationException the given insert position is not a valid
     * position within the document
     */
    @Throws(BadLocationException::class)
    override fun insertString(
        fb: FilterBypass, offset: Int, string: String?,
        attr: AttributeSet?
    ) {
        console.fireOnInsert(fb, offset, string, attr)
        if (offset > 2000) {
            forceClear = true
            remove(fb, 0, 500)
            forceClear = false
//            clearText()

            fb.insertString(offset-500, string, attr)
        } else fb.insertString(offset, string, attr)
    }

    /**
     * Invoked prior to replacing a region of text in the specified Document.
     * Subclasses that want to conditionally allow replace should override this
     * and only call supers implementation as necessary, or call directly into
     * the FilterBypass.
     *
     * @param fb FilterBypass that can be used to mutate Document
     * @param offset Location in Document
     * @param length Length of text to delete
     * @param text Text to insert, null indicates no text to insert
     * @param attrs AttributeSet indicating attributes of inserted text, null is
     * legal.
     * @exception BadLocationException the given insert position is not a valid
     * position within the document
     */
    @Throws(BadLocationException::class)
    override fun replace(
        fb: FilterBypass, offset: Int, length: Int, text: String?,
        attrs: AttributeSet?
    ) {
        console.fireOnReplace(fb, offset, length, text, attrs)
        fb.replace(offset, length, text, attrs)
    }

    override fun removeLastCommand(lastCommand: String, toInsert: String) {
        val doc = console().styledDocument
        try {
            if (lastCommand.isNotEmpty()) {
//                val text = doc.getText(newLineOffset, length)
                doc.remove(doc.length - lastCommand.length, lastCommand.length)
//                val root: Element = doc.defaultRootElement
//                val first: Element = root.getElement(root.elementCount-1)
//                doc.remove(first.startOffset, first.endOffset)

//                val attr: MutableAttributeSet = SimpleAttributeSet()

//                StyleConstants.setBackground(attr, Color(0, 255, 0, 255))
//                StyleConstants.setForeground(attr, Color(255, 0, 0, 255))

                doc.insertString(doc.length, toInsert, console._textPane?.attr)
            }
//            updateCaretPosition(true)
        } catch (ex: BadLocationException) {
            log.error("Error: ", ex.message)
        }
    }

    /**
     * Gets the Jtextpane which represents the console
     *
     * @return
     */
    private fun console(): JTextPane {
        return console._textPane as JTextPane
    }

    fun validCaretPosition(location: Int): Boolean {
        val length = newLineOffset // + console.getLinePrefix().length();
        return location >= length
    }

    /**
     * Updates the Caret's position, setting the range within wich the caret is
     * allowed to be and moving the caret within that range if need be
     */
    override fun updateCaretPosition() {
        updateCaretPosition(true)
    }

    /**
     * @see  `updateCaretPosition
     * @param updateOldOffset if true then the lower end of the range that the
     * caret is allowed to be in is updated, if false then only the caret's
     * position is forced into the allowed range.
    ` */
    fun updateCaretPosition(updateOldOffset: Boolean) {
        //set the new line offset
        val newRangeStart: Int = console._consoleOffset
        if (updateOldOffset)
            newLineOffset = newRangeStart
        //then add the default console prompt (MUST BE DONE IN THIS ORDER!!!)
        console().caretPosition = newRangeStart
    }

}