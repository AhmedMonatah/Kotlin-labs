package com.example.myapplication

data class Person(
    var name: String="",
    var id: Int=0,
    var gender: String="" ,
    var age: Int=10
)


fun getPerson(): Person{
    return Person("Ahmed", 101, "Male", 25)
}
fun lab() {
    val person = Person().apply {
        name = "Ahmed"
        id = 101
        gender = "Male"
        age = 25
    }
    println(person)

    person.also {  println("The list elements before adding new one: $it") }



    val message = person.let {
        if (it.age > 20) {
            "${it.name} is ${it.age} years old"
        } else {
            "Age is less than 20"
        }
    }
    println(message)

  val firstAndLast=  with(message ){
        "The first element is ${first()}," +
                " the last element is ${last()}"
    }
    println(firstAndLast)


}

fun getOp(st:String):(Int,Int)->Int {
    return when (st) {
        "add" -> { a, b -> a + b }
        "sub" -> { a, b -> a - b }
        "mult" -> { a, b -> a * b }
        "div" -> { a, b -> if(b>a) 0 else a / b }
        else -> { a, b -> a + b }
    }
}

fun setCalc(st:String){
when (st) {
    "sum" -> calc = fun (x:Int,y:Int)= x+y
        "sub" -> calc = fun (x:Int,y:Int)= x-y
        "mult" -> calc = fun (x:Int,y:Int)= x*y
        "div" -> calc = fun (x:Int,y:Int)= x/y
        }
}
var calc:((Int,Int)->Int)?= null

fun lab3(){
    val res = getOp("mult").invoke(5,6)
    println(res)

    setCalc("sum")
    println(calc?.invoke(8,3))

    setCalc("sub")
    println(calc?.invoke(8,3))
}



