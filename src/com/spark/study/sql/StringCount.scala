package com.spark.study.sql

import org.apache.spark.sql.expressions.{UserDefinedAggregateFunction,MutableAggregationBuffer}
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StructField,StringType,DataType,StructType,IntegerType}

class StringCount extends UserDefinedAggregateFunction {
  //inputSchema，指的是输入数据的类型
  override def inputSchema: StructType = {
    StructType(Array(StructField("str",StringType,true)))
  }
  
  //bufferSchema，代表进行聚合时所处理数据的类型
  override def bufferSchema : StructType = {
    StructType(Array(StructField("count",IntegerType,true)))
  }
  
  //dataType，代表函数返回值的类型
  override def dataType: DataType = {
    IntegerType
  }
  
  override def deterministic: Boolean = {
    true
  }
  
  //为每个分组的数据执行初始化操作
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = 0
  }
  
  //每个分组有新值产生时，进行分组对应的聚合值计算
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    if(input.isNullAt(0)) return
    buffer(0) = buffer.getAs[Int](0) + 1
  }
  
  //由于spark是分布式，所以一个分组的数据，可能会在不同的节点上进行局部聚合，就update
  //但是，最后一个分组，在各个节点上的聚合值，要进行merge，也就是合并
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer1.getAs[Int](0) + buffer2.getAs[Int](0)
  }
  
  //一个分组的聚合值，如何通过中间的缓存聚合值，最后返回一个最终的聚合值
  override def evaluate(buffer: Row): Any = {
    buffer.getAs[Int](0)
  }
  
}