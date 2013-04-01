package com.hui800.listing.svc

import com.hui800.listing._
import com.hui800.listing.svc.model._
import com.hui800.listing.db._

trait ListService {

  def list(params: Parameters): FinalResult
  
  def shopIdList(params: Parameters): Result
  
  def aggregate(query: QueryParams, sortKey: String, shopOnly: Boolean = false): List[Item]
}
