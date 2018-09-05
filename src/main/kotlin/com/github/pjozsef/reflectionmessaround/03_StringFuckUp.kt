package com.github.pjozsef.reflectionmessaround

import java.util.*

//source: https://avaldes.com/hacking-the-immutable-string-class-using-java-reflection/
fun main(args: Array<String>) {

    val s1 = "string_value"
    println("s1 value: ${s1}, hashCode: ${s1.hashCode()}")
    println("String literal hashCode: ${"string_value".hashCode()}")
    println("$s1==string_value: ${s1 == "string_value"}")

    val field = s1::class.java.getDeclaredField("value")
    field.isAccessible = true
    val charArray = field.get(s1) as CharArray
    Arrays.fill(charArray, 0, charArray.size, 'X')
    println("\nAfter messing up:")
    println("s1 value: ${s1}, hashCode: ${s1.hashCode()}")
    println("String literal hashCode: ${"string_value".hashCode()}")
    println("$s1==string_value: ${s1 == "string_value"}")
}