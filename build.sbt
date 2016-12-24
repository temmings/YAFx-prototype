name := "yafx"
version := "0.1.0"
scalaVersion := "2.12.1"

libraryDependencies += "com.sun.jna" % "jna" % "3.0.9"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.102-R11"
//libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.3"
//libraryDependencies += "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0"

resolvers += Resolver.sonatypeRepo("releases")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

// https://github.com/pathikrit/better-files
libraryDependencies += "com.github.pathikrit" % "better-files_2.11" % "2.16.0"

// https://mvnrepository.com/artifact/org.apache.commons/commons-vfs2
libraryDependencies += "org.apache.commons" % "commons-vfs2" % "2.1"
libraryDependencies += "commons-io" % "commons-io" % "2.5"

enablePlugins(JavaAppPackaging)
