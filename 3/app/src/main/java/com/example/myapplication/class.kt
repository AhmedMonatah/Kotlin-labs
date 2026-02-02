package com.example.myapplication



class X(var z: Int, var y: Int) {

    init {
        println("1: $z + $y");
    }
    init {
        println("2: $z - $y");
    }
    init {
        println("3: $z * $y");
    }
    constructor(s: String, e: Int) : this(5, 8) {

        println("this is z = $z")
        println("this is  y= $y")

    }
}
//data class User(var name: String, var age: Int)

//fun main() {
//    val u1 = User("Ahmed", 25)
//    val u2 = u1.copy(age = 26)
//
//    u2.name = "Ali" // ممكن تعديل مباشرة
//    println(u2) // User(name=Ali, age=26)
//}

interface Printer {
    fun local()
    fun remote()
}

class SimplePrinter : Printer {
    override fun local() {
        println("Local...")
    }

    override fun remote() {
        println("Remote....")
    }
    fun print(){
        println("print")
    }

}


class ReportPrinter(del: SimplePrinter) : Printer by del{

}


class User {

}

class Student{
    var user:String
    get() {
        return "Ahmed"
    }
    set(value) {
        println("this is $value")
    }
}
//fun main() {
//val x:Printer=ReportPrinter(SimplePrinter())
//    x.local()
//    x.remote()
//}

/*
fun main() {
    val obj=X("hello", 10)
    println("z = ${obj.z}, y = ${obj.y}")
}*/
data class Address(var name: String,var city: String, var street: String, var building: Building)


class Person(var name: String = "") {

    inner class kk(
        var city: String = "",
        var street: String = "",
        var building: Building = Building.APARTMENT
    ) {
        override fun toString(): String {
            return "$name, $city, $street, $building"
        }
    }
}

enum class Building {
    VILLA,
    APARTMENT
}

fun main() {
    val address = Address(
        name = "ahmed",
        city = "qena",
        street = "tahrir street",
        building = Building.VILLA
    )

   print(address.toString())
}


interface MyNumber {
    fun getNext(): Int
    fun getcount():Int
    fun reset()

}
class ByThrees : MyNumber {

    private var value:Int = 0

    override fun getNext(): Int {
        value += 3
        return value
    }

    override fun reset() {
        value = 0
    }
    override fun getcount():Int {
        return value

    }
}
fun ad() {
    val num: MyNumber = ByThrees()
    println("this is first: "+num.getNext())
    println("this is second: "+num.getNext())
    println("this is third: "+num.getNext())
    println("this is count: "+num.getcount())
    num.reset()
    println(num.getNext())

}

class pb(val name:String="zzname",var address:String="zzaddress"){
    init {
        println("hey bro")
        println("$name and $address")
    }
     constructor(x:Int,y:String):this("ahmed","ahmed"){
        println("hey bro")
        println("$x and $y")
    }
}

fun test(){
    pb(2,"ahmed")
}