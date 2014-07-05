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

package com.klaudwerk.test

import com.klaudwerk.persistence.{Drivers, HiLoKeyProvider}
import org.scalatest.{BeforeAndAfter, FunSuite}
import com.klaudwerk.persistence.{KeyType, DatabaseKeyGenerator}
import scala.actors.Futures._
//import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.JdbcBackend.{Session, Database}

/**
 *
 *
 */
class KeyGeneratorTestSuite extends FunSuite with BeforeAndAfter {

  val da=new TestDAL(Drivers.driver("mysql"))

  //Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver="org.h2.Driver")
  val db:Database=
    Database.forURL("jdbc:mysql://localhost:3306/test", driver = "com.mysql.jdbc.Driver", user="testuser")

  val hlp:HiLoKeyProvider=new HiLoKeyProvider(5,1,"PROJECT",db,da.profile)

  before {
    // setup the database
    db withSession
      {
        session:Session=>implicit val implSession=session
          session.withTransaction {
            hlp.keyValues.create
          }
      }
  }
  after {
    // tear the down database
    db withSession
      {
        session:Session=>implicit val implSession=session
          session.withTransaction {
            hlp.keyValues.drop
          }
      }
  }

  test("Generate the series of keys"){
    //key test sequence
    val keys=(0 to 9).toList
    val generated=for {
      i<-1 to 10
    } yield(hlp.nextKey)
    assert(keys==generated)
  }

  test("Generate the series of keys in different key spaces"){
    //key test sequence
    val keys=(0 to 9).toList
    val ks1:HiLoKeyProvider=new HiLoKeyProvider(5,1,"CALENDAR",db,da.profile)
    val generated=for {
      i<-1 to 10
    } yield(hlp.nextKey)
    val calendars=for {
      i<-1 to 10
    } yield(ks1.nextKey)
    assert(keys==generated)
    assert(keys==calendars)
  }

  test("Generate the series of keys different partitions"){
    //key test sequence
    val keys=(0 to 9).toList
    val ks1:HiLoKeyProvider=new HiLoKeyProvider(5,2,"PROJECT",db,da.profile)
    val generated=for {
      i<-1 to 10
    } yield(hlp.nextKey)
    val calendars=for {
      i<-1 to 10
    } yield(ks1.nextKey)
    assert(keys==generated)
    assert(keys==calendars)

  }

  test("Generate the series of keys in multiple threads") {
    val keys=(0 to 99).toList
    val tasks=for(i<-1 to 10) yield future[Seq[Long]] {
      for {
        i<-1 to 10
      } yield(hlp.nextKey)
    }
    val result=awaitAll(20000L,tasks:_*);
    val generated=(result.map(x=>x.get.asInstanceOf[Seq[Long]])).flatten.sorted
    assert(keys==generated)
  }

  test("Generate the series of keys in multiple threads in multiple domains") {
    val keys=(0 to 99).toList
    val ks1:HiLoKeyProvider=new HiLoKeyProvider(5,1,"PROJECT",db,da.profile)
    val tasks=for(i<-1 to 10) yield future[Seq[Long]] {
      for {
        i<-1 to 10
      } yield (if(i%2==0) hlp.nextKey else ks1.nextKey)
    }
    val result=awaitAll(20000L,tasks:_*);
    val generated=(result.map(x=>x.get.asInstanceOf[Seq[Long]])).flatten.sorted
    assert(keys==generated)

  }

  test("On-demand key space key generator") {

    val gen=new DatabaseKeyGenerator(db,1,5,da.profile)
    val sp1=for { i<-1 to 10} yield gen.getKey(new KeyType("Project"))
    val sp2=for { i<-1 to 10} yield gen.getKey(new KeyType("Activity"))
    val sp3=for { i<-1 to 10} yield gen.getKey(new KeyType("Calendar"))
    val sp4=for { i<-1 to 10} yield gen.getKey(new KeyType("Project"))
    val keys=(0 to 9).toList
    assert(keys==sp1)
    assert(keys==sp2)
    assert(keys==sp3)
    assert(keys!=sp4)
    assert((10 to 19).toList==sp4)
  }

}
