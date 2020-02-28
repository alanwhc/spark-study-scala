package com.spark.study.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{StreamingContext,Seconds}

object HDFSWordCount {
  def main(args: Array[String]){
    val conf = new SparkConf()
      .setAppName("HDFSWordCount")
      .setMaster("local[2]")
   
    val sc: StreamingContext = new StreamingContext(conf,Seconds(5))
    
    val lines = sc.textFileStream("hdfs://master:9000/wordcount_dir")
    
    val words = lines.flatMap(_.split(" ").iterator)
    val wordCounts = words.map((_,1)).reduceByKey(_+_)
    
    wordCounts.print()
    
    sc.start()
    sc.awaitTermination()
  }
}