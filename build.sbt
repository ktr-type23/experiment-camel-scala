name := "experiment"

version := "1.0"

scalaVersion := "2.11.6"

val camelVersion = "2.15.0"

libraryDependencies ++= Seq(
  "org.apache.camel" % "camel-core" % camelVersion,
  "org.apache.camel" % "camel-scala" % camelVersion,
  "org.apache.camel" % "camel-stream" % camelVersion,
  "org.apache.activemq" % "activemq-core" % "5.7.0",
  "com.github.nscala-time" % "nscala-time_2.11" % "1.8.0",
  "ch.qos.logback" % "logback-core" % "1.1.2",
  "ch.qos.logback" % "logback-classic" % "1.1.2"
)

libraryDependencies ++= Seq(
"org.apache.camel" % "camel-testng" % camelVersion % "test"
)
