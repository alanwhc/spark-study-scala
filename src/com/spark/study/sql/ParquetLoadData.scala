package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object ParquetLoadData {
  def main(args: Array[String]):Unit = {
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("ParquetLoadData")
      
    val spark = SparkSession
    .builder()
    .appName("ParquetLoadData")
    .config(conf)
    .enableHiveSupport()
    .getOrCreate()
  
    val userDf = spark.read.parquet("/Users/alanwang/Documents/Work/textFile/users.parquet")
    userDf.createTempView("users")
    val userNameDf = spark.sql("select name from users")
    val nameList = userNameDf.rdd.map(user =>{
      "Name: " + user.getString(0)
    }).collect()
    
    nameList.foreach(name => println(name))
  }
}