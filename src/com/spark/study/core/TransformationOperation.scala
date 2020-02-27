package com.spark.study.core

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

/**
 *		transformation操作 
 */

object TransformationOperation {
    //map操作
    def map(){
       val conf = new SparkConf()
         .setAppName("map")
         .setMaster("local")
       val sc = new SparkContext(conf)
       
       val numbers = Array(1,2,3,4,5)
       val numberRdd = sc.parallelize(numbers, 1)
       val doubleNumberRdd = numberRdd.map{num => num * 2}
       
       doubleNumberRdd.foreach{num => println(num)}
    }
    
    //filter操作
    def filter(){
      val conf = new SparkConf()
        .setAppName("filter")
        .setMaster("local")
      
      val sc = new SparkContext(conf)
      val numbers = Array(1,2,3,4,5,6,7,8,9,10)
      val numberRdd = sc.parallelize(numbers, 1)
      val evenNumRdd = numberRdd.filter{num => num % 2 ==0}
      
      evenNumRdd.foreach{num => println(num)}
    }
   
    //flatMap操作
    def flatMap(){
      val conf = new SparkConf()
        .setAppName("flatMap")
        .setMaster("local")
      
      val sc = new SparkContext(conf)
      val lineArray = Array("hello you","hello me","hello world")
      val linesRdd = sc.parallelize(lineArray, 1)   //并行化集合
      val wordsRdd = linesRdd.flatMap{line => line.split(" ")}  //执行flatMap操作
      
      wordsRdd.foreach{word => println(word)}
    }
    
     //groupByKey操作
    def groupByKey(){
      val conf = new SparkConf()
        .setAppName("groupByKey")
        .setMaster("local")
      
      val sc = new SparkContext(conf)
      val scoreList = Array(Tuple2("class1",80),Tuple2("class2",75),Tuple2("class1",90),Tuple2("class2",60))
      val scores = sc.parallelize(scoreList, 1)
      val groupedScores = scores.groupByKey()      
      groupedScores.foreach(score => {
        println(score._1); 
        score._2.foreach{singleScore => println(singleScore) };
        println("=====================================")
      })
    }
    
    //reduceByKey操作
    def reduceByKey(){
      val conf = new SparkConf()
        .setAppName("reduceByKey")
        .setMaster("local")
        
      val sc = new SparkContext(conf)
      val scoreList = Array(Tuple2("class1",80),Tuple2("class2",75),Tuple2("class1",90),Tuple2("class2",60))
     
      val scores = sc.parallelize(scoreList, 1)
      val totalScores = scores.reduceByKey(_ + _)
      
      totalScores.foreach(classScore => println(classScore._1 + ": " + classScore._2))
    }
    
     //sortByKey操作
    def sortByKey(){
      val conf = new SparkConf()
        .setAppName("sortByKey")
        .setMaster("local")
        
      val sc = new SparkContext(conf)
      val scoreList = Array(Tuple2(100,"John"),Tuple2(70, "Jack"),Tuple2(85, "July"),Tuple2(50, "Tom"))
     
      val scores = sc.parallelize(scoreList, 1)
      val sortedScores = scores.sortByKey(false)
      
      sortedScores.foreach(score => println(score._1 + ": " + score._2))
    }
    
    //join & cogroup操作
    
    def joinAndCogroup(){
      val conf = new SparkConf()
        .setAppName("sortByKey")
        .setMaster("local")
        
      val sc = new SparkContext(conf)
      val nameList = Array(Tuple2(1,"Leo"),Tuple2(2,"Jack"),Tuple2(3,"Tom"))
      val scoreList = Array(Tuple2(1,90),Tuple2(2, 80),Tuple2(3, 100),Tuple2(1,70),Tuple2(2, 50),Tuple2(3, 90))
      
      val names = sc.parallelize(nameList, 1)
      val scores = sc.parallelize(scoreList, 1)
      
      //Join算子
      val nameScoreJoin = names.join(scores)
      println("Join操作")
      nameScoreJoin.foreach{nameScore => 
        println("Student id：" + nameScore._1);
        println("Student name: " + nameScore._2._1);
        println("Student score: " + nameScore._2._2);
        println("==========================")
      }
         
      //Cogroup算子
      val nameScoreCogroup = names.cogroup(scores)
      println("Cogroup操作")
      nameScoreCogroup.foreach{nameScore => 
        println("Student id：" + nameScore._1);
        println("Student name: " + nameScore._2._1);
        println("Student score: " + nameScore._2._2);
        println("==========================")
      }
    }
    
    
    def main(args: Array[String]){
       //map()
      //filter()
      //flatMap()
      //groupByKey()
      //reduceByKey()
      //sortByKey()
      joinAndCogroup()
    }
}