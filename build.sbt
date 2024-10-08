import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import scala.scalanative.build._

name := "volcano"

version := "0.2.0"

ThisBuild / scalaVersion                                   := "3.3.4"
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
          "eu.joaocosta" %%% "minart" % "0.6.1"
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
        nativeConfig ~= {
          _
          .withLinkStubs(true)
          .withMode(Mode.releaseFull)
          .withLTO(LTO.thin)
          .withGC(GC.commix)
          .withEmbedResources(true)
        }
      )
    )
