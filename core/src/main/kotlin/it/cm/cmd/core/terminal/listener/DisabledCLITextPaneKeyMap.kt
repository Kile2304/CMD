package it.cm.cmd.core.terminal.listener

import it.cm.cmd.core.terminal.frame.ITextPaneInputHandler
import it.cm.cmd.core.terminal.frame.ITextPaneOutputHandler
import it.cm.cmd.core.terminal.listener.actionMap.KeyMapAction

class DisabledCLITextPaneKeyMap(
    sessionKey: String
    , textPaneInputHandler: ITextPaneInputHandler
    , textPaneOutputHandler: ITextPaneOutputHandler
): KeyMapAction(sessionKey, textPaneInputHandler, textPaneOutputHandler)