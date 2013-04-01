package com.hui800.listing.svc.impl

import com.hui800.listing.db._
import com.hui800.listing.db.model._
import com.hui800.listing.svc.CreditCardService
import com.hui800.listing.util._
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.Buffer

class CreditCardServiceImpl(
  queries: CreditCardQueries
) extends CreditCardService {

  def listCardDeals(
    cityCode: String,
    bankId:   Int,
    typeCode: String,
    now:      Long,
    excludes: Set[Int],
    pager:    Pager
  ) = {
    val typeCodePattern = if (typeCode.endsWith("0")) {
      typeCode.substring(0, typeCode.length - 1)
    } else {
      typeCode + "%"
    }
    val cityWideDeals = queries.getCardDealList(bankId, cityCode, typeCodePattern)
    val nationalWideDeals = queries.getCardDealList(bankId, "0", typeCodePattern)
    val allDeals = merge(
      cityWideDeals,
      nationalWideDeals,
      (a: CardDeal, b: CardDeal) => a.createTime > b.createTime
    )
    val (expired, nonExpired) = allDeals.partition(_.deadline.map(_ < now).getOrElse(false))
    val all = nonExpired ++ expired
    val count = all.size
    val deals = all
    .filterNot(d => excludes.contains(d.id))
    .slice(pager.skip, pager.skip + pager.size)
    (count, deals)
  }
  
  def countCardDeals(cityCode: String, bankId: Int) = {
    val cityWideCounts = queries.countDealsByTypeCode(bankId, cityCode)
    val nationalWideCounts = queries.countDealsByTypeCode(bankId, "0")
    val counts = collection.mutable.Map[String, AtomicInteger]()
    (cityWideCounts.view ++ nationalWideCounts.view) foreach {
      case (cityCode, count) =>
        counts.getOrElseUpdate(cityCode, new AtomicInteger(0)).addAndGet(count)
        if (cityCode.length > 2) {
          counts.getOrElseUpdate(cityCode.substring(0, 2), new AtomicInteger(0)).addAndGet(count)
        }
    }
    counts.toMap.map(kv => (kv._1, kv._2.intValue))
  }
  
  def listCardBrandOrShop(
    cityCode: String,
    bankId:   Int,
    category: Option[String],
    subcate:  Option[String],
    now:      Long,
    pager:    Pager
  ) = {
    val brandOrShops = queries.getCardBrandOrShopList(bankId, cityCode)
    val filtered = category match {
      case Some(category) if category.endsWith("0") =>
        val trimed = category.substring(0, category.length - 1)
        brandOrShops.filter(_.categorySet.contains(trimed))
      case Some(category) =>
        brandOrShops.filter(_.categorySet.exists(_.startsWith(category)))
      case None => brandOrShops
    }
    val (expired, nonExpired) = filtered.partition(_.deadline.map(_ < now).getOrElse(false))
    val all = nonExpired ++ expired
    (all.size, all.slice(pager.skip, pager.skip + pager.size))
  }
    
  def countBrandOrShopsByCategory(
    cityCode: String,
    bankId:   Int,
    level:    Int
  ): Map[String, Int] = {
    val boses = queries.getCardBrandOrShopList(bankId, cityCode)
    val catBosSet = boses.flatMap(
      bos => bos.categorySet.map(_.substring(0, level)).map(c => (c, bos.typeCode, bos.id))
    ).toSet
    catBosSet.groupBy(_._1) map {
      case (cat, set) => (cat, set.size)
    }
  }
  
  private def merge[T](la: List[T], lb: List[T], less: (T, T) => Boolean): List[T] = {
    if (la.isEmpty) {
      lb
    } else {
      val ia = la.iterator
      val ib = lb.iterator
      val ea = ia.next
      merge(Buffer[T](), ea, ia, ib, less).toList
    }
  }
  
  private def merge[T](ab: Buffer[T], ea: T, ia: Iterator[T], ib: Iterator[T], less: (T, T) => Boolean): Buffer[T] = {
    if (!ib.hasNext) {
      ab.append(ea)
      ab.appendAll(ia)
      ab
    } else {
      val eb = ib.next
      val (nextElem, nextEa, nextIa, nextIb) = if (less(ea, eb)) {
        (ea, eb, ib, ia)
      } else {
        (eb, ea, ia, ib)
      }
      ab.append(nextElem)
      merge(ab, nextEa, nextIa, nextIb, less)
    }
  }
}
