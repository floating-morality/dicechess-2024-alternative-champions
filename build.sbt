ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .enablePlugins(WartRemover)
  .settings(
    name := "dicechess-2024-alternative-champions",
    scalacOptions := Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-encoding",
      "utf8",
      "-Xfatal-warnings",
      s"-P:wartremover:excluded:${sourceManaged.value.asFile.getPath}",
      "-Wunused:imports",
      "-Wunused:locals",
      "-Wunused:implicits"
    )
  )
  .settings(
    libraryDependencies ++=
      "org.typelevel" %% "cats-effect" % "3.5.7" +:
        Seq(
          "io.circe" %% "circe-core",
          "io.circe" %% "circe-generic",
          "io.circe" %% "circe-parser"
        ).map(_ % "0.14.10")
  )
