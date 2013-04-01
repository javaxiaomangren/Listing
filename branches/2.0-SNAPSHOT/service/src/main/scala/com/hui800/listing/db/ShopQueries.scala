package com.hui800.listing.db

trait ShopQueries {

  def countShops(param: QueryParams): Long
  
  def countShopsGroupByCategory(params: QueryParams): List[(String, Long)]

  def countShopsGroupBySubCate(params: QueryParams): List[(String, Long)]

  def countShopsGroupByDistrict(params: QueryParams): List[(Int, Long)]

  def countShopsGroupByBizone(params: QueryParams): List[(Int, Long)]

  def countShopsGroupByDealType(params: QueryParams): List[(String, Long)]

  def countShopsGroupBySubType(params: QueryParams): List[(String, Long)]

  def countShopsGroupByMall(params: QueryParams): List[(Int, Long)]

  def countShopsGroupByBrand(params: QueryParams): List[(Int, Long)]

  def countShopsGroupByBank(params: QueryParams): List[(Int, Long)]
  
  def getShopIds(params: QueryParams, sortKey: String, includeMalls: Boolean = true): List[(Int, Option[Int], Option[Int], Int, Float ,Option[String],Float,Float,Float)]
  
  def getAllShopIds(params: QueryParams, sortKey: String): List[Int]
  
  def getBestShopForDeal(params: QueryParams, dealId: Int): Option[Int]
  
  def getShopIdsForEval(params: QueryParams, sortKey: String): List[Int]
  
  def countShopsInBrand(params: QueryParams, brandIds: Int*): List[(Int, Long)]
}
