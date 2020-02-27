package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StructType,StructField,StringType,IntegerType}
import org.apache.spark.sql.functions._

object DailyUV {
  def main(args: Array[String]):Unit ={
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("DailyUV")
   
    val spark = SparkSession
      .builder()
      .appName("DailyUV")
      .config(conf)
      .enableHiveSupport()
      .getOrCreate()
    //使用SparkSQL的内置函数，必须导入隐式转换
    import spark.implicits._
    
    //构造用户访问日志，并创建DataFrame
    //模拟用户访问日志，第一列为日期，第二列为id
    val userAccessLog = Array(
        "2020-02-17,1101",
        "2020-02-17,1101",
        "2020-02-17,1102",
        "2020-02-17,1102",
        "2020-02-17,1103",
        "2020-02-18,1101",
        "2020-02-18,1103",
        "2020-02-18,1102",
        "2020-02-18,1102");
    //并行化集合
    val userAccessLogRdd = spark.sparkContext.parallelize(userAccessLog, 5)
    
    //将模拟的用户访问日志RDD，转化为DataFrame
    //首先把普通RDD转为类型为Row的RDD
    val userAccessLogRowRdd = userAccessLogRdd.map(log =>{
      Row.apply(log.split(",")(0),log.split(",")(1).toInt)
    })
    //构造DataFrame元数据
    val structFields = StructType(Array(
        StructField("date",StringType,true),
        StructField("userid",IntegerType,true)));
    //创建DataFrame
    val userAccessLogRowDf = spark.createDataFrame(userAccessLogRowRdd, structFields)
    
    //使用内置函数
    //uv指的是，对用户去重以后的访问总数
    //聚合函数的用法：
    //首先，对DataFrame调用groupBy()方法，对某一列进行分组
    //然后，调用agg()方法，第一个参数，必须传入之前在groupBy()方法中出现的字段
    //第二个参数，传入countDistinct、sum、first等，spark提供的内置函数
    //内置函数中，传入的参数，也是用单引号作为前缀
    userAccessLogRowDf.groupBy("date")
      .agg('date, countDistinct('userid))
      .rdd.map(row => Row.apply(row(1),row(2)))
      .collect()
      .foreach(println)
    
  }
}