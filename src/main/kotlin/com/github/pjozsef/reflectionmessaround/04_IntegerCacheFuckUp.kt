package com.github.pjozsef.reflectionmessaround

import java.util.*

//source: javax0.wordpress.com/2017/05/03/hacking-the-integercache-in-java-9/
fun main(args: Array<String>) {
    val type = Class.forName("java.lang.Integer\$IntegerCache")
    val field = type.getDeclaredField("cache")
    field.isAccessible = true
    val cache = field.get(type) as Array<java.lang.Integer>

    val random = Random()
    for (i in cache.indices) {
        cache[i] = java.lang.Integer(random.nextInt(cache.size))
    }

    for (i in 0..9) {
        println("Primitive int: $i, Integer: ${i as java.lang.Integer}")
    }
}