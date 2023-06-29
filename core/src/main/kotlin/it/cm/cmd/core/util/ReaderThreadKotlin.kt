package it.cm.cmd.core.util

import it.cm.cmd.core.terminal.component.model.Tab
import java.io.*

import javax.swing.SwingWorker

class ReaderThreadKotlin (
        _in: BufferedReader
        , out: PrintStream
        , private val tab: Tab
        , private val onDone: ()->Unit
) : SwingWorker<Unit, String>() {
    private val myIn: BufferedReader
    private val myOut: PrintStream

    init {
        myIn = _in
        myOut = out
    }

    override fun doInBackground(): Unit {
        try {
            var line: String? = myIn.readLine()
            do {
                line?.let {
                    tab.appendANSI(it)
                    println(it)
                }
                line = myIn.readLine()
            } while (line != null)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun done() {
        super.done()
//        println("FINITO")
        onDone.invoke()
    }
}
