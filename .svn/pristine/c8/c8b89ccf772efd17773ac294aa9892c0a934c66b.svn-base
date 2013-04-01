package com.hui800.listing.svc

import com.hui800.http.server._
import com.hui800.listing._
import com.hui800.listing.svc.model._
import com.hui800.listing.db._
import com.hui800.listing.controller.CommonParameters

trait ListService {

  def list(params: CommonParameters): FinalResult
  
  def shopIdList(params: CommonParameters): Result
  
  def aggregate(query: QueryParams, sortKey: String, shopOnly: Boolean = false): List[Item]
}
