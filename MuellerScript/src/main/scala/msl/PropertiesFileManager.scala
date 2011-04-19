package msl

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/21/11
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */

object PropertiesFileManager {
  def read(filename: String) : Option[Map[String,String]] = try {
    val file = new java.io.FileInputStream(filename)
    val props = new java.util.Properties
    props.load(file)
    file.close
    val iter = props.entrySet.iterator
    val vals = scala.collection.mutable.Map[String,String]()
    while (iter.hasNext)
    {
      val item = iter.next
      vals += (item.getKey.toString ->item.getValue.toString)
    }
    Some(vals.toMap)
  }
  catch {
    case e:Exception => println("Properties.loadFile: " + e)
    None
  }

}