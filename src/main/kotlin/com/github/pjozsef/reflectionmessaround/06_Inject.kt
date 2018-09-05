package com.github.pjozsef.reflectionmessaround

import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.isAccessible

class Consumer1 {
    @Inject
    lateinit var field1: String

    @Inject(qualifier = "japanese")
    lateinit var field2: String

    @Inject
    lateinit var field3: Runnable

    override fun toString(): String {
        return "Consumer1(field1='$field1', field2='$field2', field3=$field3)"
    }
}

private class Consumer2 {
    @Inject(qualifier = "german")
    private lateinit var field1: String

    @Inject
    lateinit var field2: Runnable

    @Inject(qualifier = "special")
    lateinit var field3: Runnable

    override fun toString(): String {
        return "Consumer2(field1='$field1', field2=$field2, field3=$field3)"
    }

}

private val container = mapOf(
        DependencyKey(String::class) to "hali",
        DependencyKey(String::class, "japanese") to "konnichiwa",
        DependencyKey(String::class, "german") to "hallo",
        DependencyKey(Runnable::class) to Runnable { println("simple runnable") },
        DependencyKey(Runnable::class, "special") to Runnable { println("special runnable") }
)

fun main(args: Array<String>) {
    val consumer1 = instantiate(Consumer1::class)
    println(consumer1)
    consumer1.field3.run()

    println("\n-+-+-+-+-+-+-+-+-+-+-+-+-+-\n")

    val consumer2 = instantiate(Consumer2::class)
    println(consumer2)
    consumer2.field2.run()
    consumer2.field3.run()
}

private fun <T : Any> instantiate(type: KClass<out T>): T {
    val noArgsConstructor = type.constructors.single { it.parameters.all(KParameter::isOptional) }
    noArgsConstructor.isAccessible = true
    val result = noArgsConstructor.call()
    type.declaredMemberProperties.map {
        it to it.findAnnotation<Inject>()
    }.filter {(property, inject)->
        property is KMutableProperty<*> && inject!=null
    }.map {(property, inject)->
        property as KMutableProperty<*> to inject!!
    }.forEach { (property, inject)->
        val classifier = property.returnType.classifier!!
        val qualifier = inject.qualifier
        property.isAccessible = true
        property.setter.call(result, container[DependencyKey(classifier, qualifier)])
    }
    return result
}

private const val NO_QUALIFIER = ""

private data class DependencyKey(val type: KClassifier, val qualifier: String? = NO_QUALIFIER)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Inject(val qualifier: String = NO_QUALIFIER)