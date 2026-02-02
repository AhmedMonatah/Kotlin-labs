package com.example.myapplication

import java.util.Random

fun lab1(){

    val x = readLine()
    val name = if (x.isNullOrBlank()) "guest" else x
    println("Hello $name")
}

fun lab2(){


    val numbers = IntArray(100)

    for (i in numbers.indices) {
        numbers[i] = (0 .. 100).random()
    }


    for (num in numbers) {
        if (num <= 10) {
            println(num)
        }
    }

}
fun lab3() {
    while (true) {
        print("enter first number (or q to quit): ")
        val input1 = readLine()
        if (input1 == "q") break

        val num1 = input1?.toDoubleOrNull()
        if (num1 == null) {
            println("error: not a number")
            continue
        }

        print("enter operator (+, -, *, /): ")
        val op = readLine()

        print("enter second number: ")
        val num2 = readLine()?.toDoubleOrNull()
        if (num2 == null) {
            println("error: not a number")
            continue
        }

        val result = when (op) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> {
                if (num2 == 0.0) {
                    println("error: can't divide by zero")
                    continue
                } else {
                    num1 / num2
                }
            }
            else -> {
                println("error: invalid operator")
                continue
            }
        }

        println("result = $result")
    }

    println("finished")
}


fun main() {
    val n = 6
    for (i in 0 .. n) {
        val leftStars = "*".repeat(i + 1)
        val leftSpaces = " ".repeat(n - i )
        val rightStars = (0..i).joinToString(" ") { "*" }
        println("$leftStars$leftSpaces$leftSpaces  $rightStars")
    }
}


fun lab5(){
    print("enter first name: ")
    val f=readLine()
    print("enter last name: ")
    val l=readLine()
    if(f.isNullOrBlank() ||l.isNullOrBlank()){
        println("error:name is null")
        return
    }
    val result=f+" "+l
    print("your full name is :"+result)

}