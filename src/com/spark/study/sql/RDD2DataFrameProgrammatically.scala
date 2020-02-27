package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType

object RDD2DataFrameProgrammatically extends App {
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
    
    //构造类型为Row的普通RDD
    val studentRdd = sc.textFile("/Users/alanwang/Documents/Work/textFile/students.txt", 1)
      .map(line => Row(line.split(",")(0).toInt,
                      line.split(",")(1),
                      line.split(",")(2).toInt))
    
    //编程方式动态构造元数据
    val structType = StructType(Array(
        StructField("id",IntegerType,true),
        StructField("name",StringType,true),
        StructField("age",IntegerType,true)))
    
    //进行RDD到DataFrame的转换
    val studentDf = spark.createDataFrame(studentRdd, structType)
    
    //创建临时表
    studentDf.createTempView("students")
    
    val enquiryDf = spark.sql("select * from students where age >= 18")
    
    //DataFrame到RDD的转化，并进行打印
    val enquiryRdd = enquiryDf.rdd
      .collect()
      .foreach(row => println(row))
    
    spark.catalog.dropTempView("students")
}