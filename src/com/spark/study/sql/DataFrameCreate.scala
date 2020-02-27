package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * 使用json创建Dataframe
 */
object DataFrameCreate {
  def main(args: Array[String]){
    val conf =  new SparkConf()
      .setAppName("DataFrameCreate")
  
    val spark = SparkSession
    .builder()
    .appName("DataFrameCreate")
    .config(conf)
    .enableHiveSupport()
    .getOrCreate()
    
    val sc = spark.sparkContext
    val df = spark.read.json("hdfs://master:9000/students.json")
    
    df.show()
  }
}