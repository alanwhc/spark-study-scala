package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object RDD2DataFrameReflection extends App {
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("RDD2DataFrameReflection")
      
    val spark = SparkSession
    .builder()
    .appName("RDD2DataFrameReflection")
    .config(conf)
    .enableHiveSupport()
    .getOrCreate()
    
    val sc = spark.sparkContext
    
    //在Scala中使用反射方式，进行RDD到DataFrame的转换，需要手动导入隐式转换
    import spark.implicits._
    
    val lines = sc.textFile("/Users/alanwang/Documents/Work/textFile/students.txt", 1)
    
    case class Student(id:Int, name:String, age:Int)

    val students = lines
        .map(line => line.split(","))
        .map(stu => Student(stu(0).trim().toInt,stu(1),stu(2).trim().toInt))
  
    val studentDf = students.toDF()
    studentDf.createTempView("students")
    
    val enquiryDF = spark.sql("select * from students where age >= 18")
    val enquiryRdd = enquiryDF.rdd
    
    //在Scala中，row中数据的顺序，是按照期望的顺序排列的，且row的使用，比Java更加丰富
    //1.通过row id获取row的列
    enquiryRdd.map(row => Student(row(0).toString().toInt,row(1).toString(),row(2).toString().toInt))
      .collect()
      .foreach(stu => println(stu.id + ": " + stu.name + ", " + stu.age))
      
    //2.使用row的getAs方法，获取指定列名的列
   enquiryRdd.map(row => Student(row.getAs[Int]("id"),row.getAs[String]("name"),row.getAs[Int]("age")))
     .collect()
     .foreach(stu => println(stu.id + ": " + stu.name + ", " + stu.age))
     
   //3.使用row的getValuesMap方法，获取指定列的值，返回的是map类型
   enquiryRdd.map(row => {
     val map = row.getValuesMap[Any](Array("id","name","age"));
     Student(map("id").toString().toInt,map("name").toString(),map("age").toString().toInt)
   })
     .collect()
     .foreach(stu => println(stu.id + ": " + stu.name + ", " + stu.age))
}