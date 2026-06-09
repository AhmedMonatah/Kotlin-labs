package com.example.myapplication

import android.view.KeyEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.compose
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import okhttp3.Dispatcher
import kotlin.coroutines.coroutineContext

suspend fun main() {
//  runBlocking {
//      val x=  launch {
//          repeat(1000){
//              println("$it")
//              Thread.sleep(1000)
//          }
//
//      }
//      delay(1000)
//      x.cancelAndJoin()
//      println("Canceld")
//
//     // x.await()
//  }
//   println("dfaf")
//  var xz= GlobalScope.launch {
//        repeat(10){
//            println("Hello $it")
//            delay(20000000000)
//        }
//    }
//    xz.join()
//    println("adfdsf")

//runBlocking {
//  val x=  launch {
//       repeat(100){
//           delay(100)
//           print("$it")
//       }
//      }
//    val x2=  launch {
//        repeat(100){
//            delay(100)
//            print("$it")
//        }
//    }
//    delay(1000)
//    x.join()
//    x2.join()
//    x.cancel()
//    x2.cancel()
//
//

//runBlocking {
//    val x=launch {
//       print( summation(arrayOf(1,2,3,4,5,6,7,8,9,10)))
//    }
//    val x4=async {
//        print( summation(arrayOf(9,0,3,47,14,2,57,6,24,15)))
//    }
//    x.cancel()
//    x.join()
//
//}
//    coroutineScope {
//      flow<Int> {
//          repeat (12){
//              delay(2533)
//              print("$it")
//          }
//      }.filter{it->it>5}.map { it->it*5 }
//          .collect {
//              print(it)
//          }
//    }
//    coroutineScope {
//        flow<String> {
//            repeat(12){
//                delay(2533)
//                emit("$it")
//            }
//        }
//            .map { it.split(" ") }
//            .map { it.last() }
//            .flowOn(Dispatchers.IO)
//            .onEach {
//                println("onEach running on ${Thread.currentThread().name}")
//                delay(1000)
//            }
//            .flowOn(Dispatchers.Default)
//            .collect {
//                println("collect on ${Thread.currentThread().name} -> $it")
//            }
//    }
//}
//flow<String>{
//    for(i in 1..10){
//        GlobalScope.launch {
//            emit("the number is: $i")
//        }
//}
//}.collect {
//    value->
//    println(value)
//}
//
//Thread.sleep(1000)
//
//    coroutineScope {
//        val sharedFlow= MutableSharedFlow<Int>()
//        launch {
//            sharedFlow.collect {
//                println("collect1: $it")
//            }
//        }
//        launch {
//            sharedFlow.collect {
//                println("collect2: $it")
//            }
//        }
//        sharedFlow.emit(1)
//        sharedFlow.emit(2)
//        sharedFlow.emit(3)
//    }
//
//    coroutineScope {
//        flow {
//            repeat(50){
//                delay(1000)
//                emit(it)
//            }
//        }.filter { it->it%2==0 }.collect { println(it) }
//    }
//   with(CoroutineScope(Dispatchers.IO)){
//       val stateFlow= MutableStateFlow("")
//
//        launch {
//        stateFlow.collect {
//            println(it)
//        }
//        }
//       stateFlow.value="1"
//       stateFlow.value="2"
//       stateFlow.value="3"
//       stateFlow.value="4"
//    }


//    runBlocking {
//        launch {
//            delay(100)
//            println("A")
//        }
//        coroutineScope {
//            launch {
//                delay(500)
//                println("B")
//            }
//            delay(50)
//            println("C")
//        }
//    }
//    println("D")

//   fun produce()= GlobalScope.produce<Int> {
//        for(i in 1..10){
//            delay(1000)
//            send(i)
//        }
//    }
//
//    val x=produce()
//    x.consumeEach {
//        println("receive "+it)
//    }

//    coroutineScope {
//        val sharedFlow=MutableStateFlow<Int>(0)
//        launch {
//            sharedFlow.take(4).collect {
//                println("collect1: $it")
//            }
//        }
//        launch {
//            sharedFlow.take(4).collect {
//                println("collect2: $it")
//            }
//        }
//        sharedFlow.emit(1)
//        sharedFlow.emit(2)
//        sharedFlow.emit(3)

//    }

//
//    with(CoroutineScope(coroutineContext+SupervisorJob())) {
//        val x= launch{
//            delay(1000)
//            println("a1")
//        }
//
//        val x2=launch{
//            delay(1000)
//            println("a2")
//        }
//
//    }
//    coroutineScope {
//        val x= MutableSharedFlow<Int>()
//        val t1= launch {
//            x.collectLatest{
//                delay(200)
//                println("First"+it)
//            }
//        }
//      val t2=  launch {
//            x.collect {
//                println("Second"+it)
//            }
//        }
//        launch {
//            delay(100)
//            x.emit(1)
//            delay(100)
//            x.emit(2)
//            delay(100)
//            x.emit(3)
//            delay(100)
//            x.emit(4)
//            delay(100)
//            x.emit(5)
//            delay(100)
//        }
//
//        joinAll(t1,t2)
//        t1.cancel()
//        t2.cancel()

//
//    coroutineScope {
//       val x= launch {
//            repeat(10){
//                println("Hello $it")
//                delay(2000)
//            }
//        }
//       val x2= launch {
//            repeat(10){
//                println("Hello2 $it")
//                delay(2000)
//            }
//        }
//        delay(10000)
//        x.cancel()
//        x2.cancel()
//    }
//

//    runBlocking {
//
//        launch {
//            repeat(3) {
//                println("A $it")
//                yield()
//            }
//        }
//
//        launch {
//            repeat(3) {
//                println("B $it")
//                yield()
//            }
//        }
//        coroutineScope {
//            println(summation(arrayOf(1, 5, 57, 2715)))
//
//            println(x(12, 4))
//        }
//    }
//    runBlocking {
//
//        launch {
//            repeat(3) {
//                println("A $it")
//                yield()
//            }
//        }
//
//        launch {
//            repeat(3) {
//                println("B $it")
//                yield()
//            }
//        }
//    }

    val handler=CoroutineExceptionHandler{_,exception->println("Caught $exception in handeleer")
    }

    val parentJob=GlobalScope.launch(handler) {
    launch {
        try{
            delay(Long.MAX_VALUE)
        }
        catch (e:Exception){
            println("${e.javaClass.simpleName} in child 1")
        }
    }
      launch {
            delay(100)
            throw IllegalArgumentException()
        }
        delay(Long.MAX_VALUE)
    }
    parentJob.join()
//    val supering=SupervisorJob();
//    with(CoroutineScope(coroutineContext+supering)){
//
//        val first=launch {
//            println("First Child throws an exce")
//            throw ArithmeticException()
//        }
//        val second=launch{
//            println("First Child  is canceld ${first.isCancelled}")
//            try {
//                delay(5000)
//            }catch (e: CancellationException){
//                println("Second Child is cancelled because of $e")
//            }
//        }
//        first.join()
//        println("Second Child is active ${second.isActive}")
//        supering.cancel()
//        second.join()
//    }






    }
inline fun x(z: Int, y: Int): Int {
    return z + y
}

suspend fun summation(arr: Array<Int>): Int {
    delay(1500)
    return arr.sum()
}