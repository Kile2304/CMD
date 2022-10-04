package it.cm.cmd.core.ui.cli.listener

import javax.swing.event.DocumentEvent
import javax.swing.event.UndoableEditEvent
import javax.swing.text.*
import kotlin.math.min


/**
 * DefaultDocument subclass that supports batching inserts.
 */
class BatchDocument : DefaultStyledDocument() {
    /**
     * Batched ElementSpecs
     */
    private var batch: ArrayList<ElementSpec>? = ArrayList()

    /**
     * Adds a String (assumed to not contain linefeeds) for
     * later batch insertion.
     */
    fun appendBatchString(
        str: String,
        a: AttributeSet
    ) {
        // We could synchronize this if multiple threads
        // would be in here. Since we're trying to boost speed,
        // we'll leave it off for now.

        // Make a copy of the attributes, since we will hang onto
        // them indefinitely and the caller might change them
        // before they are processed.
        var a: AttributeSet = a
        a = a.copyAttributes()
        val chars = str.toCharArray()
        batch!!.add(
            ElementSpec(
                a, ElementSpec.ContentType, chars, 0, str.length
            )
        )

    }

    private fun chunkString(str: String): MutableList<String> {
        val toReturn = mutableListOf<String>()
        val count = str.length  / 2000
        var index = -1
        while (count > index++)
            toReturn.add(str.substring(2000 * index, min(str.length - (2000 * index), 2000)))
        return toReturn
    }

    /**
     * Adds a linefeed for later batch processing
     */
    fun appendBatchLineFeed(a: AttributeSet?) {
        // See sync notes above. In the interest of speed, this
        // isn't synchronized.

        // Add a spec with the linefeed characters
        batch!!.add(
            ElementSpec(
                a, ElementSpec.ContentType, EOL_ARRAY, 0, 1
            )
        )


        // Then add attributes for element start/end tags. Ideally
        // we'd get the attributes for the current position, but we
        // don't know what those are yet if we have unprocessed
        // batch inserts. Alternatives would be to get the last
        // paragraph element (instead of the first), or to process
        // any batch changes when a linefeed is inserted.
        val paragraph: Element = getParagraphElement(0)
        val pattr: AttributeSet = paragraph.attributes
        batch!!.add(ElementSpec(null, ElementSpec.EndTagType))
        batch!!.add(ElementSpec(pattr, ElementSpec.StartTagType))
    }

    @Throws(BadLocationException::class)
    fun processBatchUpdates(offs: Int) {
        // As with insertBatchString, this could be synchronized if
        // there was a chance multiple threads would be in here.
        val inserts = arrayOfNulls<ElementSpec>(batch!!.size)
        batch!!.toArray(inserts)

        // Process all of the inserts in bulk
        super.insert(offs, inserts)
    }

    @Throws(BadLocationException::class)
    override fun insert(offset: Int, data: Array<ElementSpec>?) {
        if (data.isNullOrEmpty())
            return
        try {
            writeLock()

            // install the content
            val c = content
            val n = data.size
            val sb = StringBuilder()
            for (i in 0 until n) {
                val es = data[i]
                remove(0, 500)
                if (es.length > 0)
                    sb.append(es.array, es.offset, es.length)
            }
            if (sb.isEmpty())
                // Nothing to insert, bail.
                return

            val cEdit = c.insertString(offset, sb.toString())

            // create event and build the element structure
            val length = sb.length
            val evnt = DefaultDocumentEvent(offset, length, DocumentEvent.EventType.INSERT)
            evnt.addEdit(cEdit)
            buffer.insert(offset, length, data, evnt)

            // update bidi (possibly)
            super.insertUpdate(evnt, null)

            // notify the listeners
            evnt.end()
            fireInsertUpdate(evnt)
            fireUndoableEditUpdate(UndoableEditEvent(this, evnt))
        } finally {
            writeUnlock()
        }
    }

    companion object {
        /**
         * EOL tag that we re-use when creating ElementSpecs
         */
        private val EOL_ARRAY = charArrayOf('\n')
    }

}