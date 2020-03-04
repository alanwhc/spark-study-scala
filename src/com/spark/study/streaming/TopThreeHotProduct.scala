package com.spark.study.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{StreamingContext,Seconds}
import org.apache.spark.streaming.dstream.{ReceiverInputDStream,DStream}
import org.apache.spark.sql.{Row,SparkSession,Dataset}
import org.apache.spark.sql.types.{StructField,StructType,StringType,IntegerType}

object TopThreeHotProduct {
  def main(args: Array[String]){
    val conf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("TopThreeHotProduct")
    
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(1))
    
    val categoryProductLog: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 9999)
    val categoryProductCountDstream: DStream[(String,Int)] = categoryProductLog.map(categoryProduct => (categoryProduct.split(" ")(2) + "_" + categoryProduct.split(" ")(1),1))
    val categoryProductWindowDstream: DStream[(String,Int)] = categoryProductCountDstream.reduceByKeyAndWindow(
        (v1: Int, v2: Int) => v1 + v2, 
        Seconds(60),
        Seconds(10))
        
    categoryProductWindowDstream.foreachRDD(categoryProductRdd =>{
      //将RDD转换为Row类型
      val categoryProductRowRdd = categoryProductRdd.map(categoryProduct => 
        Row.apply(categoryProduct._1.split("_")(0),
                  categoryProduct._1.split("_")(1),
                  categoryProduct._2))
      //创建StructField
      val structFields: List[StructField] = List(
          StructField("category",StringType,true),
          StructField("product",StringType,true),
          StructField("count",IntegerType,true))
      
      //创建SparkSession对象
      val sparkSession: SparkSession = SparkSession
          .builder()
          .enableHiveSupport()
          .config(conf)
          .getOrCreate()
          
      //创建DataFrame
      val categoryProductDs = sparkSession.createDataFrame(categoryProductRowRdd, StructType(structFields))
      
      //将60s内的商品创建一个临时表
      categoryProductDs.createTempView("product_click_log")
      
      //SQL查询排名前三的
      val topThreeHotProductDs: Dataset[Row] = sparkSession.sql("" 
          + "SELECT category,product,count "
          + "FROM ("
            + "SELECT "
              + "category,"
              + "product,"
              + "count,"
              + "row_number() OVER (PARTITION BY category ORDER BY count DESC) rank "
            + "FROM product_click_log"
          + ") tmp "
          + "WHERE rank <= 3")
     
      topThreeHotProductDs.show()
      sparkSession.catalog.dropTempView("product_click_log")
    })    
    
    ssc.start()
    ssc.awaitTermination()
  }
}