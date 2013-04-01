package com.hui800.listing.cache

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class Serializer {
  
  def serialize(obj: Any) = {
    try{
      val byteArrayOutputStream = new ByteArrayOutputStream(128)
      val objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
      objectOutputStream.writeObject(obj)
      objectOutputStream.flush
      byteArrayOutputStream.toByteArray
    }catch{
      case e =>
        throw new Exception("fialed to serialize class "+ obj + ", error message: " + e)
    }
  }
  
  def deserialize(source: Array[Byte]) = {
    if(source == null){
      null
    }else{
      try{
        val byteArrayInputStream = new ByteArrayInputStream(source)
        val objectInputStream = new ObjectInputStream(byteArrayInputStream)
        objectInputStream.readObject()
      }catch{
        case e =>
          throw new Exception("fialed to read object from  serialized class, caused by  " + e)
      }
    }
  }
}
