package com.spark.study.core

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object LineCount {
   def main(args: Array[String]){
     val conf = new SparkConf()
       .setAppName("LineCount")
     
     val sc = new SparkContext(conf)
     
     val lines = sc.textFile("hdfs://master:9000/hello.txt", 1)
     val pairs = lines.map{line => (line,1)}
     val lineCounts = pairs.reduceByKey(_ + _)
     lineCounts.foreach(lineCount => println(lineCount._1 + " appears " + lineCount._2 + " times."))
     
   }
}

