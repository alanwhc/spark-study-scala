package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SparkSession,Row}
import org.apache.spark.sql.types.{StructType,StructField,StringType,DoubleType}
import org.apache.spark.sql.functions._

object DailySales {
  def main(args: Array[String]):Unit = {
    val conf = new SparkConf()
       .setMaster("local")
       .setAppName("DailySales")
    
    val sparkSession = SparkSession
      .builder()
      .config(conf)
      .enableHiveSupport()
      .appName("DailySales")
      .getOrCreate()
    
    import sparkSession.implicits._
    val userSaleLog = Array(
        "2020-02-17,100.04,1101",
        "2020-02-17,78.11,1101",
        "2020-02-17,67.98,1102",
        "2020-02-17,200.29",//用户id丢失
        "2020-02-17,87.29,1103",
        "2020-02-18,1000.23",//用户id丢失
        "2020-02-18,789.21,1103",
        "2020-02-18,123.21,1102",
        "2020-02-18,28.12,1102");
    
    val userSaleLogRdd = sparkSession.sparkContext.parallelize(userSaleLog, 5)
    
    //有效日志过滤，如果用户id丢失，则不统计交易额
    val filteredUserSaleLogRdd = userSaleLogRdd.filter(log => if(log.split(",").length == 3) true else false)
    
    val userSaleLogRowRdd = filteredUserSaleLogRdd.map(log => {
      Row.apply(log.split(",")(0),
                log.split(",")(1).toDouble)
    })
    
    val structFields = StructType(Array(
        StructField("date",StringType,true),
        StructField("sales",DoubleType,true)));
    
    val userSaleLogDf = sparkSession.createDataFrame(userSaleLogRowRdd, structFields)
    //每日销售额的统计
    userSaleLogDf.groupBy("date")
      .agg('date,sum('sales))
      .rdd.map(row => Row.apply(row(1),row(2)))
      .collect()
      .foreach(println)
  }
}