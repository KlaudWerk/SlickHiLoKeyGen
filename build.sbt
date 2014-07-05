name := "SlickHiLoKeyGen"

version := "1.0"

scalaVersion:="2.10.3"

libraryDependencies++=Seq(
  "joda-time"%"joda-time"%"2.1",
  "org.joda"%"joda-convert"%"1.3.1",
  "log4j"%"log4j"%"1.2.17",
  "org.slf4j" % "slf4j-api" % "1.7.0",
  "org.slf4j" % "slf4j-simple" % "1.7.0",
  "org.scalatest" % "scalatest_2.10" % "1.9.1",
  "com.h2database" % "h2" % "1.3.172",
  "mysql" % "mysql-connector-java" % "5.1.25",
  "com.typesafe.slick" % "slick_2.10" % "2.0.0"
)

    