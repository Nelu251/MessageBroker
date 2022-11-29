ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.11"

lazy val root = (project in file("."))
  .settings(
    name := "PTR-Broker"
  )

val AkkaVersion = "2.6.18"
val AkkaHttpVersion = "10.2.7"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.9.3",
  "com.lightbend.akka" %% "akka-stream-alpakka-sse" % "2.0.2",
  "com.typesafe.akka" %% "akka-stream" % "2.5.31",
  "com.typesafe.akka" %% "akka-http" % "10.1.11",
  "com.typesafe.akka" %% "akka-actor" % "2.5.31",
  "com.lihaoyi" %% "upickle" % "0.9.5",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
)
