
name := "scala-druid-client"

organization := "com.tapad"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.6"

crossScalaVersions := Seq("2.10.6", "2.11.4")

libraryDependencies ++= Seq( 
  "com.ning" % "async-http-client" % "1.9.31",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.4",
  "org.scalatest" %% "scalatest" % "2.0.RC2" % "test"
)
