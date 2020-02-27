package com.spark.study.core

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object GroupTopThree {
  def main(args: Array[String]){
    val conf = new SparkConf()
      .setAppName("GroupTopThree")
      .setMaster("local")
      
    val sc = new SparkContext(conf)
    val lines = sc.textFile("/Users/alanwang/Documents/Work/textFile/score.txt", 1)
    val pairs = lines.map{line => (line.split(" ")(0), line.split(" ")(1).toInt)}
    val groupedPairs = pairs.groupByKey()
    
    val topThreeScores = groupedPairs.map(groupPair =>{
      val key = groupPair._1;
      val values = groupPair._2;
      val sortValues = values.toList.sortWith(_>_).take(3);
      (key,sortValues)
      }
    )
    
    topThreeScores.foreach(topThree => {
      println("class: " + topThree._1);
      topThree._2.foreach(score => println(score));
      println("=========================================")
    })
  }
}