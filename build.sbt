name := "petclinic_eff"

version := "1.0"

scalaVersion := "2.11.8"

scalaOrganization := "org.typelevel"
scalacOptions += "-Ypartial-unification"

lazy val http4sVersion = "0.14.9a"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-argonaut" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

libraryDependencies += "org.atnos" % "eff-cats_2.11" % "2.0.0-RC17"
libraryDependencies += "org.atnos" % "eff-cats-monix_2.11" % "2.0.0-RC17"
libraryDependencies += "org.tpolecat" % "doobie-h2-cats_2.11" % "0.3.1-M1"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.21"

EclipseKeys.withSource := true

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.0")

// if your project uses multiple Scala versions, use this for cross building
addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.0" cross CrossVersion.binary)

