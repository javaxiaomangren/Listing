package com.hui800.listing.db.impl

import org.squeryl._
import com.hui800.listing.db.model._
import java.text.SimpleDateFormat
import javax.sql.DataSource
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.internals.DatabaseAdapter

class DB(val dataSource: DataSource, adapter: DatabaseAdapter) extends Schema {

  val dateFormat = new SimpleDateFormat("yyyyMMdd")
  val deals = table[Deal]("deal")
  val dealCategories = table[DealCategory]("deal_category")
  val shops = table[Shop]("shop")
  val shopCategories = table[ShopCategory]("shop_category")
  val dealShops = table[DealShop]("deal_shop")
  val dealBrands = table[DealBrand]("deal_brand")
  val brands = table[Brand]("brand")
  val brandTags = table[BrandTag]("brand_tag")
  val regions = table[Region]("region")
  val brandFirstLetters = table[BrandFirstLetter]("first_letter_brand")
  val cardDeals = table[CardDeal]("card_deal")
  val cardBrandOrShops = table[CardBrandOrShop]("card_brand_or_shop")
  val mallShopDeals = table[MallShopDeal]("mall_shop_deal")

  on(deals)(d => declare(
      columns(d.dealType, d.subType) are (indexed("key_deal_type_sub_type"))
    ))

  on(shops)(s => declare(
      columns(s.cityId, s.districtId, s.bizoneId) are (indexed("key_city_district_bizone")),
      s.brandId is (indexed("key_brand"))
    ))

  on(dealShops)(ds => declare(
      ds.shopId is (indexed("key_shop")),
      columns(ds.dealId, ds.shopId) are(unique, indexed("uk_deal_shop"))
    ))

  SessionFactory.concreteFactory = Some(() => getSession)
  
//  val statisticsListener = {
//    LocalH2SinkStatisticsListener.initializeOverwrite("db-usage-stats")
//  }

  def getSession = new Session(dataSource.getConnection, adapter)
}
