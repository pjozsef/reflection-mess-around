package com.github.pjozsef.reflectionmessaround

object Singleton{
    var value = 50
}

fun main(args: Array<String>) {
    val type = Singleton::class.java
    val constructor = type.getDeclaredConstructor()
    constructor.isAccessible = true
    val instance = constructor.newInstance()
    println("Singleton == instance: ${Singleton == instance}")
    println("Singleton: $Singleton")
    println("Instance: $instance")
    println("Singleton value: ${Singleton.value}")
    println("Instance value: ${instance.value}")
    Singleton.value = 999
    println("Singleton new value: ${Singleton.value}")
    println("Instance new value: ${instance.value}")
}