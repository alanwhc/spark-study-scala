package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StructType,StructField,StringType}

object UDAF {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("UDAF")
      .setMaster("local")
    
   val sparkSession = SparkSession
     .builder()
     .config(conf)
     .appName("UDAF")
     .enableHiveSupport()
     .getOrCreate()
    
   //构造模拟数据
   val names = Array("Leo","Marry","Tom","Jack","Leo","Jack","Jack","Tom","Tom","Tom")
   val namesRdd = sparkSession.sparkContext.parallelize(names, 1)
   val namesRowRdd = namesRdd.map(name =>{Row.apply(name)})
   val structFields = StructType(Array(
       StructField("name",StringType,true)))
   val namesDf = sparkSession.createDataFrame(namesRowRdd, structFields)
   
   //注册临时表
   namesDf.createTempView("names")
   
   //定义和注册自定义函数
   //定义函数：自己写匿名函数
   //注册函数：sparkSession.udf.register()
   sparkSession.udf.register("strCount", new StringCount)
   
   //使用自定义函数
   sparkSession.sql("SELECT name,strCount(name) FROM names GROUP BY name")
     .collect()
     .foreach(println)
     
  }
}