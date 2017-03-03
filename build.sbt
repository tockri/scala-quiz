name := """Scala Quiz"""

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  "org.scalatest" % "scalatest_2.12" % "3.0.1" % Test,
  "org.seleniumhq.selenium" % "selenium-java" % "2.45.0" % Test
)

parallelExecution in Test := false
