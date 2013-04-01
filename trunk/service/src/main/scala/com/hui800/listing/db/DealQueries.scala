package com.hui800.listing.db

import com.hui800.listing.db.model._

trait DealQueries {

  def getDealIds(params: QueryParams, sortKey: String = "type"): List[Int]
  
  def getDeals(dealIds: Int*): List[Deal]

  def getSubTypeAndBankIds(dealIds: Int*): List[(Int, String, Int)]

  def getDealAndBankIds(params: QueryParams, sortKey: String): List[(Int, Int)]
  
  def countDealsGroupByCategory(params: QueryParams): List[(String, Long)]
  
  def countDealsGroupBySubCate(params: QueryParams): List[(String, Long)]
  
  def countDealsGroupByDistrict(params: QueryParams): List[(Int, Long)]
  
  def countDealsGroupByBizone(params: QueryParams): List[(Int, Long)]
  
  def countDealsGroupByDealType(params: QueryParams): List[(String, Long)]
  
  def countDealsGroupBySubType(params: QueryParams): List[(String, Long)]
  
  def countDealsGroupByMall(params: QueryParams): List[(Int, Long)]  
   
  def countDealsGroupByBrand(params: QueryParams): List[(Int, Long)]
  
  def countDealsGroupByShop(params: QueryParams): List[(Int, Long)]
  
  def countDealsGroupByBank(params: QueryParams): List[(Int, Long)]
  
  def countDealsInMall(params: QueryParams, mallIds: Int*): List[(Int, Long)]
   
  def countDealsInDealBrand(params: QueryParams, dealBrandIds: Int*): List[(Int, Long)]
  
  def countDealsInShopBrand(params: QueryParams, shopBrandIds: Int*): List[(Int, Long)]
  
  def countDealsInShop(params: QueryParams, shopIds: Int*): List[(Int, Long)]
  
  def countDeals(params: QueryParams): Long
}
