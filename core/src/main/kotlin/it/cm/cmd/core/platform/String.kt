package it.cm.cmd.core.platform

import java.util.regex.Matcher
import java.util.regex.Pattern

fun String.indexOfNextLetter(from: Int = -1): Int {
    var cont: Int = from
    while (cont++ < length)
        if (get(cont).toString().matches(Regex("(?i)[a-z]")))
            return cont
    return -1
}

fun CharSequence.splitAndKeep(input: Pattern, limit: Int = 0): MutableList<String> {
    var index = 0
    val matchLimited = limit > 0
    val matchList = mutableListOf<String>()
    val m: Matcher = input.matcher(this)

    // Add segments before each match found
    while (m.find()) {
        if (!matchLimited || matchList.size < limit - 1) {
            if (index == 0 && index == m.start() && m.start() == m.end()) {
                // no empty leading substring included for zero-width match
                // at the beginning of the input char sequence.
                continue
            }
            val match = this.subSequence(m.start(), m.end()).toString()
            if (m.start() != index && matchList.isNotEmpty())
                matchList[matchList.lastIndex] = matchList[matchList.lastIndex] + this.subSequence(index, m.start())
            index = m.end()
            matchList.add(match)
        } else if (matchList.size == limit - 1) { // last one
            val match = this.subSequence(
                index,
                this.length
            ).toString()
            matchList.add(match)
            index = m.end()
        }
    }

    // If no match was found, return this
    if (index == 0) return mutableListOf("$this")

    // Add remaining segment
    if (!matchLimited || matchList.size < limit) {
        matchList[matchList.lastIndex] = matchList[matchList.lastIndex] + this.subSequence(index, this.length)
    }

    // Construct result
    var resultSize = matchList.size
    if (limit == 0) while (resultSize > 0 && matchList[resultSize - 1].isEmpty()) resultSize--
    return matchList.subList(0, resultSize)
}
