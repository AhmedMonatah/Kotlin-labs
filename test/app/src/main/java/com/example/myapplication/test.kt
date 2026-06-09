package com.example.myapplication
sealed class Order(val price: Double) {

    fun printPrice() {
        println("price is $price")
    }

    class Burger : Order(50.0)
    class Coffee : Order(20.0)
    class Pizza : Order(80.0)
}

fun main(){
   // val xInt:Int?=x as? Int
//    val order: Order = Order.Pizza()
//    var x=23
//    println(x.dec())
//    order.printPrice()
//
//    when (order) {
//        is Order.Burger -> println("This is a burger")
//        is Order.Coffee -> println("This is a coffee")
//        is Order.Pizza -> println("This is a pizza")
//    }
    val contacts = listOf(
        Contact("Amit", "+9199XXX11111"),
        Contact("Messi", "+9199XXX22222"),
        Contact("Ronaldo", "+9199XXX33333"))
    val nameToNumberMap = contacts.associateBy( {it.name}, {it.phoneNumber})
    println(nameToNumberMap)
}
data class Contact(val name: String, val phoneNumber: String)
