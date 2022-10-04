package it.cm.cmd.core.ui.cli.util

import it.cm.cmd.core.ui.cli.CLIFrame2
import it.cm.common.ui.FrameOut
import java.awt.Color
import java.io.IOException

import java.io.PrintStream
import java.io.Reader

class ReaderThreadKotlin (
        _in: Reader
        , out: PrintStream
        , frame: CLIFrame2
) : Thread() {
    private val myIn: Reader
    private val myOut: PrintStream
    private val cliFrame2: FrameOut

    init {
        myIn = _in
        myOut = out
        cliFrame2 = frame
    }

    override fun run() {
        try {
            val buf = CharArray(32 * 1024)
            while (true) {
                val count: Int = myIn.read(buf)
                if (count < 0)
                    return
                val string = String(buf, 0, count)
                cliFrame2.appendAnsiString(string, Color.WHITE)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
