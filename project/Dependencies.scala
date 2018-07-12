import sbt._

object Dependencies {
  def common: Seq[ModuleID] = {
    akka.all ++
    // cats.all ++
    logging.all ++
    Scope.test( test.all:_* )
  }

  def defaultDependencyOverrides: Seq[ModuleID] = Seq.empty[ModuleID]

  object Scope {
    def compile( deps: ModuleID* ): Seq[ModuleID] = deps map ( _ % "compile" )
    def provided( deps: ModuleID* ): Seq[ModuleID] = deps map ( _ % "provided" )
    def test( deps: ModuleID* ): Seq[ModuleID] = deps map ( _ % "test" )
    def runtime( deps: ModuleID* ): Seq[ModuleID] = deps map ( _ % "runtime" )
    def container( deps: ModuleID* ): Seq[ModuleID] = deps map ( _ % "container" )
  }

  trait Module {
    def groupId: String
    def version: String
    def artifactId( id: String ): String
    def isScala: Boolean = true
    def module( id: String ): ModuleID = {
      if ( isScala ) groupId %% artifactId(id) % version
      else groupId % artifactId(id) % version
    }
  }

  trait SimpleModule extends Module {
    def artifactIdRoot: String
    override def artifactId( id: String ): String = {
      if ( id.isEmpty ) artifactIdRoot else s"$artifactIdRoot-$id"
    }
  }


  object akka extends SimpleModule {
    override val groupId: String = "com.typesafe.akka"
    override val artifactIdRoot: String = "akka"
    override val version: String = "2.5.13"

    def all = Seq( actor, slf4j )
    val actor = module( "actor" )
    val slf4j = module( "slf4j" )
    val testkit= module( "testkit" )
  }

  object cats extends SimpleModule {
    override val groupId: String = "org.typelevel"
    override val artifactIdRoot: String = "cats"
    override val version: String = "1.1.0"

    def all = Seq( core, macros, kernel )
    val core = module( "core" )
    val macros = module( "macros" )
    val kernel = module( "kernel" )
  }

  object logging extends SimpleModule {
    override val groupId: String = "com.outr"
    override val artifactIdRoot: String = "scribe"
    override val version: String = "2.5.3-SNAPSHOT"

    def all = Seq(
      scribe,
      scribeSlf4j,
//      logback,
      slf4jApi
//      slf4jSimple,
    )

    val scribe= module( "" )
    val scribeSlf4j = module( "slf4j" )
    val slf4jVersion = "1.7.25"
    val slf4jApi = "org.slf4j" % "slf4j-api" % slf4jVersion
    val slf4jSimple = "org.slf4j" % "slf4j-simple" % slf4jVersion
    val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  }

  object test {
    def all = Seq( scalatest, akka.testkit )
    val scalatest = "org.scalatest" %% "scalatest" % "3.0.5"
  }
}
