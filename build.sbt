name := """Scala Quiz"""

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1",
  "org.scalatest" % "scalatest_2.12" % "3.0.1" % Test,
  "org.seleniumhq.selenium" % "selenium-java" % "2.45.0" % Test,
  "org.scalikejdbc" %% "scalikejdbc"       % "2.5.1",
  "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "2.5.1",
  "com.h2database"  %  "h2"                % "1.4.193",
  "ch.qos.logback"  %  "logback-classic"   % "1.2.1"
)

parallelExecution in Test := false
