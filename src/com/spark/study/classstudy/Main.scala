package com.spark.study.classstudy

object Main {
  def main(args : Array[String]){
    val helloWorld = new HelloWorld
    helloWorld.sayHello()
    println(helloWorld.getName)
    println("====================")
    
    val leo = new Student("John", 45)
    println("====================")

    val person = new Person("Alan",29)
    person.sayHello
  }
}