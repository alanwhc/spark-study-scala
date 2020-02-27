package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object GenericLoadSave {
  def main(args: Array[String]){
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
    
    val usersDf = spark.read.load("/Users/alanwang/Documents/Work/textFile/users.parquet")
    usersDf.select("name", "favorite_color").write.save("/Users/alanwang/Documents/Work/textFile/targetScala.parquet")
  }
}