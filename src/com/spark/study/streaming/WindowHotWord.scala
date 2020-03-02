package com.spark.study.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream

object WindowHotWord {
  def main(args: Array[String]){
    val conf = new SparkConf()
      .setAppName("WindowHotWord")
      .setMaster("local[2]")
    
    val ssc = new StreamingContext(conf, Seconds(1))
    
    val searchLogDStream: DStream[String] = ssc.socketTextStream("localhost", 9999)
    
    val pairDStream: DStream[(String,Int)] = searchLogDStream.map(_.split(" ")(1)).map((_,1))
    
    //执行reduceByKeyAndWindow操作
    val searchWordsCountDStream: DStream[(String,Int)] = pairDStream.reduceByKeyAndWindow(
        (v1: Int, v2: Int) => v1 + v2,
        Seconds(60), 
        Seconds(10))
        
    //执行transform操作
    val hotWordsDStream: DStream[(String,Int)] = searchWordsCountDStream.transform(searchWordsCountRdd => {
       val countWordsRdd = searchWordsCountRdd.map(tuple => (tuple._2, tuple._1))
       val sortedCountWordsRdd = countWordsRdd.sortByKey(false)
       val wordsCountRdd = sortedCountWordsRdd.map(tuple => (tuple._2, tuple._1))
       wordsCountRdd.take(3).foreach(wordCount => println(wordCount._1 + " appears " + wordCount._2 + " times."))       
       searchWordsCountRdd
    })
    
    hotWordsDStream.print()
    
    ssc.start()
    ssc.awaitTermination()
  }
}