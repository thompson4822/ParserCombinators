package msl

import dsl.Types._
import collection.mutable.HashMap
import java.io.FileNotFoundException
import javax.swing.UIDefaults.LazyInputMap

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 6:20 AM
 * To change this template use File | Settings | File Templates.
 */

object Context {
  lazy val properties: Map[String, String] = PropertiesFileManager.read("msl.properties") match {
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

  lazy val netServiceConsumer = findProperty("netServiceConsumer")
  lazy val netServiceAdmin = findProperty("netServiceAdmin")
  lazy val netServiceCommon = findProperty("netServiceCommon")
  lazy val netServiceInstaller = findProperty("netServiceInstaller")
  lazy val netFactory = findProperty("netFactory")
  lazy val netFactoryTest = findProperty("netFactoryTest")
  lazy val netDto = findProperty("netDto")
  lazy val netDao = findProperty("netDao")
  lazy val netUtility = findProperty("netUtility")

  private lazy val flexConsumerSrc = findProperty("flexConsumerSrc")
  private lazy val flexCommonSrc = findProperty("flexCommonSrc")
  private lazy val flexUtilitySrc = findProperty("flexUtilitySrc")
  private lazy val flexInstallerSrc = findProperty("flexInstallerSrc")
  lazy val flexConsumerBasePackage = findProperty("flexConsumerBasePackage")
  lazy val flexCommonBasePackage = findProperty("flexCommonBasePackage")
  lazy val flexUtilityBasePackage = findProperty("flexUtilityBasePackage")
  lazy val flexInstallerBasePackage = findProperty("flexInstallerBasePackage")

  // Will be netServiceConsumer, netServiceCommon, netServiceAdmin, or netServiceInstaller
  var netService: String = _

  lazy val netServices = List(netServiceConsumer, netServiceCommon, netServiceAdmin, netServiceInstaller)

  def flexBasePackage(flexPackage: FlexPackage) = flexPackage.namespace match {
    case NamespaceType.Common => flexCommonBasePackage
    case NamespaceType.Consumer => flexConsumerBasePackage
    case NamespaceType.Utility => flexUtilityBasePackage
    case NamespaceType.Installer => flexInstallerBasePackage
  }

  def setNetService(flexPackage: FlexPackage) =
    netService = getNetService(flexPackage)

  def getNetService(flexPackage: FlexPackage) = flexPackage.namespace match {
      case NamespaceType.Common => netServiceCommon
      case NamespaceType.Consumer => netServiceConsumer
      case NamespaceType.Utility => netServiceAdmin
      case NamespaceType.Installer => netServiceInstaller

    }

  def flexBasePath(p: FlexPackage): String = flexBasePackage(p).split('.').mkString("/")

  // Will be either flexConsumerSrc, flexCommonSrc, or flexUtilitySrc
  var flexProject: String = _

  // Will indicate the sub directory or project under the flex base package for the flex project
  def flexPackage(p: FlexPackage): String = List(flexBasePackage(p), p.flexSubPackage).mkString(".")

  def flexPath(p: FlexPackage): String = List(flexContext(p.namespace), flexBasePath(p), p.flexSubDirectory).mkString("/")

  def netPath: String = netSrc

  val elements: HashMap[String, Statement] = new HashMap

  val projectMapping: HashMap[String, List[String]] = new HashMap

  def flexContext(namespace: NamespaceType.Value) = namespace match {
    case NamespaceType.Consumer => flexConsumerSrc
    case NamespaceType.Common => flexCommonSrc
    case NamespaceType.Utility => flexUtilitySrc
    case NamespaceType.Installer => flexInstallerSrc
  }

}