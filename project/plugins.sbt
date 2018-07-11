// see: https://github.com/sbt/sbt-git#known-issues
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25"

logLevel := Level.Info

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.8.0" )
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6" )

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.4.0" )
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4" )
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.1" )
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1" )
