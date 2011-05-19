import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) with ProguardProject {

  override def proguardInJars = super.proguardInJars +++ Path.fromFile("""c:\scala-2.9.0.final\lib""")

  override def proguardOptions = List(proguardKeepMain("msl.Main")
)

  val scalaToolsSnapshots = ScalaToolsSnapshots

  val scalaTestVersion = "1.6-SNAPSHOT" 
  val mockitoVersion = "1.8.5"

  val scalatest = "org.scalatest" % "scalatest_2.9.0" % scalaTestVersion % "test"
  val junit = "junit" % "junit" % "4.4" % "test"
  val mockito = "org.mockito" % "mockito-all" % mockitoVersion %"test"

  override def testOptions = super.testOptions ++ Seq(TestArgument(TestFrameworks.ScalaTest, "-oD"))
}