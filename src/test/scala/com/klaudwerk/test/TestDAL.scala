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

/**
 * Data Access Layer definition for Unit Tests
 */
import com.klaudwerk.persistence.{HiDbGenerator, Profile}
import scala.slick.driver.JdbcProfile
import scala.slick.jdbc.JdbcBackend.{Session, Database}

/**
 * Test Data Access Layer definition
 */
class TestDAL(override val profile:JdbcProfile)
  extends  HiDbGenerator
  with Profile {

  def ddl=keyValues.getDdl
  /**
   * Helper method to create tables
   * @param session implicit session parameter
   */
  def createTables(implicit session:Session):Unit= {
    val ddl=keyValues.getDdl
    ddl.createStatements.foreach(println)

  }

  /**
   * Helper method that drops all tables
   *
   * @param session implicit session parameter
   * @return none
   */
  def dropTables(implicit session:Session):Unit={
    val ddl=keyValues.getDdl
    ddl.dropStatements.foreach(println)

  }
}