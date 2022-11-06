package it.cm.cmd.core.ui.cli.util

import it.cm.cmd.core.ui.cli.CLIFrame2
import it.cm.common.ui.FrameOut
import java.awt.Color
import java.io.*

import javax.swing.SwingWorker

class ReaderThreadKotlin (
        _in: BufferedReader
        , out: PrintStream
        , frame: CLIFrame2
        , private val tabID: String
        , private val onDone: ()->Unit
) : SwingWorker<Unit, String>() {
    private val myIn: BufferedReader
    private val myOut: PrintStream
    private val cliFrame2: CLIFrame2

    init {
        myIn = _in
        myOut = out
        cliFrame2 = frame
    }

//    override fun run() {
//        try {
//            val buf = CharArray(32 * 1024)
//            while (true) {
//                val count: Int = myIn.read(buf)
//                if (count < 0)
//                    return
//                val string = String(buf, 0, count)
////                cliFrame2.appendAnsiString(string, Color.WHITE)
//                cliFrame2.appendAnsi(string, tabID)
//            }
//        } catch (e: IOException) {
//            throw RuntimeException(e)
//        }
//    }

    override fun doInBackground(): Unit {
        try {
            val buf = CharArray(32 * 1024)
            var line: String? = myIn.readLine()
            do {
                line?.let {
                    cliFrame2.appendAnsi(it, tabID)
                    println(it)
                }
                line = myIn.readLine()
            } while (line != null)
//            while (true) {
//                val count: Int = myIn.read(buf)
//                if (count < 0)
//                    break
//                val string = String(buf, 0, count)
////                cliFrame2.appendAnsiString(string, Color.WHITE)
//                cliFrame2.appendAnsi(string, tabID)
//                println(string)
//            }
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
