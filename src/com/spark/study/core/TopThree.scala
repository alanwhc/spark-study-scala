package com.spark.study.core

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object TopThree {
  def main(args:Array[String]){
    val conf = new SparkConf()
      .setAppName("TopThree")
      .setMaster("local")
   
    val sc = new SparkContext(conf)
    
    val lines = sc.textFile("/Users/alanwang/Documents/Work/textFile/top.txt", 1)
    
    val pairs = lines.map{line => (line.toInt,line)}
    
    val sortedPairs = pairs.sortByKey(false)
    
    val sortedNumbers = sortedPairs.map{sortedPair => sortedPair._1}
    
    val sortedNumberList = sortedNumbers.take(3)
    
    sortedNumberList.foreach(n => println(n))
  }
}