package com.spark.study.sql

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SparkSession,Row,RowFactory}
import scala.collection.mutable.Map
import org.apache.spark.sql.types.{StructType,StructField,StringType,IntegerType}
import java.sql.{DriverManager,SQLException,Connection,Statement}

object JDBCDataSource {
  def main(args:Array[String]){
    val conf = new SparkConf()
      .setAppName("JDBCDataSource")
    
    val jdbcSession = SparkSession
      .builder
      .appName("JDBCDataSource")
      .enableHiveSupport()
      .config(conf)
      .getOrCreate()
    
    val options = Map("url"->"jdbc:mysql://master:3306/testdb",
                      "dbtable"->"student_info")
    val studentInfoDf = jdbcSession.read.format("jdbc").options(options).load()
    
    options += ("dbtable"->"student_scores")
    val studentScoreDf = jdbcSession.read.format("jdbc").options(options).load()
    
    val studentRdd = studentInfoDf.rdd.map(studentInfo => (studentInfo.get(0).toString(),studentInfo.get(1).toString().toInt))
                      .join(studentScoreDf.rdd.map(studentScore =>(studentScore.get(0).toString(),studentScore.get(1).toString().toInt)))
    
    val studentRowRdd = studentRdd.map(student => {
      Row.apply(student._1,student._2._1,student._2._2)
    })
    //筛选出符合要求的人员
    val filteredStudentRdd = studentRowRdd.filter(row =>{
      if(row.getInt(2) > 80)
        true
      else
        false
    })
    
    //编程式转化DataFrame
    val structFields = StructType(Array(
        StructField("name",StringType,true),
        StructField("age",IntegerType,true),
        StructField("score",IntegerType,true)))
   
    val studentDf = jdbcSession.createDataFrame(filteredStudentRdd, structFields)
    
    //打印结果
    studentDf.foreach(row => println(row))
    
		//将DataSet中的数据保存到MySQL表中
    studentDf.foreach(student =>{
      val sql = "insert into good_student_info values ("+"'" + String.valueOf(student.get(0)) + "'," + Integer.valueOf(String.valueOf(student.get(1))) + ","+ Integer.valueOf(String.valueOf(student.get(2))) + ")"
			Class.forName("com.mysql.cj.jdbc.Driver")
			var conn: Connection = null
			var stmt: Statement = null
			try {
			  conn = DriverManager.getConnection("jdbc:mysql://master:3306/testdb", "", "")			  
			  stmt = conn.createStatement()
			  stmt.executeUpdate(sql)
			} catch {
			  case e1: SQLException => println("Cannont connect to MySQL")
			  case _:  Exception => println("Exception occurs")
			} finally {
			  if(stmt != null)
			    stmt.close()
			  if(conn != null)
			    conn.close()
			}
    })
  }
}