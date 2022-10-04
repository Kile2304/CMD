package it.cm.cmd.core.ui.cli.listener

import it.cm.cmd.core.ui.cli._interface.ICaretListener
import javax.swing.event.CaretEvent
import javax.swing.event.CaretListener


class CLICaretListener(
    private val caretListener: ICaretListener
) : CaretListener {

    override fun caretUpdate(e: CaretEvent) {
        val location = e.dot
        if (e.dot == e.mark && !caretListener._pasting) {
            //only try to handle caret positioning if the user hasn't pasted
            if (!caretListener._filter.validCaretPosition(location)) {
                //don't change the range that the caret is allowed to be in
                //just move the caret into the allowed range
                caretListener._filter.updateCaretPosition(false)
            }
        }
    }

}