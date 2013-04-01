package com.hui800.listing

import java.util.concurrent.atomic.AtomicInteger

object ID {
  
  val MAX_SEQUENCE = 0xFFFF
  val sequence = new AtomicInteger(0xFFF0)
  
  def generate(machineId: Short) = new ID(
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

class ID private (
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
