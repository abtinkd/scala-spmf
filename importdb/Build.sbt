name := "dsl"

version := "1.0"

scalaVersion := "2.10.2"

organization := "com.tahlilafzar"

//resolvers += "spray repo" at "http://repo.spray.io",
libraryDependencies ++= Seq("org.skife.com.typesafe.config" % "typesafe-config" % "0.3.0"
  , "com.typesafe.akka" %% "akka-kernel" % "2.2.0-RC1"
  , "com.typesafe.akka" %% "akka-remote" % "2.2.0-RC1"
  , "org.slf4j" % "jcl-over-slf4j" % "1.6.5"
  , "io.spray" % "spray-http" % "1.2-M8"
  , "io.spray" % "spray-can" % "1.2-M8"
  , "io.spray" % "spray-json_2.10.0-RC5" % "1.2.3"
  , "org.scalatest" %% "scalatest" % "1.9" % "test"
  , "org.scala-lang" % "scala-reflect" % "2.10.2"
  , "org.scala-lang" % "scala-library" % "2.10.2"
  , "mysql" % "mysql-connector-java" % "latest.integration"
  , "com.philippe-fournier-viger" %% "spmf" % "0.94")

version := "1.0.0-SNAPSHOT"

publishTo := Some(Resolver.url("ghasemz-snapshot", new URL("http://192.168.10.30:8081/artifactory/ghasemz-snapshot-local/")))

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")