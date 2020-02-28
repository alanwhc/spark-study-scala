package com.spark.study.streaming

/**
 * WordCount示例
 */

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{StreamingContext,Seconds}
import org.apache.spark.streaming.dstream.{ReceiverInputDStream,DStream}

object WordCount {
  def main(args: Array[String]){
    //创建SparkConf对象
    val conf: SparkConf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("WordCount")
      
    //创建JavaStreamingContext对象
    val sc: StreamingContext = new StreamingContext(conf,Seconds(1))
    
    //读取Socket数据流
    val lines: ReceiverInputDStream[String] = sc.socketTextStream("localhost", 9999)
    
    //对RDD进行算子操作
    val words: DStream[String] = lines.flatMap(line => {line.split(" ").iterator})
    val wordCounts = words.map((_,1)).reduceByKey(_+_)
    
    //打印结果
    wordCounts.print()
     
    sc.start()
    sc.awaitTermination()
  }
}