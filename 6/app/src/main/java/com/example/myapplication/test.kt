package com.example.myapplication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.cancellation.CancellationException

fun lab1(){
    println("hi before")

    runBlocking {
        val job1 =async(Dispatchers.Default) {
           for (i in 1..10){
               println(i)
               delay(1000)
           }
            "complete"
       }
        val job2=launch(Dispatchers.Default) {
            for (i in 1..10){
                println(i)
                delay(1000)
            }
        }
        val result = job1.await()
        println(result)

        job2.join()
    }
    println("hi after")
}

suspend fun factorial(n: Int): Int =
    coroutineScope {
    val deferred = async(Dispatchers.Default) {
        var result = 1
        for (i in 1..n) {
            result *= i
        }
        result
    }

    deferred.await()
}

fun lab2(){
    println("hi before")
    runBlocking {
        val fac = factorial(5)
        println("Factorial = $fac")
    }

    println("hi after")
}

fun lab3(){
    runBlocking {

        val job1 = launch {
            try {
               repeat(1000){i->
                   delay(200)
                   println("Printing 1: $i")
               }
            } catch (e: CancellationException) {
                println("job 1: "+e)
            }
        }

        val job2 = launch {
            try {
                repeat(1000){i->
                    delay(200)
                    println("Printing 2: $i")
                }
            } catch (e: CancellationException) {
                println("job 2: "+e)
            }
        }

        delay(500)
        job1.cancel()
        job2.cancel()

        job1.join()
        job2.join()

        println("finished both")
}
}
suspend fun sumArray(n: Array<Int>) = coroutineScope {
    val deferred = async(Dispatchers.Default) {
        var sum = 0
        for (i in n) {
            sum += i
        }
        sum
    }

    deferred.await()
}
fun lab4(){

    runBlocking {
        val arr = arrayOf(1, 2, 3, 4, 5)
        val result = sumArray(arr)
        println(result)
    }
}


fun lab5(){
    runBlocking {
        val job= launch {
            try {
                repeat(1000){
                    i->delay(200)
                    println("Printing: $i")
                }
            } catch (e: CancellationException) {
                println(e)
            }

        }
        delay(500)
        job.cancelAndJoin()
        println("finished")
    }

}

fun test(){
    runBlocking {
        var job=launch {

            repeat(1000){
                println("Printing: $it")
                Thread.sleep(1000)
            }

            }
        delay(1000)
        job.cancel()
        println("Finshed")
    }
}
data class Complex(
    val real: Int,
    val img: Int
)

infix operator fun Complex.plus(complex: Complex):Complex{
    return Complex(real = this.real + complex.real, img = this.img + complex.img)
}
operator fun Complex.minus (complex: Complex): Complex{
    return Complex (real = this.real - complex.real, img = this.img - complex.img)
}
fun main(){
    val c1 = Complex(real = 1, img = 2)
    val c2 =Complex(real = 9, img = 22)
    val c3=c1 + c2
    val c4=c1 -c2
    println(c3)
    println(c4)
}
