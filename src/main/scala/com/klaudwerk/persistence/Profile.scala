package com.klaudwerk.persistence
import scala.slick.driver.JdbcProfile

/**
 * Database profile trait
 */
trait Profile {
  val profile:JdbcProfile
}