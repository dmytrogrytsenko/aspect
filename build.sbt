name := "aspect"
 
version := "1.0-SNAPSHOT"
 
organization := "com.delirium"
 
scalaVersion := "2.12.2"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  "com.typesafe.akka" %% "akka-actor" % "2.5.3",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.3",
  "com.typesafe.akka" %% "akka-http" % "10.0.9",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.9",
  "com.github.mauricio" %% "postgresql-async" % "0.2.21",
  "org.scalikejdbc" %% "scalikejdbc-async" % "0.8.0"
)
