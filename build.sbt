ThisBuild / scalaVersion := "3.5.2"

ThisBuild / scalacOptions ++= Seq(
  "-new-syntax",
  "-Wunused:all",
  "-deprecation",
  "-feature"
)

lazy val slussen = (project in file("."))
  .settings(
    name := "hornstull",
    libraryDependencies ++= Seq(
      "io.lettuce"    % "lettuce-core" % "6.5.0.RELEASE",
      "com.softwaremill.ox" %% "core" % "1.0.4"
    )
  )
