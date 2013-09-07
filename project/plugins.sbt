addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.4.0")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers += "Scala Tools Nexus" at "http://nexus.tapad.com:8080/nexus/content/groups/aggregate/"

