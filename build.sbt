name := "top3000words"

version := "1.0"

scalaVersion := "2.11.7"


resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.1"
libraryDependencies += "com.typesafe.akka" % "akka-testkit_2.11" % "2.4.1" % "test"
libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "0.1.2"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
libraryDependencies += "com.typesafe.scala-logging" % "scala-logging-slf4j_2.11" % "2.1.2"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.1"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.3"
