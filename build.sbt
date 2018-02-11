name := "learning_akka_2"

version := "1.0"

scalaVersion := "2.11.9"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.9",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.9" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.11",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.9",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.9" % Test
)

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.0-RC1"

// https://mvnrepository.com/artifact/com.hazelcast/hazelcast-all
libraryDependencies += "com.hazelcast" % "hazelcast-all" % "3.7.2"
libraryDependencies += "com.hazelcast" %% "hazelcast-scala" % "3.7.2" withSources()
