package com.spark.study.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.dstream.DStream

object TransformBlacklist {
  def main(args: Array[String]){
    val conf = new SparkConf()
      .setAppName("TransformBlacklist")
      .setMaster("local[2]")
    
    val ssc = new StreamingContext(conf,Seconds(5))
    
    //模拟黑名单
    val blacklistData = Array(("Tom",true),("John",true))
    
    val blacklistRdd = ssc.sparkContext.parallelize(blacklistData, 1)
    
    //监听Socket端口数据
    val adsClickLogDStream: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)
    
    //构造DStream
    val userAdsClickLogDStream: DStream[(String,String)] = adsClickLogDStream.map(log => (log.split(" ")(1),log))
    
    //执行Transform操作
    val validUserAdsClickLogDStream: DStream[String] = userAdsClickLogDStream.transform(userAdsLog => {
      val joinedRdd = userAdsLog.leftOuterJoin(blacklistRdd)
      val filterdeRdd = joinedRdd.filter(tuple => {
        if(tuple._2._2.getOrElse(false)){
            false
        }
        else{
          true
        }
      })
      filterdeRdd.map(tuple => tuple._2._1)
    })
    
    validUserAdsClickLogDStream.print()
    
    ssc.start()
    ssc.awaitTermination()
  }
}