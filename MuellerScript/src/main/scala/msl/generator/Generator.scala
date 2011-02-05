package msl.generator

import msl.dsl.Types.Statement

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 5:52 AM
 * To change this template use File | Settings | File Templates.
 */

trait Generator {
  def namespace: String
  def filename: String
  def filepath: String
  def overwrite = true

  def filePathName: String = List(filepath, filename).mkString("/")

  val generationNotice =
  """
/*
    This file has been automatically generated.  Please do not modify its content directly,
    as your changes will be lost the next time it is created.
*/
  """
}