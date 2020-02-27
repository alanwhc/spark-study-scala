package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SaveMode

object ParquetMergeSchema {
  def main(args: Array[String]){
    val conf = new SparkConf()
      .setAppName("ParquetMergeSchema")
      .setMaster("local")
      
    val spark = SparkSession
    .builder()
    .appName("ParquetMergeSchema")
    .config(conf)
    .enableHiveSupport()
    .getOrCreate()
    
    val sc = spark.sparkContext
    
    //导入隐式转化
    import spark.implicits._
    
    //首先手动创建一个dataframe作为学生的基本信息，并写入Parquet文件中
    val studentWithNameAge = Array(("Leo",23),("John",25)).toSeq
    val studentWithNameAgeDf = sc.parallelize(studentWithNameAge, 2).toDF("name","age")
    studentWithNameAgeDf.write.format("parquet").mode(SaveMode.Append).save("hdfs://master:9000/spark-study/students")
    
    //创建一个DataFrame，作为学生的成绩信息，并写入一个Parquet文件中
    val studentWithGrade = Array(("Jason","A"),("Jennifer","B")).toSeq
    val studentWithGradeDf = sc.parallelize(studentWithGrade, 2).toDF("name","grade")
    studentWithGradeDf.write.format("parquet").mode(SaveMode.Append).save("hdfs://master:9000/spark-study/students")
    
    //用merge schema的方式，读取students表中的数据，进行元数据的合并
    val studentsDf = spark.read.option("mergeSchema", "true").load("hdfs://master:9000/spark-study/students")
    studentsDf.printSchema()
    studentsDf.show()
  }
}