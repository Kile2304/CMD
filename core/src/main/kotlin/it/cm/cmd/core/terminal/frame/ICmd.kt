package it.cm.cmd.core.terminal.frame;

import it.cm.cmd.core.util.HistoryHandler
import java.awt.Color

interface ICmd {

	fun getCurrentCommand(): String;
	fun resetCommand();
	fun appendLine(append: String);
	fun appendLine(append: String, foregroundColor: Color);
	fun getHistory(): HistoryHandler;
	fun setCommandText(toAppend: String);
	
}
