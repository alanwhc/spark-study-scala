package com.spark.study.core

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

/**
 * action操作实战
 */
 
object ActionOperation {
  /*
   * reduce算子
   */
  def reduce(){
    
    val conf = new SparkConf()
        .setAppName("reduce")
        .setMaster("local")
    val sc = new SparkContext(conf)
    val numberArray = Array(1,2,3,4,5,6,7,8,9,10)
    val numbers = sc.parallelize(numberArray, 1)
 
    val sum = numbers.reduce(_ + _)
    
    println(sum)
  }
  
  /*
   * collect算子
   */
  def collect(){
    val conf = new SparkConf()
        .setAppName("collect")
        .setMaster("local")
    val sc = new SparkContext(conf)
    val numberArray = Array(1,2,3,4,5,6,7,8,9,10)
    val numbers = sc.parallelize(numberArray, 1)

    val doubleNumbers = numbers.map(num => num * 2);
    
    val doubleNumberArray = doubleNumbers.collect()
    for(num <- doubleNumberArray){
      println(num)
    }
  }
  
  /*
   * count算子
   */
  def count(){
    val conf = new SparkConf()
        .setAppName("count")
        .setMaster("local")
    val sc = new SparkContext(conf)
    val numberArray = Array(1,2,3,4,5,6,7,8,9,10)
    val numbers = sc.parallelize(numberArray, 1)
    
    val count = numbers.count()
    println(count)
  }
  
  /*
   * take(n)算子
   */
  def take(){
    val conf = new SparkConf()
        .setAppName("take")
        .setMaster("local")
    val sc = new SparkContext(conf)
    val numberArray = Array(1,2,3,4,5,6,7,8,9,10)
    val numbers = sc.parallelize(numberArray, 1)
    val topThreeNumbers = numbers.take(3)
    for(n <- topThreeNumbers){
      println(n)
    }
  }
  
  /*
   * countByKey算子
   */
  def countByKey(){
    val conf = new SparkConf()
        .setAppName("countByKey")
        .setMaster("local")
    val sc = new SparkContext(conf)
    val studentArray = Array(Tuple2("class1","leo"),Tuple2("class2","jack"),Tuple2("class1","tom"),Tuple2("class2","david"),Tuple2("class2","john"))
    val students = sc.parallelize(studentArray, 1)
  
    val studentCount = students.countByKey()
    println(studentCount)
    
  }
  
 
  def main(args: Array[String]){
   // reduce()
   // collect()
    // count()
    //take()
    countByKey()
  }
  
}