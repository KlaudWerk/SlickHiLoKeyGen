package com.klaudwerk.persistence


import scala.slick.driver._

/**
 * Set of standard JDBC drivers
 *
 */
object Drivers {

  private def driverByName:String => Option[JdbcProfile] = Map(
    "default" -> H2Driver
    ,"derby" -> DerbyDriver
    ,"h2" -> H2Driver
    ,"hsqldb" -> HsqldbDriver
    ,"mysql" -> MySQLDriver
    ,"postgresql" -> PostgresDriver
    ,"sqlite" -> SQLiteDriver
    ).get(_)

  def driver(name:String)=driverByName(name).getOrElse(throw new IllegalArgumentException(s"Cursus Database Error: cannot find the driver for: [$name]"))
}