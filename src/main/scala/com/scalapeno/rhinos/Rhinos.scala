package com.scalapeno

import org.slf4j.LoggerFactory
import org.corespring.javascript._
import spray.json._

package object rhinos {

  val log = LoggerFactory.getLogger(this.getClass)

  // TODO: This should be parameterized
  val sandbox = true

  if (sandbox) {
    ContextFactory.initGlobal(new SandboxContextFactory());
  }

  class RhinosScope(val wrapped: ScriptableObject)

  implicit def scopeToRhinosScope(scope: ScriptableObject): RhinosScope = new RhinosScope(scope)

  implicit def rhinosScopeToScope(rhinosScope: RhinosScope): ScriptableObject = rhinosScope.wrapped


  class RhinosRuntime(
                       val scope: RhinosScope = withContext[RhinosScope](_.initStandardObjects()).get
                       ) extends RhinosEvaluationSupport with RhinosJsonSupport {

    /**
     * Makes an object available to javascript so that it can be called off to
     * @param name
     * @param callbackObj
     */
    def addObject(name: String, callbackObj: Any) {
      withContext {
        context =>
          val jsobj = Context.javaToJS(callbackObj, scope)
          scope.put(name, scope.wrapped, jsobj)
      }
    }

    class JsWrapFactory[T:JsonReader] extends WrapFactory with RhinosJsonSupport {
      override def wrap(cx: Context, scope: Scriptable, obj: Any, staticType: Class[_]) = {
        println("trying to wrap up:" + obj)
        Context.javaToJS(toScala[T](obj),scope)
      }
    }

  }


  private[rhinos] def withContext[T](block: Context => T): Option[T] = {
    val context = Context.enter()

    try {
      Option(block(context))
    } catch {
      case e: Exception => throw e
    } finally {
      Context.exit()
    }
  }

  implicit object JsObjectReader extends JsonReader[JsObject] {
    def read(value: JsValue) = value match {
      case o: JsObject => o
      case x => deserializationError("Expected JsObject, but got " + x)
    }
  }

  implicit object JsArrayReader extends JsonReader[JsArray] {
    def read(value: JsValue) = value match {
      case o: JsArray => o
      case x => deserializationError("Expected JsArray, but got " + x)
    }
  }

}
