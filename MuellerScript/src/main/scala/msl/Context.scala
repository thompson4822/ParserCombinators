package msl

import dsl.Types._
import collection.mutable.HashMap
import java.io.FileNotFoundException

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 6:20 AM
 * To change this template use File | Settings | File Templates.
 */

object Context {
  lazy val properties: Map[String, String] = PropertiesFile.read("msl.properties") match {
    case Some(props) => props
    case None => throw new FileNotFoundException("msl.properties file not found")
  }

  def findProperty(propertyName: String): String = properties.get(propertyName) match {
      case Some(p) =>
        p
      case _ =>
        throw new Exception("The property '" + propertyName + "' was not defined")
    }

  lazy val netSrc = findProperty("netSrc")
  private lazy val flexConsumerSrc = findProperty("flexConsumerSrc")
  private lazy val flexCommonSrc = findProperty("flexCommonSrc")
  private lazy val flexUtilitySrc = findProperty("flexUtilitySrc")
  lazy val flexBasePackage = findProperty("flexBasePackage")

  private lazy val netServiceConsumer = findProperty("netServiceConsumer")
  private lazy val netServiceAdmin = findProperty("netServiceAdmin")
  private lazy val netServiceCommon = findProperty("netServiceCommon")
  lazy val netFactory = findProperty("netFactory")
  lazy val netFactoryTest = findProperty("netFactoryTest")
  lazy val netDto = findProperty("netDto")
  lazy val netDao = findProperty("netDao")

  // Will be netServiceConsumer, netServiceCommon, or netServiceAdmin
  var netService: String = _

  def setNetService(flexPackage: FlexPackage) =
    netService = flexPackage.namespace match {
      case NamespaceType.Common => netServiceCommon
      case NamespaceType.Consumer => netServiceConsumer
      case NamespaceType.Utility => netServiceAdmin
    }

  lazy val flexBasePath = flexBasePackage.split('.').mkString("/")

  // Will be either flexConsumerSrc, flexCommonSrc, or flexUtilitySrc
  var flexProject: String = _

  // Will indicate the sub directory or project under the flex base package for the flex project
  def flexPackage(p: FlexPackage): String = List(flexBasePackage, p.name).mkString(".")

  def flexPath(p: FlexPackage): String = List(flexContext(p.namespace), flexBasePath, p.name).mkString("/")
  //def flexPath: String = List(flexProject, flexBasePath, flexPackage).mkString("/")

  def netPath: String = netSrc

  val elements: HashMap[String, Statement] = new HashMap

  val projectMapping: HashMap[String, List[String]] = new HashMap

  def flexContext(namespace: NamespaceType.Value) = namespace match {
    case NamespaceType.Consumer => flexConsumerSrc
    case NamespaceType.Common => flexCommonSrc
    case NamespaceType.Utility => flexUtilitySrc
  }

  /*
  def setFlexContext(packageDef: FlexPackage) = {
    flexProject = packageDef.namespace match {
      case NamespaceType.Consumer => flexConsumerSrc
      case NamespaceType.Common => flexCommonSrc
      case NamespaceType.Utility => flexUtilitySrc
    }
    flexPackage = packageDef.name
  }
  */
}