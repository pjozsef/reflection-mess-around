package com.github.pjozsef.reflectionmessaround

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.jvm.isAccessible

private data class Foo(val a: Int?, val b: String?, val r: Runnable, val ac: (Int) -> Int) {

    private constructor(a: Int) : this(a, null, Runnable { }, { 4 })

    private fun fun1(a: Int, b: Int, d: String) {
        println("$a, $b, $d")
    }

    private fun fun2() {
        println("No params")
    }

}

fun main(args: Array<String>) {
    val args2: (Int) -> Int = { 5 }
    "com.github.pjozsef.reflectionmessaround.Foo"(null, null, Runnable { }, args2).also(::println)

    "com.github.pjozsef.reflectionmessaround.Foo"(3).also(::println).let {
        "fun1" withArgs listOf(1, 2, "asdf") invokeOn it
        "fun2" invokeOn it
    }
}

operator fun String.invoke(vararg args: Any?): Any {
    val type = Class.forName(this)

    val argCount = args.size
    val argTypes = args.map { it?.javaClass?.kotlin }

    val constructor = type.declaredConstructors.filter {
        it.parameterCount == argCount
    }.map {
        it to it.parameterTypes.map { it.kotlin }
    }.filter { (_, constructorArgTypes) ->
        var matches = true
        for (i in 0 until argCount) {
            val constructorArgType = constructorArgTypes[i]
            val argType = argTypes[i]
            if (argType != null
                    && !(constructorArgType == argType
                            || constructorArgType.java.isAssignableFrom(argType.java)
                            || isMatchingLambda(constructorArgType, argType))) {
                matches = false
                break
            }
        }
        matches
    }.associate { (constructor, _) ->
        val matchingArgs = argCount - argTypes.count { it == null }
        constructor to matchingArgs
    }.maxBy { it.value }?.key ?: kotlin.run {
        val argJavaTypes = args.map { it?.javaClass?.toString() }
        error("No matching constructor found for $argJavaTypes")
    }

    constructor.isAccessible = true

    return constructor.newInstance(*args)
}

data class Invocation(val functionName: String, val args: List<Any?>)

infix fun String.withArgs(args: List<Any>) = Invocation(this, args)

infix fun String.invokeOn(obj: Any) {
    Invocation(this, listOf()) invokeOn obj
}

infix fun Invocation.invokeOn(obj: Any) {
    val function = obj::class.declaredFunctions.filter {
        it.name == this.functionName
    }.firstOrNull {
        val extraParameter = if (it.hasImplicitParameter()) 1 else 0
        it.parameters.size == this.args.size + extraParameter
    } ?: kotlin.run {
        val argJavaTypes = this.args.map { it?.javaClass?.toString() }
        error("No matching constructor found for $argJavaTypes")
    }
    val finalArguments = if (function.hasImplicitParameter()) listOf(obj) + this.args else this.args
    function.isAccessible = true
    function.call(*finalArguments.toTypedArray())
}

private fun KFunction<*>.hasImplicitParameter() = extensionReceiverParameter != null || instanceParameter != null

private fun isMatchingLambda(constructorArg: KClass<out Any>, arg: KClass<out Any>): Boolean {
    return constructorArg.java.toString().contains("kotlin.jvm.functions.Function") and arg.java.superclass.toString().contains("kotlin.jvm.internal.Lambda")
}