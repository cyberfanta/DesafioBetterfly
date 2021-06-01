package com.cyberfanta.desafiobetterfly.exceptions

class ConnectionException
/**
 * Constructs a new exception with `null` as its detail message.
 * The cause is not initialized, and may subsequently be initialized by a
 * call to [.initCause].
 */(private val kind: String) : Exception() {
    override fun toString(): String {
        return "ConnectionException{" +
                "kind='" + kind + '\'' +
                "} " + super.toString()
    }
}
