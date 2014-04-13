name := """super-actors"""

version := "0.1"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.1",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.1",
  "org.scalatest" %% "scalatest" % "2.1.3" % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")