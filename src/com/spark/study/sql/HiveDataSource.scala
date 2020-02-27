package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object HiveDataSource {
  def main(arge: Array[String]){
    val conf = new SparkConf()
      .setAppName("HiveDataSource")
    
    val hiveSession = SparkSession
      .builder
      .appName("HiveDataSource")
      .enableHiveSupport()
      .config(conf)
      .getOrCreate()
      
    hiveSession.sql("DROP TABLE IF EXISTS student_info1")
    hiveSession.sql("CREATE TABLE IF NOT EXISTS student_info1 (name String,age INT)")
    hiveSession.sql("LOAD DATA "
				+ "LOCAL INPATH '/usr/local/spark-study/resources/student_info.txt' "
				+ "INTO TABLE student_info1")
	
	  hiveSession.sql("DROP TABLE IF EXISTS student_scores1")
		hiveSession.sql("CREATE TABLE IF NOT EXISTS student_scores1 (name String,score INT)")
		hiveSession.sql("LOAD DATA "
				+ "LOCAL INPATH '/usr/local/spark-study/resources/student_scores.txt' "
				+ "INTO TABLE student_scores1")
	
	  val goodStudentsDf = hiveSession.sql("SELECT si.name,si.age,ss.score "
				+ "FROM student_info1 si "
				+ "JOIN student_scores1 ss ON si.name = ss.name "
				+ "WHERE ss.score>=80")
		
		hiveSession.sql("DROP TABLE IF EXISTS good_student_info1")
		goodStudentsDf.write.saveAsTable("good_student_info1")
		
		val goodStudentsRow =  hiveSession.table("good_student_info1").collect()
		for(row <- goodStudentsRow){
		  println(row)
		}
  }
}