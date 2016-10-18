package com.newyorker.data_engg.data_engg_smack

import com.newyorker.data_engg.data_engg_smack.common._
import com.newyorker.data_engg.data_engg_smack.domain._
import com.datastax.spark.connector.cql.TableDef
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import com.datastax.spark.connector._

/**
 * @author ${user.name}
 */
object YelpTipDataLoader {


	def main(args : Array[String]) {
		saveRddToCaccendra(getRdd(loadJsonFile("yelp_academic_dataset_tip.json")), getTableDef())
	}

	def loadJsonFile(fileName:String):DataFrame={
		val jsonData = SContext.sqlContext.read.json(fileName)
		return jsonData
	}
	
	def getTableDef():TableDef ={
	  val tipTableDef = TableDef.fromType[YelpTip]("yelp_data","tips_scala")
	  return tipTableDef 
	}
	
	def getRdd(tips:DataFrame ): RDD[YelpTip]={
	  val yelpRdd = tips.map(t => YelpTip(t.getAs[String]("user_id")
	      ,t.getAs[String]("text")
	      ,t.getAs[String]("business_id")
	      ,t.getAs[Long]("likes")
	      ,t.getAs[String]("date")
	      ,t.getAs[String]("type")))
	  return yelpRdd    
	}
	
	def saveRddToCaccendra(rddValue:RDD[YelpTip],tableDef:TableDef){
	  rddValue.saveAsCassandraTableEx(tableDef)
	}
	
}
