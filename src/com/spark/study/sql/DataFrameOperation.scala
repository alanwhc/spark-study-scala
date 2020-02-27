package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object DataFrameOperation {
   def main(args: Array[String]){
     val conf =  new SparkConf()
      .setAppName("DataFrameOperation")
  
    val spark = SparkSession
    .builder()
    .appName("DataFrameOperation")
    .config(conf)
    .enableHiveSupport()
    .getOrCreate()
    
    val sc = spark.sparkContext
    val df = spark.read.json("hdfs://master:9000/students.json")
    
    df.show()
    df.printSchema()
    df.select("name").show()
    	df.select(df("name"),df("age")+1).show()
    	df.filter(df("age")>18).show()
    	df.groupBy("age").count().show()
   }
}