package com.hui800.listing.db.model

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations._
import org.squeryl.dsl._

case class BrandFirstLetter(
  @Column("brand_id") brandId: Int,
  @Column("field_name") fieldName: String,
  @Column("first_letter") firstLetter: String
) extends KeyedEntity[CompositeKey2[Int,String]] {
  
  def id = CompositeKey2(brandId, fieldName)
}
