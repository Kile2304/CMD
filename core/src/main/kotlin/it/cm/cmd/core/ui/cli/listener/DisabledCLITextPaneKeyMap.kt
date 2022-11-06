package it.cm.cmd.core.ui.cli.listener

import it.cm.cmd.core.ui.cli._interface.ITextPaneInputHandler
import it.cm.cmd.core.ui.cli._interface.ITextPaneOutputHandler
import it.cm.cmd.core.ui.cli.listener.actionMap.KeyMapAction

class DisabledCLITextPaneKeyMap(
    sessionKey: String
    , textPaneInputHandler: ITextPaneInputHandler
    , textPaneOutputHandler: ITextPaneOutputHandler
    , tabID: String
): KeyMapAction(sessionKey, textPaneInputHandler, textPaneOutputHandler, tabID)