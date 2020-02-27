package com.spark.study.core

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.util.LongAccumulator

object AccumulatorVariable {
  def main(args: Array[String]){
    val conf = new SparkConf()
      .setAppName("AccumulatorVariable")
      .setMaster("local")
    val sc = new SparkContext(conf)
    val sum = new LongAccumulator
    val numberArray = Array(1,2,3,4,5)
    val numbers = sc.parallelize(numberArray, 1)
    sc.register(sum)
    numbers.foreach(sum.add(_))
    println(sum.value)
  }
}