import scala.sys.process.Process

name := "rhinos"

version := "0.6.0.corespring-" + Process("git rev-parse --short HEAD").lines.head + "-SNAPSHOT"

organization := "com.scalapeno"

scalaVersion := "2.10.0"

crossScalaVersions := Seq("2.10.0", "2.9.2")

unmanagedBase <<= baseDirectory { base => base / "lib" }

scalacOptions := Seq("-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io",
  "typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
  "typesafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
)

libraryDependencies ++= Seq(
  "io.spray" %% "spray-json" % "1.2.3",
  "org.mozilla" % "rhino" % "1.7R4",
  "com.typesafe.play" %% "play-json" % "2.2-2013-08-18-e10a665-SNAPSHOT",
  "org.slf4j" %  "slf4j-api" % "1.6.4",
  "ch.qos.logback" % "logback-classic" % "1.0.0" % "provided",
  "org.specs2" %% "specs2" % "1.12.3" % "test",
  "junit" % "junit" % "4.8.2" % "test",
  "org.mockito" % "mockito-all" % "1.9.0" % "test"
)

publishTo <<= version { (v: String) =>
  def isSnapshot = v.trim.contains("-")
  val base = "http://repository.corespring.org/artifactory"
  val repoType = if (isSnapshot) "snapshot" else "release"
  val finalPath = base + "/ivy-" + repoType + "s"
  Some( "Artifactory Realm" at finalPath )
}