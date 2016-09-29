name := "petclinic_eff"

version := "1.0"

scalaVersion := "2.11.8"

scalaOrganization := "org.typelevel"
scalacOptions += "-Ypartial-unification"


libraryDependencies += "org.atnos" % "eff-scalaz_2.11" % "2.0.0-RC7"
libraryDependencies += "org.atnos" % "eff-scalaz-concurrent_2.11" % "2.0.0-RC7"
libraryDependencies += "org.tpolecat" % "doobie-h2_2.11" % "0.3.1-M1"

EclipseKeys.withSource := true

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.0")

// if your project uses multiple Scala versions, use this for cross building
addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.0" cross CrossVersion.binary)

