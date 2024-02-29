import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

name := "volcano"

version := "0.1.0"

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"
ThisBuild / scalaVersion                                   := "3.2.2"
ThisBuild / scalafmtOnCompile                              := true
ThisBuild / semanticdbEnabled                              := true
ThisBuild / semanticdbVersion                              := scalafixSemanticdb.revision
ThisBuild / scalafixOnCompile                              := true

lazy val root =
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .in(file("."))
    .settings(
      Seq(
        libraryDependencies ++= List(
          "eu.joaocosta" %%% "minart" % "0.5.2"
        )
      )
    )
    .settings(name := "volcanoRoot")
    .jsSettings(
      Seq(
        scalaJSUseMainModuleInitializer := true
      )
    )
    .nativeSettings(
      Seq(
        nativeLinkStubs := true,
        nativeMode      := "release",
        nativeLTO       := "thin",
        nativeGC        := "commix",
        nativeConfig ~= {
          _.withEmbedResources(true)
        }
      )
    )
