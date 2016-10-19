package com.newyorker.data_engg.data_engg_smack


import com.newyorker.data_engg.data_engg_smack.common._
import com.newyorker.data_engg.data_engg_smack.domain._
import com.datastax.spark.connector.cql.TableDef
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import com.datastax.spark.connector._

object YelpBusinessDataLoader {


	def main(args : Array[String]) {
		saveRddToCaccendra(getRdd(loadJsonFile("yelp_academic_dataset_business.json")), getTableDef())
		saveBusinessCatRddToCaccendra(getBusinessCatRdd(loadJsonFile("yelp_academic_dataset_business.json")), getBusinessCategoriesTableDef())
	}

	def loadJsonFile(fileName:String):DataFrame={
			val jsonData = SContext.sqlContext.read.json(fileName)
					return jsonData
	}

	def getTableDef():TableDef ={
			val businessTableDef = TableDef.fromType[YelpBusiness]("yelp_data","yelp_business")
					return businessTableDef 
	}
	
	def getBusinessCategoriesTableDef():TableDef ={
			val businessCatTableDef = TableDef.fromType[YelpBusinessCategories]("yelp_data","yelp_business_categories")
					return businessCatTableDef 
	}

	def getRdd(tips:DataFrame ): RDD[YelpBusiness]={
			val yelpRdd = tips.map(t => YelpBusiness(
			    t.getAs[String]("business_id"),
					t.getAs[String]("full_address"),
					t.getAs[String]("city"),
					t.getAs[Long]("review_count"),
					t.getAs[String]("name"),
					t.getAs[Double]("longitude"),
					t.getAs[String]("state"),
					t.getAs[Double]("stars"),
					t.getAs[Double]("latitude"),
					t.getAs[String]("type")     
					))
					return yelpRdd    
	}
	
	def getBusinessCatRdd(tips:DataFrame ): RDD[YelpBusinessCategories]={
			val yelpRdd = tips.map(t => YelpBusinessCategories(
			    t.getAs[String]("business_id"),
					t.getAs[Seq[String]]("categories")
					))
					return yelpRdd    
	}
	
	def saveRddToCaccendra(rddValue:RDD[YelpBusiness],tableDef:TableDef){
		rddValue.saveAsCassandraTableEx(tableDef)
	}
	
	def saveBusinessCatRddToCaccendra(rddValue:RDD[YelpBusinessCategories],tableDef:TableDef){
		rddValue.saveAsCassandraTableEx(tableDef)
	}
}