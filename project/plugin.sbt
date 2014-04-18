addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.3.6")

addSbtPlugin("org.scala-sbt.plugins" % "sbt-onejar" % "0.8")

addSbtPlugin("com.orrsella" % "sbt-stats" % "1.0.6-SNAPSHOT")

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")