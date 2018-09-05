package com.github.pjozsef.reflectionmessaround

import kotlin.reflect.KClass
import kotlin.reflect.full.functions

fun main(args: Array<String>) {
    spyOn(String::class)
    separator()
    spyOn(ArrayList::class)
}

fun spyOn(type: KClass<*>){
    println("Spying on ${type.qualifiedName}")
    println("\nConstructors:")
    type.constructors.forEach(::println)
    println("\nMembers:")
    type.members.forEach(::println)
    println("\nFunctions:")
    type.functions.forEach(::println)
    println("\nAnnotations:")
    type.annotations.forEach(::println)
}

fun separator() {
    println()
    println("          ##############          ")
    println("##################################")
    println("          ##############          ")
    println()
}