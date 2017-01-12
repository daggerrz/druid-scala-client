
name := "scala-druid-client"

organization := "com.tapad"

version := "0.1.2"

scalaVersion := "2.11.8"

licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))
bintrayOrganization := Some("tabmo")

libraryDependencies ++= Seq(
                             "com.ning" % "async-http-client" % "1.9.31",
                             "org.json4s" %% "json4s-jackson" % "3.2.11",
                             "joda-time" % "joda-time" % "2.3",
                             "org.joda" % "joda-convert" % "1.4",
                             "org.scalatest" %% "scalatest" % "3.0.1" % "test"
                           )




