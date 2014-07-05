/**
The MIT License (MIT)

Copyright (c) 2013 Igor Polouektov

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
  */
package com.klaudwerk.persistence

import com.klaudwerk.persistence.{HiLoKeyProvider, Profile}
import scala.slick.driver.JdbcProfile
import scala.slick.jdbc.JdbcBackend.{Session, Database}
import scala.collection.mutable._
import scala.collection.mutable

/**
 * Key Generator that generates hi-lo keys in specific key space
 * @param db
 * @param partitionNumber
 * @param maxLowValue
 * @param profile
 */
class DatabaseKeyGenerator( db:Database,
                            partitionNumber:Int,
                            maxLowValue:Int,
                            override val profile:JdbcProfile)

  extends Profile with KeyProvider {

  private val _generators:Map[KeyType,HiLoKeyProvider]=
    new mutable.HashMap[KeyType,HiLoKeyProvider]
      with SynchronizedMap[KeyType,HiLoKeyProvider] {
      override def default(key:KeyType)={
        val provider=new HiLoKeyProvider(maxLowValue,partitionNumber,key.keyType,db,profile)
        this(key)=provider
        provider
      }
    }

  /**
   * Generate next unique key for a key space
   * @param keyType key space name
   * @return next key value
   */
  def getKey(keyType:KeyType):Long=_generators(keyType).nextKey
}