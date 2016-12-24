import sbt._

lazy val commonSettings = Seq(
  organization := "to.amamo.h",
  version := "0.1.0",
  scalaVersion := "2.12.1"
)

lazy val filer = (project in file("filer"))
  .settings(commonSettings: _*)
  .settings(
    name := "filer"
  )

lazy val root = (project in file("."))
  .aggregate(filer)
  .settings(commonSettings: _*)
  .settings(
    name := "yafx"
  )

enablePlugins(JavaAppPackaging)
