package com.hui800.listing.data.creditcard

import com.twitter.querulous.evaluator._

class DealTransfer(src: QueryEvaluator) {

  def transfer = {
    src.select(
      """SELECT d.id
              , d.end_date
              , d.create_time
              , d.city
              , c.provider_id
              , CONCAT("type:", d.deal_type_code)
           FROM deal d
              , deal_card dc
              , card c
          WHERE dc.deal_id = d.id
            AND dc.card_id = c.id
            AND d.deal_type_code <> '30000003'"""
    ) {
      rs => Map(
        "id"       -> rs.getInt(1),
        "deadline" -> rs.getDate(2),
        "mtime"    -> rs.getDate(3),
        "city"     -> rs.getString(4),
        "bank"     -> rs.getInt(5),
        "tags"     -> rs.getString("6")
      )
    }
  }
}
