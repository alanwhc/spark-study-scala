package com.spark.study.core

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object HDFSFile {
  def main(args: Array[String]){
    val conf = new SparkConf()
      .setAppName("HDFSFile")
  
    val sc = new SparkContext(conf)
  
    val lines = sc.textFile("hdfs://master:9000/passage.txt", 1)
  
    val count = lines.map {line => line.length()}.reduce(_ + _)
  
    println("Total count is: " + count)
   
  } 
}