package com.spark.study.classstudy

object Person {
  private var eyeNum = 2
  println("This is Person object")
  def getEyeNum = eyeNum
}

class Person(val name:String, val age:Int){
  def sayHello = println("Hi, " + name +", I guess your age is " + age + " years old. And you must have " + Person.eyeNum + " eyes.")
  
}