package com.github.pjozsef.reflectionmessaround

class MySecuManager: SecurityManager(){
    fun whoCalled() {
        println(classContext.map { it.simpleName })
    }
}
data class A(val mySecuManager: MySecuManager){
    fun a() {
        mySecuManager.whoCalled()
    }

}
data class B(val mySecuManager: MySecuManager){
    fun b() {
        mySecuManager.whoCalled()
    }

}

fun main(args: Array<String>) {
    val mySecuManager = MySecuManager()
    val a = A(mySecuManager)
    val b = B(mySecuManager)

    mySecuManager.whoCalled()
    a.a()
    b.b()
}