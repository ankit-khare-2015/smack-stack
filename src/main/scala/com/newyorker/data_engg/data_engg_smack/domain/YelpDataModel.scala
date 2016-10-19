package com.newyorker.data_engg.data_engg_smack.domain


import com.datastax.spark.connector.CassandraRow
import org.joda.convert.ToString
import scala.collection.mutable.WrappedArray

class YelpDataModel {
}

case class YelpTip(user_id:String,text:String,business_id:String,likes:Long,date:String,type_1:String)

case class YelpBusinessCategories(business_id:String ,cat:Seq[String])

case class YelpBusiness(
		business_id:String,
		full_address:String,
		city:String,
		review_count:Long,
		name:String,
		longitude:Double,
		state:String,
		stars:Double,
		latitude:Double,
		businesstype:String)


class CassandraRowWrapper(row: CassandraRow) {
	def toYelpTip = {
			YelpTip(
					user_id = row.getString("user_id"),
					text    = row.getString("text"),
					business_id = row.getString("business_id"),
					likes =row.getLong("likes"),
					date= row.getString("date"),
					type_1 = row.getString("type_1")
					)
	}
	
		def toYelpBusiness = {
		  	YelpBusiness(
					business_id = row.getString("business_id"),
					full_address = row.getString("full_address"),
					city= row.getString("city"),
					review_count= row.getLong("review_count"),
					name = row.getString("name"),
					longitude = row.getDouble("longitude"),
					state= row.getString("state"),
					stars= row.getDouble("stars"),
					latitude= row.getDouble("latitude"),
					businesstype= row.getString("businesstype")
					)
			
	 }
		def toYelpBusinessCategories = {
		  YelpBusinessCategories(
					business_id = row.getString("business_id"),
					cat = row.get[Seq[String]]("cat")
					)
		
	}
}