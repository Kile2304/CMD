package it.cm.cmd.core.ui.util;

import it.cm.cmd.core.ui.cli._interface.IHistory
import org.apache.commons.lang3.StringUtils

class HistoryHandler : IHistory {

	private val history = mutableListOf<String>();

	private var currentIndex: Int = 0;

	override fun addCommand(command: String) {
		if (StringUtils.isNotEmpty(command)) {
			history.add(command);
			currentIndex = history.size;
		}
	}

	override val previousCommand: String?
		get() = getPrevious()

	override val nextCommand: String?
		get() = getNext()


	private fun getNext(): String? {
		var previousCommand: String? = null;

		if (currentIndex < history.size - 1) {
			currentIndex += 1;
			previousCommand = history[currentIndex]
		}

		return previousCommand;
	}

	private fun getPrevious(): String? {
		var previousCommand: String? = null;

		if (currentIndex > 0) {
			currentIndex -= 1;
			previousCommand = history[currentIndex]
		}

		return previousCommand;
	}

}
