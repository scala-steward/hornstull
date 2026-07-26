ThisBuild / scalaVersion := "3.5.2"

// Scala 3 only — no crossScalaVersions, no Scala 2 fallback.
ThisBuild / crossScalaVersions := Seq(scalaVersion.value)

ThisBuild / scalacOptions ++= Seq(
  "-new-syntax", // Scala 3 braceless / new syntax only
  "-Wunused:all",
  "-Wvalue-discard", // Scala 3
  "-Wnonunit-statement", // Scala 3
  "-Xkind-projector:underscores", // Scala 3 native kind-projector
  "-deprecation",
  "-feature"
)

lazy val slussen = (project in file("."))
  .settings(
    name := "hornstull",
    libraryDependencies ++= Seq(
      "io.lettuce" % "lettuce-core" % "6.5.0.RELEASE",
      "com.softwaremill.ox" %% "core" % "1.0.6"
    ),
    // Refuse to build on anything that isn't Scala 3.
    Compile / compile := (Compile / compile).dependsOn(Def.task {
      val sv = scalaVersion.value
      if (!sv.startsWith("3.")) sys.error(s"hornstull is Scala 3 only; got $sv")
    }).value
  )
