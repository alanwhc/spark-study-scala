package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object ManuallySpecifyOptions {
  def main(args:Array[String]){
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("ManuallySpecifyOptions")
      
    val spark = SparkSession
      .builder()
      .appName("ManuallySpecifyOptions")
      .config(conf)
      .enableHiveSupport()
      .getOrCreate()
  
    val sc = spark.sparkContext
    
    val peopleDf = spark.read.format("json").load("/Users/alanwang/Documents/Work/textFile/people.json")
    peopleDf.select("name").write.save("/Users/alanwang/Documents/Work/textFile/peopleNameScala.parquet")
  }
}