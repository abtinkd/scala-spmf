import scala._
import scala.Some


mainClass in oneJar := Some("com.tahlilafzar.snow.boot.main")

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

name := "scala-sbt"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.10.2"

organization := "com.tahlilafzar"


libraryDependencies ++= Seq(
  "io.spray" % "spray-http" % "1.2-M8"
  , "io.spray" %% "spray-json" % "1.2.5"
  , "io.spray" % "spray-can" % "1.2-RC2"
  , "io.spray" % "spray-routing" % "1.2-RC2"
  , "com.typesafe.akka" %% "akka-kernel" % "2.2.3"
  , "com.typesafe.akka" %% "akka-remote" % "2.2.3"
  , "com.typesafe.akka" %% "akka-actor" % "2.2.3"
  , "com.typesafe.akka" %% "akka-slf4j" % "2.2.3"
  , "com.typesafe.akka" %% "akka-agent" % "2.2.3"
  , "com.typesafe.akka" %% "akka-zeromq" % "2.2.3"
  , "com.typesafe.akka" %% "akka-cluster" % "2.2.3"
  , "com.typesafe.akka" %% "akka-contrib" % "2.2.3"
  , "org.scalaz" %% "scalaz-core" % "7.0.4"
  , "commons-lang" % "commons-lang" % "2.6"
  , "com.typesafe.akka" %% "akka-testkit" % "2.2.3" % "test"
  , "org.scalatest" %% "scalatest" % "1.9" % "test"
  , "org.scala-lang" % "scala-reflect" % "2.10.2"
  , "org.scala-lang" % "scala-library" % "2.10.2"
  , "com.github.axel22" %% "scalameter" % "0.4-M2"
)

testFrameworks += new TestFramework(
  "org.scalameter.ScalaMeterFramework")

logBuffered := false


publishTo := Some(Resolver.url("ghasemz-snapshot-local", new URL("http://tahlilafzar.com:8081/artifactory/ghasemz-snapshot-local/")))

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")