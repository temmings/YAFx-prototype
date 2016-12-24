libraryDependencies ++= Seq(
  "com.sun.jna" % "jna" % "3.0.9",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalafx" %% "scalafx" % "8.0.102-R11",
  // https://github.com/pathikrit/better-files
  //"com.github.pathikrit" % "better-files_2.11" % "2.16.0",
  // https://mvnrepository.com/artifact/org.apache.commons/commons-vfs2
  "org.apache.commons" % "commons-vfs2" % "2.1",
  "org.apache.commons" % "commons-compress" % "1.9",
  //"commons-io" % "commons-io" % "2.5"
  // https://mvnrepository.com/artifact/com.googlecode.juniversalchardet/juniversalchardet
  "com.googlecode.juniversalchardet" % "juniversalchardet" % "1.0.3",
  "com.jsuereth" %% "scala-arm" % "2.0"
)

enablePlugins(JavaAppPackaging)
