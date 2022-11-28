ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val akkaVersion = "2.6.18"

val akkaHttpVersion = "10.2.9"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.9.3",
  "com.lightbend.akka" %% "akka-stream-alpakka-sse" % "2.0.2",
  "com.typesafe.akka" %% "akka-stream" % "2.5.31",
  "com.typesafe.akka" %% "akka-http" % "10.4.0",
  "com.typesafe.akka" %% "akka-actor" % "2.5.31",
  "com.lihaoyi" %% "upickle" % "0.9.5",
//  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "org.json4s" %% "json4s-jackson" % "4.1.0-M1",
  "org.json4s" %% "json4s-native" % "4.1.0-M1",
  "net.liftweb" %% "lift-json" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime

)


lazy val root = (project in file("."))
  .settings(
    name := "Broker"
  )
