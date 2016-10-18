package com.newyorker.data_engg.data_engg_smack.common

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql._

object SContext {
	val conf = new SparkConf().setAppName("Docker spark execution test")
  val sc = new SparkContext(conf)
	val sqlContext = new SQLContext(sc)

}