ThisBuild / organization := "kz.edu.iot"
ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "2.13.14"

lazy val akkaVersion = "2.6.20"
lazy val scalaTestVersion = "3.2.19"

lazy val root = (project in file("."))
  .settings(
    name := "akka-iot-service",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test
    ),
    Compile / run / fork := true,
    Test / fork := true,
    Test / parallelExecution := false,
    Test / testOptions += Tests.Argument(
      TestFrameworks.ScalaTest,
      "-oD",
      "-u",
      ((Test / target).value / "test-reports").getAbsolutePath
    ),
    assembly / assemblyJarName := "akka-iot-service.jar",
    assembly / mainClass := Some("kz.edu.iot.IotServiceApp"),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) =>
        xs.map(_.toLowerCase) match {
          case "manifest.mf" :: Nil | "index.list" :: Nil | "dependencies" :: Nil => MergeStrategy.discard
          case _ => MergeStrategy.first
        }
      case "reference.conf" => MergeStrategy.concat
      case _ => MergeStrategy.first
    }
  )
