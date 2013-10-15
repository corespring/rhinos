import sbt._
import Keys._
import Project._
import scala.sys.process.Process

object Build extends sbt.Build {

  val appVersion = "0.6.0.corespring-" + Process("git rev-parse --short HEAD").lines.head + "-SNAPSHOT"

  def buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.scalapeno",
    scalaVersion := "2.10.0",
    version := appVersion,
    resolvers ++= Seq(
      "typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
      "typesafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
    ),
    libraryDependencies ++= Seq(
      "org.mozilla" % "rhino" % "1.7R4",
      "com.typesafe.play" %% "play-json" % "2.2.0",
      "org.slf4j" %  "slf4j-api" % "1.6.4",
      "ch.qos.logback" % "logback-classic" % "1.0.0" % "provided",
      "org.specs2" %% "specs2" % "1.12.3" % "test",
      "junit" % "junit" % "4.8.2" % "test",
      "org.mockito" % "mockito-all" % "1.9.0" % "test"
    ),
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishTo <<= version {
      (v: String) =>
        def isSnapshot = v.trim.contains("-")
        val base = "http://repository.corespring.org/artifactory"
        val repoType = if (isSnapshot) "snapshot" else "release"
        val finalPath = base + "/public-ivy-" + repoType + "s"
        Some( "Artifactory Realm" at finalPath )
    },
    unmanagedBase <<= baseDirectory { base => base / "lib" },
    scalacOptions := Seq("-deprecation", "-encoding", "utf8")
  )

  val main = Project("rhinos", base = file("."), settings = buildSettings )
}