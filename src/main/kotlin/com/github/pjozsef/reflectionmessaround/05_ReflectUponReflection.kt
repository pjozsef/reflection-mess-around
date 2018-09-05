package com.github.pjozsef.reflectionmessaround

import java.lang.reflect.Modifier

private class Something {
    companion object {
        @JvmField
        val finalValue = 10
    }
}

fun main(args: Array<String>) {
    val obj = Something()
    val secretField = obj::class.java.getDeclaredField("finalValue")
    secretField.isAccessible = true

    if (secretField.modifiers and Modifier.FINAL == Modifier.FINAL) println("Field is final") else println("Field is not final")

    val modifiersField = secretField::class.java.getDeclaredField("modifiers")
    modifiersField.isAccessible = true
    modifiersField.set(secretField, secretField.modifiers and Modifier.FINAL.inv())

    secretField.set(null, 400)

    println(Something.finalValue)
}