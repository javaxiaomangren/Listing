package com.hui800.listing.svc

trait RegionService {

  def resolve(cityId: Option[Int], districtId: Option[Int], bizoneId: Option[Int]): (Int, Option[Int], Option[Int])
}
