package it.cm.cmd.core.util;

class HistoryHandler : IHistory {

	private val history = mutableListOf<String>();

	private var currentIndex: Int = 0

	override operator fun plusAssign(command: String) {
		if (command.isNotEmpty()) {
			history.add(command);
			currentIndex = history.size;
		}
	}

	override fun addCommand(toAdd: String) {
		this += toAdd
	}

	override val previousCommand: String?
		get() = if (currentIndex > 0) history[--currentIndex] else null

	override val nextCommand: String?
		get() = if (currentIndex < history.size - 1) history[++currentIndex] else null

}
