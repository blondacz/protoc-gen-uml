import sbt.Tags.Publish

enablePlugins(JavaAppPackaging)

scalaVersion := "2.13.14"
Test / parallelExecution := false

name := "protoc-gen-uml"

organization := "dev.g4s"
version := "1.0.0"

resolvers += Resolver.jcenterRepo

import xerial.sbt.Sonatype.sonatype01

ThisBuild / sonatypeCredentialHost := sonatype01

libraryDependencies ++= Seq(
  "com.thesamet.scalapb"  %% "compilerplugin"           % "0.11.17",
  "com.github.pureconfig" %% "pureconfig"               % "0.17.7" excludeAll ExclusionRule(organization = "com.typesafe", name = "config"),
  "com.github.os72"       % "protoc-jar"                % "3.11.4",
  "com.typesafe"          % "config"                    % "1.4.3",
  "org.scalatest"         %% "scalatest-flatspec"       % "3.2.19" % "test",
  "org.scalatest"         %% "scalatest-shouldmatchers" % "3.2.19" % "test"
)

ThisBuild / scalafmtConfig := file(".scalafmt")

Compile / mainClass := Some("dev.g4s.protoc.uml.Main")

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "dev.g4s"
ThisBuild / organizationName := "dev.g4s"
ThisBuild / organizationHomepage := Some(url("https://github.com/blondacz/protoc-gen-uml"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/blondacz/protoc-gen-uml"),
    "scm:git@github.com/blondacz/protoc-gen-uml"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "blondacz",
    name = "Tomas Klubal",
    email = "tomas.klubal@gmail.com",
    url = url("https://github.com/blondacz")
  )
)
isSnapshot := false

ThisBuild / description := "Plugin into `protoc` generating PlantUML diagrams from the messages defined in the proto files"
ThisBuild / licenses := List(
  "MIT License" -> url("https://github.com/blondacz/protoc-gen-uml/blob/master/LICENSE")
)
ThisBuild / homepage := Some(url("https://github.com/blondacz/protoc-gen-uml"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / version := "v1.0.0"

ThisBuild / publishTo := {
  // For accounts created after Feb 2021:
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true

credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials")

publishTo := sonatypePublishToBundle.value

sonatypeRepository := "https://s01.oss.sonatype.org/service/local"



