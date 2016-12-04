name := "yafx"

version := "0.1.0"

scalaVersion := "2.11.4"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test"

libraryDependencies += "com.sun.jna" % "jna" % "3.0.9"

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "8.0.92-R10",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.2.2",
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.5.0"
)

resolvers += Resolver.sonatypeRepo("releases")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

// FXML reference
// http://hatanas.hatenablog.com/entry/intellij-scalafx-environment
// http://gluonhq.com/labs/scene-builder/#download
