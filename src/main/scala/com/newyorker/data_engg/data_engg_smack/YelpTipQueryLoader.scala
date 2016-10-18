package com.newyorker.data_engg.data_engg_smack

import com.newyorker.data_engg.data_engg_smack.domain.CassandraRowWrapper
import com.datastax.spark.connector.CassandraRow
import com.newyorker.data_engg.data_engg_smack.common.SContext
import com.datastax.spark.connector._

object YelpTipQueryLoader {
  
  def main(args : Array[String]) {
    queryYelpTipsLoader()
    queryYelpBusinessDataLoader()
    joinQueryBusinessCategories()
  }
   implicit def pimpCassandraRowForDF(row: CassandraRow): CassandraRowWrapper 
   = new CassandraRowWrapper(row: CassandraRow)
   
   def queryYelpTipsLoader()={
	   import SContext.sqlContext.implicits._
     SContext.sc.cassandraTable("yelp_data", "tips")
     .map(_.toYelpTip)
     .toDF()
     .registerTempTable("yelp_tips")
     
     val yelp_tips =SContext.sqlContext.sql("SELECT business_id,text FROM yelp_tips ").filter($"text".contains("bad")).limit(10).collect()
     val yelp_like =SContext.sqlContext.sql("SELECT business_id,likes FROM yelp_tips ").filter($"likes" > 2).limit(10).collect()
     
     val report =
         s"""
            |Yelp Tips report containing bad keyword:
            |
            |Top 10 yelp tips in real-time store:
            |${yelp_tips.mkString("\n")}
            |
         """.stripMargin

      println(report)
      
          val likesReport =
         s"""
            |Yelp Tips report containing likes > 0:
            |
            |Top 10 yelp tips in real-time store:
            |${yelp_like.mkString("\n")}
            |
         """.stripMargin

      println(likesReport)

   }
   	
   def queryYelpBusinessDataLoader()={
	   import SContext.sqlContext.implicits._
	   
     SContext.sc.cassandraTable("yelp_data", "yelp_business")
     .map(_.toYelpBusiness)
     .toDF()
     .registerTempTable("yelp_business")
     
     val yelp_business =SContext.sqlContext.sql("SELECT business_id,name,review_count,stars FROM yelp_business ").filter($"stars" > 4 ).limit(10).collect()
     
     val report =
         s"""
            |Yelp Business report containing business having stars > 3
            |
            |Top 10 yelp business :
            |${yelp_business.mkString("\n")}
            |
         """.stripMargin

      println(report)
   }
   
    def joinQueryBusinessCategories()={
	   import SContext.sqlContext.implicits._
	   
     SContext.sc.cassandraTable("yelp_data", "yelp_business")
     .map(_.toYelpBusiness)
     .toDF()
     .registerTempTable("yelp_business")
     
     SContext.sc.cassandraTable("yelp_data", "yelp_business_categories")
     .map(_.toYelpBusinessCategories)
     .toDF()
     .registerTempTable("yelp_business_cat")
     
     
     
     val yelp_business =SContext.sqlContext.sql("SELECT yelp_business.business_id,yelp_business.name,yelp_business_cat.cat FROM yelp_business JOIN  yelp_business_cat ON yelp_business.business_id =  yelp_business_cat.business_id where  yelp_business.stars >4 ").limit(10).collect()
     
     val report =
         s"""
            |Yelp Business report containing business having stars > 3
            |
            |Top 10 yelp business :
            |${yelp_business.mkString("\n")}
            |
         """.stripMargin

      println(report)
   }
}