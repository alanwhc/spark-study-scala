package com.spark.study.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{StreamingContext,Seconds}
import scala.collection.Map
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.{KafkaUtils,LocationStrategies,ConsumerStrategies}

object KafkaWordCount {
  def main(args: Array[String]){
    val conf = new SparkConf()
      .setAppName("KafkaWordCount")
      .setMaster("local[2]")
   
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(5))
    
    val brokers: String = "172.19.205.205:9092,47.103.113.98:9092,101.133.157.138:9092"
    val groupId: String = "customer-group"
    val topics: String = "wordcount"
    
    val topicSet: Set[String] = topics.split(",").toSet
    
    val kafkaParams = Map[String,Object](
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
        ConsumerConfig.GROUP_ID_CONFIG -> groupId,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer]
      )
      
    val msgs = KafkaUtils.createDirectStream(
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String,String](topicSet, kafkaParams))
    
    val lines = msgs.map(_.value)
    val words = lines.flatMap(line => line.split(" ").iterator)
    val wordCounts = words.map((_,1)).reduceByKey(_+_)
    
    wordCounts.print()
    
    ssc.start()
    ssc.awaitTermination()
  }
}