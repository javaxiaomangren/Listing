package com.hui800.util.core

import com.twitter.util.Local
import java.util.concurrent.atomic.AtomicInteger

object IDManager {
  
  var machineId: Short = 1.toShort
  val MAX_SEQUENCE = 0xFFFF
  val sequence = new AtomicInteger(0xFFF0)
  val id = new Local[ID]
  
  def generate = {
    id() = new ID(
      timestamp = System.currentTimeMillis,
      machineId = machineId,
      sequence = sequence.incrementAndGet match {
        case seq if seq > MAX_SEQUENCE =>
          sequence.synchronized {
            val seq = sequence.get
            if (seq > MAX_SEQUENCE)
              sequence.set(seq - MAX_SEQUENCE - 1)
          }
          (seq - MAX_SEQUENCE).toShort
        case seq =>
          seq.toShort
      }
    )
  }
  
  def get = id()
}

class ID (
  val timestamp: Long,
  val machineId: Short,
  val sequence: Short
) {
  
  private lazy val _id = String.format(
    "%X_%X_%X",
    timestamp.asInstanceOf[AnyRef], 
    machineId.asInstanceOf[AnyRef],
    sequence.asInstanceOf[AnyRef]
  )
  
  override def toString = _id
}
