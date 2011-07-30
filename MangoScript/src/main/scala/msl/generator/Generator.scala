package msl.generator

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 5:52 AM
 * To change this template use File | Settings | File Templates.
 */

trait Generator {
  val nl = System.getProperty("line.separator")
  def namespace: String
  def filename: String
  def filePath: String
  def overwrite = true

  def filePathName: String = List(filePath, filename).mkString("/")

  val generationNotice =
  """
/*
    This file has been automatically generated.  Please do not modify its content directly,
    as your changes will be lost the next time it is created.
*/
  """
}