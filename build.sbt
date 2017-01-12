
name := "scala-druid-client"

organization := "com.tapad"

scalaVersion := "2.11.8"

crossScalaVersions := Seq(scalaVersion.value, "2.12.1")

licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))
bintrayOrganization := Some("tabmo")

libraryDependencies ++= Seq(
 "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.5",
                             "com.ning" % "async-http-client" % "1.9.31",
                             "org.json4s" %% "json4s-jackson" % "3.5.0",
                             "joda-time" % "joda-time" % "2.3",
                             "org.joda" % "joda-convert" % "1.4",
                             "org.scalatest" %% "scalatest" % "3.0.1" % "test"
                           )




