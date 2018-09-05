package com.github.pjozsef.reflectionmessaround

private class TrulyPrivate(private val value: String)

fun main(args: Array<String>) {
    val trulyPrivate = TrulyPrivate("here's a value for you")

    println("Without SecurityManager:")
    printPrivateField(trulyPrivate)

    println("\nWith SecurityManager:")
    System.setSecurityManager(SecurityManager())
    printPrivateField(trulyPrivate)
}

private fun printPrivateField(trulyPrivate: TrulyPrivate) {
    trulyPrivate::class.java
            .getDeclaredField("value")
            .also { it.isAccessible = true }
            .get(trulyPrivate)
            .also(::println)
}