package msl.generator.flex

import msl.generator.StringExtensions._
import msl.generator._
import msl.dsl.Types._
import msl.Context

/**
 * Created by IntelliJ IDEA.
 * User: Steve
 * Date: 1/15/11
 * Time: 5:46 AM
 * To change this template use File | Settings | File Templates.
 */

class CommandGen(serviceName: String, method: Method, flexPackage: FlexPackage) extends Generator {
  val namespace = List(Context.flexPackage(flexPackage), "commands").mkString(".")

  lazy val eventPackagePartial = List(Context.flexPackage(flexPackage), "events", method.name).mkString(".")

  lazy val filepath = List(Context.flexPath(flexPackage), "commands").mkString("/")

  lazy val filename = method.name + "Command.as"

  private def requestArguments = method.parameters.map("request." + _.name).mkString(", ")

  private def responseMethod = if(method.returnType.forCSharp != "void")
    """
        public function result(response:""" + method.returnType.forFlex + """):void
        {
            LOG.debug("Response for """ + method.name + """ was " + response);
            dispatcher(new """ + method.name + """Response(response));
        }
    """
    else
    """
        public function result():void
        {
            dispatcher(new """ + method.name + """Response());
        }
    """

  override def toString =
    """
package """ + namespace + """
{
	import com.mueller.mihan.navigation.events.ShowDialogEvent;
	import com.mueller.mihan.navigation.events.ShowMessageEvent;
	import com.mueller.mihan.service.BaseCommand;
	import """ + eventPackagePartial + """Request;
	import """ + eventPackagePartial + """Response;
	import com.mueller.mihan.utilities.LogUtils;

	import flash.net.registerClassAlias;
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.logging.ILogger;
	import mx.logging.Log;
	import mx.rpc.AsyncToken;
	import mx.rpc.Fault;
	import mx.rpc.remoting.RemoteObject;

	public class """ + method.name + """Command extends BaseCommand
	{
        private static const LOG:ILogger = LogUtils.getLogger(""" + method.name + """Command);

        [Inject(id=""" + "\"" + serviceName.unCapitalize + "\"" + """)]
        public var service:RemoteObject;

        public function execute(request:""" + method.name + """Request):AsyncToken
        {
            LOG.debug(""" + "\"" + method.name + """Command now initiating the service call """ + method.name + """ on the service """ + serviceName.unCapitalize + "\"" + """);
            return service.""" + method.name + """Command(""" + requestArguments + """);
        }
""" + responseMethod + """
	}
}
  """
}