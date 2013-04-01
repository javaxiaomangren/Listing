package com.hui800.listing.svc.impl

import com.hui800.listing.db._
import com.hui800.listing.svc._
import com.hui800.listing.svc.model._
import com.twitter.logging.Logger
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session

class BankServiceImpl(dealCountService: CountService) extends BankService {

  val logger = Logger(getClass.getName)
  
  def dealCounts(params: Parameters) = inTransaction {
    if (params.debug) {
      Session.currentSession.setLogger(logger.debug(_))
    }
    dealCountService.countByBank(QueryParams(cityId = params.cityId.get))
    .collect({
        case MidItem(bankId: Int, dealCount, _) if bankId != 0 =>
          Item(bank_id = bankId, deal_count = dealCount)
      })
    .toArray
  }
}
