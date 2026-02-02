package com.example.myapplication

import android.R

abstract class Shape {
    protected  var dimValue: Double = 0.0

    fun setDim(d: Double) {
        dimValue = d
    }

    fun getDim(): Double {
        return dimValue
    }

    abstract fun calcArea(): Double
}
class Rectangle : Shape {
    private var width: Double = 0.0
    private var height: Double = 0.0


    constructor(w: Double, h: Double) {
        width = w
        height = h
    }

    fun setHeight(h: Double) {
        height = h
    }

    fun getHeight(): Double {
        return height
    }

    override fun calcArea(): Double {
        return width * height
    }
}
class Circle : Shape {
    private var radius: Double = 0.0


    constructor( name:String,  age:Int){

    }
    constructor(r: Double) {
        radius = r
    }

    override fun calcArea(): Double {
        return Math.PI * radius * radius
    }
}
class Triangle : Shape {
    private var base: Double = 0.0
    private var height: Double = 0.0

    constructor()

    constructor(b: Double, h: Double) {
        base = b
        height = h
    }

    fun setHeight(h: Double) {
        height = h
    }

    fun getHeight(): Double {
        return height
    }

    override fun calcArea(): Double {
        return 0.5 * base * height
    }
}
class Picture {

    fun sumAreas(s1: Shape, s2: Shape, s3: Shape): Double {
        return s1.calcArea() + s2.calcArea() + s3.calcArea()
    }
}

fun main(){

    val r = Rectangle(1.87, 2.35)
    println("rectangle area = ${r.calcArea()}")

    val c = Circle(2.6)
    println("circle area = ${c.calcArea()}")

    val t = Triangle(6.8, 2.21)
    println("triangle area = ${t.calcArea()}")
    val picture = Picture()
    val totalArea = picture.sumAreas(r, c, t)
    println("total area = $totalArea")

}
