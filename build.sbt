import sbt.Keys._
import sbt._

val appVersion = sys.env.getOrElse("GO_PIPELINE_LABEL", "2.0.0-SNAPSHOT")

lazy val abel = (project in file("."))
  .aggregate(core, server)


lazy val core = (project in file("core"))
  .settings(
    name := "abel-core",
    crossScalaVersions := Seq("2.10.4", "2.11.8")
  )
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "com.twitter" %% "algebird-core" % "0.12.1",
    "org.isarnproject" %% "isarn-sketches-algebird-api" % "0.0.2",
    "com.twitter" %% "chill-bijection" % "0.8.0",
    "com.indix" % "kafkav010" % "0.10.0.5" classifier("assembly") excludeAll ExclusionRule("org.apache.kafka", "kafka-clients"),
    "io.spray" %% "spray-json" % "1.3.2",
    "org.scalatest" %% "scalatest" % "3.0.0" % Test,
    "org.mockito" % "mockito-all" % "1.10.19" % Test
  ),
    resolvers ++= Seq("isarn project" at "https://dl.bintray.com/isarn/maven/")
  )
  .settings(publishSettings: _*)

lazy val server = (project in file("server"))
  .settings(name := "abel-server")
  .dependsOn(core)
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http-core" % "2.4.7",
    "com.typesafe.akka" %% "akka-http-experimental" % "2.4.7",
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.7",
    "com.google.inject" % "guice" % "4.1.0",
    "org.rocksdb" % "rocksdbjni" % "4.1.0",
    "org.scalatest" %% "scalatest" % "3.0.0" % Test,
    "org.mockito" % "mockito-all" % "1.10.19" % Test
  ))


lazy val commonSettings = Seq(
  organization := "com.indix",
  version := appVersion,
  scalaVersion := "2.11.8"
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  crossPaths := true,
  publishArtifact in Test := false,
  publishArtifact in(Compile, packageDoc) := true,
  publishArtifact in(Compile, packageSrc) := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := {
    <url>http://github.com/ind9/abel</url>
      <licenses>
        <license>
          <name>Apache License, Version 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:indix/abel.git</url>
        <connection>scm:git:git@github.com:indix/abel.git</connection>
      </scm>
      <developers>
        <developer>
          <id>vinothkr</id>
          <name>Vinothkumar</name>
        </developer>
      </developers>

  }
)
