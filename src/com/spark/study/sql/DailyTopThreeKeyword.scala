package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SparkSession,Row}
import scala.collection.immutable.Map
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.sql.types.{StructField,StringType,LongType,StructType}

object DailyTopThreeKeyword {
  def main(args:Array[String]):Unit = {
    val conf = new SparkConf()
      .setAppName("DailyTopThreeKeyword")
      
    val sparkSession = SparkSession
      .builder()
      .config(conf)
      .appName("DailyTopThreeKeyword")
      .enableHiveSupport()
      .getOrCreate()
      
   val sc = sparkSession.sparkContext
   
   //步骤1：从HDFS中读取文件，并存储至RDD中
   //val rawRdd = sc.textFile("/Users/alanwang/Documents/Work/textFile/keyword.txt", 3)
   val rawRdd = sc.textFile("hdfs://master:9000/spark-study/keyword.txt", 3)
   
   //步骤2：
   //  1）初始化过滤的map，并根据map中的key进行过滤
   val filterMap = Map("city"->List("beijing"),"platform"->List("android"),"version"->List("1.0","1.2","1.5","2.0"))
   //  2）创建广播变量，在filter算子中使用广播变量操作构造新的rdd
   val broadcastFilterMap = sc.broadcast(filterMap)
   val filteredRdd = rawRdd.filter{raw => {
     val queryMap = broadcastFilterMap.value
     val city = raw.split(",")(3);val platform = raw.split(",")(4);val version = raw.split(",")(5)
     if(queryMap("city").size > 0 && !queryMap("city").contains(city))
       false
     else if(queryMap("platform").size > 0 && !queryMap("platform").contains(platform))
       false
     else if(queryMap("version").size > 0 && !queryMap("version").contains(version))
       false
     else
       true
   }}

   //步骤3：
   // 1)将数据转换为(日期_搜索词,用户)格式，数据进行分组
   val dateKeywordUserRdd = filteredRdd.map(log => {
     val date = log.split(",")(0)
     val user = log.split(",")(1)
     val keyword = log.split(",")(2)
     (date + "_" + keyword, user)
   })  
   // 2)对数据进行分组
   val groupedDateKeywordUserRdd = dateKeywordUserRdd.groupByKey()
   // 3)数据去重，并组合成(日期_搜索词,uv)的格式
   val dateKeywordUvRdd = groupedDateKeywordUserRdd.map(keyword =>{
     val dateKeyword = keyword._1
     val users = keyword._2.iterator
     val distinctUsers = ArrayBuffer[String]()
     while(users.hasNext){
       val user = users.next()
       if(!distinctUsers.contains(user))
         distinctUsers += (user)
     }
     (dateKeyword,distinctUsers.size)
   })
   
   //步骤4：将3中获得的Rdd转换为Row类型(日期，搜索词，uv)的Rdd，并创建DataFrame
   val dateKeywordUvRowRdd = dateKeywordUvRdd.map(dateKeyWord =>{
     Row.apply(dateKeyWord._1.split("_")(0),
               dateKeyWord._1.split("_")(1),
               dateKeyWord._2.toLong)
   }) 
      
   val structFields = Array(
       StructField("date",StringType,true),
       StructField("keyword",StringType,true),
       StructField("uv",LongType,true))
   
   val dateKeyWordUvDf = sparkSession.createDataFrame(dateKeywordUvRowRdd, StructType(structFields))
   
   //步骤5：将DataFrame注册为临时表，使用Spark SQL开窗函数查询总uv排名前三的数据
   dateKeyWordUvDf.createTempView("daily_topthree_uv")
   val dailyTopThreeKeywordDs = sparkSession.sql(""
       +"SELECT date,keyword,uv "
       +"FROM ("
         +"SELECT "
           +"date,"
           +"keyword,"
           +"uv,"
           +"row_number() OVER (PARTITION BY date ORDER BY uv DESC) rank "
         +"FROM daily_topthree_uv"
       +") tmp "
       +"WHERE rank <= 3");
   sparkSession.catalog.dropTempView("daily_topthree_uv")
   
   //步骤6：
   //1）将DataFrame转换为RDD，映射为(日期,搜索词_uv)的格式
   val topThreeKeywordUvRdd = dailyTopThreeKeywordDs.rdd.map(dailyTopThree => {
     (dailyTopThree.get(0).toString(),
      dailyTopThree.get(1) + "_" + dailyTopThree.get(2))
   })
   //2）对1）中映射后的数据进行分组，并统计分组内uv的总数，最后映射为(uv,日期,搜索词)的格式
   val uvDateKeywordRdd = topThreeKeywordUvRdd.groupByKey().map(dateKeywordUv => {
     var dateKeyword: String = dateKeywordUv._1  //日期
     var totalUv: Long = 0L  //日期_搜索词对应的总uv
     dateKeywordUv._2.foreach(keywordUv => {
       totalUv += keywordUv.split("_")(1).toLong
       dateKeyword += "," + keywordUv
     })
     (totalUv,dateKeyword)
   })
   
   //步骤7：使用sortByKey算子，对日期+搜索词对应的总Uv进行倒序排序
   val sortedUvDateKeywordRdd = uvDateKeywordRdd.sortByKey(false)
   
   //步骤8：将排序后的数据映射为日期,搜索词，uv的Row类型的Rdd
   val resultDateKeywordUvRdd = sortedUvDateKeywordRdd.flatMap(sortedUvDateKeyword => {
     val result = ArrayBuffer[Row]()
     val dateKeywordUv: Array[String] = sortedUvDateKeyword._2.split(",") //格式为(日期,关键词_uv)的Array
     Array(Row.apply(dateKeywordUv(0),  //日期
                     dateKeywordUv(1).split("_")(0),  //搜索词
                     dateKeywordUv(1).split("_")(1).toLong), //uv
           Row.apply(dateKeywordUv(0),  //日期
                     dateKeywordUv(2).split("_")(0),   //搜索词
                     dateKeywordUv(2).split("_")(1).toLong), //uv
           Row.apply(dateKeywordUv(0),  //日期
                     dateKeywordUv(3).split("_")(0),   //搜索词
                     dateKeywordUv(3).split("_")(1).toLong)  //uv
         ).iterator
   })
   
   //步骤9：将RDD转换为DataFrame并存至Hive表中
   val resultDs = sparkSession.createDataFrame(resultDateKeywordUvRdd, StructType(structFields))
   sparkSession.sql("DROP TABLE IF EXISTS daily_topthree_keyword_uv_scala")
   resultDs.write.saveAsTable("daily_topthree_keyword_uv_scala")
  }
}