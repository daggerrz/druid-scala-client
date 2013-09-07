
name := "scala-druid-client"

organization := "com.tapad"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.2"

crossScalaVersions := Seq("2.10.2", "2.9.3")

libraryDependencies ++= Seq( 
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "org.json4s" %% "json4s-jackson" % "3.2.2",
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.4"
)
