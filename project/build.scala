import proguard._
import sbt._

import Keys._
import AndroidKeys._

object General {
  lazy val settings = Defaults.defaultSettings ++ Seq (
    name := "facebook-android-sdk",
    organization := "facebook",
    version := "3.0",
    versionCode := 1,
    scalaVersion := "2.10.0",
    crossScalaVersions := Seq("2.9.1","2.9.2","2.10.0"),
    platformName in Android := "android-16",
    minSdkVersion := Some(8),
    publishArtifact in packageDoc := false
  )

  val proOpt = Seq(
    "-keep class android.support.v4.app.** { *; }",
    "-keep interface android.support.v4.app.** { *; }",
    "-keep class com.actionbarsherlock.** { *; }",
    "-keep interface com.actionbarsherlock.** { *; }",
    "-keepattributes *Annotation*")

  val proguardSettings = Seq (
    useProguard in Android := true,
    proguardOption in Android := proOpt.mkString(" ")
  )

  
  lazy val fullAndroidSettings =
   General.settings ++
   AndroidProject.androidSettings ++
   TypedResources.settings ++
   proguardSettings ++
   AndroidManifestGenerator.settings ++    
   addArtifact(Artifact("facebook-android-sdk", "apklib", "apklib"), apklibPackage in Android).settings
 } 

object AndroidBuild extends Build {
  lazy val main = Project(
    "facebook-android-sdk",
    file("facebook"),
    settings = General.fullAndroidSettings ++ Seq(
    unmanagedSourceDirectories in Compile <<= (baseDirectory)(base=>Seq(base / "src")),      
    javaSource in Android <<= (baseDirectory)(_ / "src"),
    mainResPath in Android <<= (baseDirectory)map(_  / "res"),
    unmanagedBase <<= (baseDirectory)(_ / ".."/ "libs"),
    manifestPath in Android <<= (baseDirectory)map(base => Seq(base /"AndroidManifest.xml")),
    apklibPackage in Android <<= (apklibPackage in Android, javaSource in Android)map{(apk,jSrc)=>
     val file =(jSrc / "com" / "facebook" / "android" / "BuildConfig.java").asFile
     if(!file.exists())file.createNewFile() 
     val pw = new java.io.PrintWriter(file)
     val body = """package com.facebook.android;
     public final class BuildConfig {
     public final static boolean DEBUG = false;
     }"""
     pw.println(body)
     pw.close
     apk
    }
   )
  )
}


// vim: set ts=4 sw=4 et:
