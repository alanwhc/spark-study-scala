package com.spark.study.classstudy

class Student(val name: String = "Leo",val age: Int = 30) {
  /*private var myName = "Leo"
  def name = "Your name is " + myName //getter方法
  def name_=(newValue: String){  //setter方法
    println("You cannot edit your name!!!")
  }
  
  def updateName(newName: String) : Unit = {
    if(newName == "Leo1") myName = newName
    else println("You cannot edit your name!")
  }*/
  
  /*
   * private[this]
   */
  /*private var myAge = 0
  def age_=(newValue: Int) {
    if(newValue >=0 && newValue <=18){
      print("You're a minor, your age is: ");
      myAge = newValue
    }else if(newValue > 18){
      print("You're an adult, your age is: ");
      myAge = newValue
    }else{
      println("Illegal age")
    }
  }
  def age = myAge
  def older(s: Student) = {
    myAge > s.myAge
  }*/
  
  /*
   * 辅助constructor
   */
  /*private var name = ""
  private var age = 0
  def this(name:String){
    this()
    this.name = name
  }
  def this(name: String, age: Int){
    this(name)
    this.age = age
  }*/
  
  /*
   * 主constructor
   */
   println("Your name is " + name + ", and your age is " + age)
  
}