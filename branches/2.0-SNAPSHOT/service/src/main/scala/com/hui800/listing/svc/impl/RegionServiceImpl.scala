package com.hui800.listing.svc.impl

import com.hui800.listing.db._
import com.hui800.listing.svc._

class RegionServiceImpl(regionQueries: RegionQueries) extends RegionService {

  def resolve(cityId: Option[Int], districtId: Option[Int], bizoneId: Option[Int]) = {
    (cityId, districtId, bizoneId) match {
      case (_, _, Some(bizone)) =>
        regionQueries.getRegion(2, bizone) match {
          case Some(region) =>
            (region.cityId, Some(region.districtId), bizoneId)
          case None =>
            throw new IllegalArgumentException("指定的商圈不存在: bizone=" + bizone)
        }
      case (_, Some(district), _) =>
        regionQueries.getRegion(1, district) match {
          case Some(region) =>
            (region.cityId, districtId, None)
          case None =>
            throw new IllegalArgumentException("指定的区县不存在: district=" + district)
        }
      case (Some(city), _, _) =>
        (city, None, None)
      case _ =>
        throw new IllegalArgumentException("请输入城市编码(city)")
    }
  }
}
