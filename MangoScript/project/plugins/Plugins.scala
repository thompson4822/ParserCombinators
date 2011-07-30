/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/30/11
 * Time: 6:55 AM
 * To change this template use File | Settings | File Templates.
 */

class Plugins(info: sbt.ProjectInfo) extends sbt.PluginDefinition(info) {
  val codaRepo = "Coda Hale's Repository" at "http://repo.codahale.com/"
  val assemblySBT = "com.codahale" % "assembly-sbt" % "0.1.1"
  val proguard = "org.scala-tools.sbt" % "sbt-proguard-plugin" % "0.0.5"

}