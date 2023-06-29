package it.cm.cmd.core.util

import kotlinx.serialization.Serializable

@Serializable
class SConfiguration: java.io.Serializable {

    var enabledAlwaysOnTop = false
        private set

}