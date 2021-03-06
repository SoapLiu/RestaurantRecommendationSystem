package recommendationSystem

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{lit, to_date}
import org.apache.spark.sql.{DataFrame, SparkSession}

object DataProcess {

  def getBusinessDataFrame(): DataFrame = {
    val spark = SparkSession
      .builder
      .appName("businessDataFrame")
      .master("local[2]")
      .getOrCreate()

    import org.apache.spark.sql.expressions.Window
    import org.apache.spark.sql.functions.row_number
    val dfTemp = spark.read.json("../finalproject/yelp-dataset/yelp_academic_dataset_business.json")
    val w = Window.orderBy("business_id")
    val df = dfTemp.withColumn("business_id_INT", row_number().over(w))
    df.select("business_id", "name", "state", "city", "address", "business_id_INT", "categories")
      .filter(df("categories").contains("Food") || df("categories").contains("food")
      || df("categories").contains("Restaurant") || df("categories").contains("restaurant"))
  }

  def getReviewDataFrame(): DataFrame = {
    val spark = SparkSession
      .builder
      .appName("reviewDataFrame")
      .master("local[2]")
      .getOrCreate()

    val df = spark.read.json("../finalproject/yelp-dataset/yelp_academic_dataset_review.json")
    df.select("business_id", "user_id", "stars", "date")
      .filter(to_date(df("date"),"yyyy-MM-dd").gt(lit("2015-01-01")))
  }

  def getUserDataFrame(): DataFrame = {
    val spark = SparkSession
      .builder
      .appName("userDataFrame")
      .master("local[2]")
      .getOrCreate()

    import org.apache.spark.sql.functions._
    val dfTemp = spark.read.json("../finalproject/yelp-dataset/yelp_academic_dataset_user.json")
    val w = Window.orderBy("user_id")
    val df = dfTemp.withColumn("user_id_INT", row_number().over(w))
    df
  }

}

