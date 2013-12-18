import scala._
import scala.Some
import UniqueVersionKeys._

uniqueVersionSettings

uniqueVersion := true

mainClass in oneJar := Some("com.tahlilafzar.sample.main")

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

name := "sample"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.10.2"

organization := "com.tahlilafzar"

//resolvers += "spray repo" at "http://repo.spray.io",
libraryDependencies ++= Seq(
  "org.slf4j" % "jcl-over-slf4j" % "1.6.5"
  , "io.spray" % "spray-http" % "1.2-M8"
  , "org.scalatest" %% "scalatest" % "1.9" % "test"
  , "org.skife.com.typesafe.config" % "typesafe-config" % "0.3.0"
  , "com.typesafe.akka" %% "akka-kernel" % "2.2.3"
  , "com.typesafe.akka" %% "akka-remote" % "2.2.3"
  , "com.typesafe.akka" %% "akka-actor" % "2.2.3"
  , "com.typesafe.akka" %% "akka-slf4j" % "2.2.3"
  , "com.typesafe.akka" %% "akka-agent" % "2.2.3"
  , "com.typesafe.akka" %% "akka-zeromq" % "2.2.3"
  , "com.typesafe.akka" %% "akka-cluster" % "2.2.3"
  , "com.typesafe.akka" %% "akka-contrib" % "2.2.3"
  , "org.scalaz" %% "scalaz-core" % "7.0.4"
  , "io.spray" %% "spray-json" % "1.2.5"
  , "io.spray" % "spray-can" % "1.2-RC2"
  , "io.spray" % "spray-routing" % "1.2-RC2"
  , "ch.qos.logback" % "logback-classic" % "1.0.7"
  , "commons-lang" % "commons-lang" % "2.6"
  , "com.typesafe.akka" %% "akka-testkit" % "2.2.3" % "test"
  , "org.scalatest" %% "scalatest" % "1.9" % "test"
  , "org.scala-lang" % "scala-reflect" % "2.10.2"
  , "org.scala-lang" % "scala-library" % "2.10.2"
)

version := "1.0.0-SNAPSHOT"

publishTo := Some(Resolver.url("ghasemz-snapshot", new URL("http://192.168.10.30:8081/artifactory/ghasemz-snapshot-local/")))

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")