package it.cm.cmd.core.util

class CircularIndex (
    val maxSize: Int
) {

    var index = 0
        private set

    fun getNext() = if (++index < maxSize) index else { index = 0; index }
    fun getPrevious() = if (--index >= 0) index else  { index = maxSize-1; index }
    fun getAndIncrease(): Int {
        getNext()
        return index
    }

}