import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

//package com.example.myapplication
//
//
//
//class X(var z: Int, var y: Int) {
//
//    init {
//        println("1: $z + $y");
//    }
//    init {
//        println("2: $z - $y");
//    }
//    init {
//        println("3: $z * $y");
//    }
//    constructor(s: String, e: Int) : this(5, 8) {
//
//        println("this is z = $z")
//        println("this is  y= $y")
//
//    }
//}
////data class User(var name: String, var age: Int)
//
////fun main() {
////    val u1 = User("Ahmed", 25)
////    val u2 = u1.copy(age = 26)
////
////    u2.name = "Ali" // ممكن تعديل مباشرة
////    println(u2) // User(name=Ali, age=26)
////}
//
//interface Printer {
//    fun local()
//    fun remote()
//}
//
//class SimplePrinter : Printer {
//    override fun local() {
//        println("Local...")
//    }
//
//    override fun remote() {
//        println("Remote....")
//    }
//    fun print(){
//        println("print")
//    }
//
//}
//
//
//class ReportPrinter(del: SimplePrinter) : Printer by del{
//
//}
//
//
//class User {
//
//}
//
//class Student{
//    var user:String
//    get() {
//        return "Ahmed"
//    }
//    set(value) {
//        println("this is $value")
//    }
//}
////fun main() {
////val x:Printer=ReportPrinter(SimplePrinter())
////    x.local()
////    x.remote()
////}
//
///*
//fun main() {
//    val obj=X("hello", 10)
//    println("z = ${obj.z}, y = ${obj.y}")
//}*/
//data class Address(var name: String,var city: String, var street: String, var building: Building)
//
//
//class Person(var name: String = "") {
//
//    inner class kk(
//        var city: String = "",
//        var street: String = "",
//        var building: Building = Building.APARTMENT
//    ) {
//        override fun toString(): String {
//            return "$name, $city, $street, $building"
//        }
//    }
//}
//
//enum class Building {
//    VILLA,
//    APARTMENT
//}
//
//fun main() {
//    val address = Address(
//        name = "ahmed",
//        city = "qena",
//        street = "tahrir street",
//        building = Building.VILLA
//    )
//
//   print(address.toString())
//}
//
//
//interface MyNumber {
//    fun getNext(): Int
//    fun getcount():Int
//    fun reset()
//
//}
//class ByThrees : MyNumber {
//
//    private var value:Int = 0
//
//    override fun getNext(): Int {
//        value += 3
//        return value
//    }
//
//    override fun reset() {
//        value = 0
//    }
//    override fun getcount():Int {
//        return value
//
//    }
//}
//fun ad() {
//    val num: MyNumber = ByThrees()
//    println("this is first: "+num.getNext())
//    println("this is second: "+num.getNext())
//    println("this is third: "+num.getNext())
//    println("this is count: "+num.getcount())
//    num.reset()
//    println(num.getNext())
//
//}
//
//class pb(val name:String="zzname",var address:String="zzaddress"){
//    init {
//        println("hey bro")
//        println("$name and $address")
//    }
//     constructor(x:Int,y:String):this("ahmed","ahmed"){
//        println("hey bro")
//        println("$x and $y")
//    }
//}
//
//fun test(){
//    pb(2,"ahmed")
//}

interface Producer{
    fun produce()

}

class ProducerImp:Producer{
    override fun produce() {
       print("test1")
    }


}

class  EnhancedProducer(private val delegate: Producer):Producer by if(false) delegate else ProducerImp()

fun lab(){
    val producer=EnhancedProducer(ProducerImp())
    producer.produce()

}
interface DataSource {
    fun getData(): String
}
class LocalDataSource : DataSource {
    override fun getData(): String {
        return "Data from LOCAL"
    }
}
class RemoteDataSource : DataSource {
    override fun getData(): String {
        return "Data from REMOTE"
    }
}
class DataRepository(
    private val local: DataSource,
    private val remote: DataSource,
    private val useRemote: Boolean
) : DataSource by if (useRemote) remote else local
fun test() {

    val local = LocalDataSource()
    val remote = RemoteDataSource()

    val repoRemote = DataRepository(
        local = local,
        remote = remote,
        useRemote = true
    )
    println(repoRemote.getData())

    val repoLocal = DataRepository(
        local = local,
        remote = remote,
        useRemote = false
    )
    println(repoLocal.getData())
}
enum class SaladType {
    GREEK,
    CAESAR,
    FRUIT
}

fun makeSalad(salad: SaladType) {
    when (salad) {
        SaladType.GREEK -> println("Making Greek Salad")
        SaladType.CAESAR -> println("Making Caesar Salad")
        SaladType.FRUIT -> println("Making Fruit Salad")
    }
}
abstract class ahmed(){
    abstract fun k(num:Int)
    abstract fun x(num:Int)
    fun test2(){
        print("test2")
    }
}

fun test2() {
    val mySalad = SaladType.CAESAR
    makeSalad(mySalad)
}


// Simple delegate that converts names to lowercase
//zy

class Person {
    var firstName: String by NameDelegate()  // Delegated!
    var lastName: String by NameDelegate()   // Delegated!
}

fun t() {
    val person = Person()

    person.firstName = "John"
    person.lastName = "DOE"

    println(person.firstName)
    println(person.lastName)
}
import kotlin.reflect.KProperty

class NameDelegate1 {
    private var formattedString: String? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        if (thisRef is FullName) {
            thisRef.counter++
        }
        return formattedString?.lowercase() ?: "NullOrEmptyString"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        formattedString = value
        if (thisRef is FullName) {
            thisRef.counter++
        }
    }
}

class FullName {
    var counter: Int = 0

    var name: String by NameDelegate1()
    var surname: String by NameDelegate1()
}

fun main() {
    val person = FullName()

    println(person.name)  // NullOrEmptyString
    println("Counter: ${person.counter}") // 1

    person.name = "Ahmed"
    println(person.name)  // ahmed
    println("Counter: ${person.counter}") // 3 (get + set)

    person.surname = "Mohamed"
    println(person.surname) // mohamed
    println("Counter: ${person.counter}") // 5
}
