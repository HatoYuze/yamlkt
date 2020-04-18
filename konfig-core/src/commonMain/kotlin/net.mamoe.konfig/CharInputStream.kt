package net.mamoe.konfig

import kotlinx.io.core.Input
import kotlinx.io.core.Output
import kotlinx.io.core.readTextExactCharacters
import kotlinx.io.core.writeText

/**
 * A stream for outputting [Char]s
 */
interface CharOutputStream {
    /**
     * Write the [string] to this stream
     */
    fun write(string: CharSequence)

    /**
     * Write the [char] to this stream
     */
    fun write(char: Char)
}

/**
 * A stream for inputting [Char]s
 */
interface CharInputStream {
    /**
     * Indicates whether the stream has ended
     */
    val endOfInput: Boolean

    /**
     * read next char
     */
    fun read(): Char

    // TODO for debug only!! should remove in release
    fun peakRemaining(): String
}

fun String.asCharStream(): CharInputStream = object : CharInputStream {
    var cur = 0

    override val endOfInput: Boolean
        get() = cur == this@asCharStream.length

    override fun read(): Char {
        return this@asCharStream[cur].also { cur++ } // don't move cur++ into []
    }

    override fun peakRemaining(): String {
        val cur = this.cur
        return readRemaining().also { this.cur = cur }
    }
}

fun Input.asCharStream(): CharInputStream = object : CharInputStream {
    override val endOfInput: Boolean
        get() = this@asCharStream.endOfInput

    override fun read(): Char {
        return readTextExactCharacters(1)[0]
    }

    override fun peakRemaining(): String {
        TODO("not implemented")
    }
}

fun StringBuilder.asCharStream(): CharOutputStream = object : CharOutputStream {
    override fun write(string: CharSequence) {
        this@asCharStream.append(string)
    }

    override fun write(char: Char) {
        this@asCharStream.append(char)
    }
}

fun Output.asCharStream(): CharOutputStream = object : CharOutputStream {
    override fun write(string: CharSequence) {
        this@asCharStream.writeText(string)
    }

    override fun write(char: Char) {
        this@asCharStream.writeText(char.toString())
    }
}

/**
 * Read all the remaining available chars and call [block]
 */
inline fun CharInputStream.readAhead(block: (char: Char) -> Unit) {
    while (!endOfInput) block(read())
}

fun CharInputStream.readRemaining(): String {
    return buildString { readAhead { append(it) } }
}