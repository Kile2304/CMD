package it.cm.cmd.core.ui.listener;

import it.cm.cmd.core.ui.util.HistoryHandler
import java.awt.Color

interface ICmd {

	fun getCurrentCommand(): String;
	fun resetCommand();
	fun appendLine(append: String);
	fun appendLine(append: String, foregroundColor: Color);
	fun getHistory(): HistoryHandler;
	fun setCommandText(toAppend: String);
	
}
