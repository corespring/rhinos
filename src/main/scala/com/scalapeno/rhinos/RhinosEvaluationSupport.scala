package com.scalapeno.rhinos

import scala.util.control.Exception._
import org.mozilla.javascript._
import play.api.libs.json._

trait RhinosEvaluationSupport { self: RhinosJsonSupport =>
  val scope: RhinosScope

  private def injectableByRhino(obj: Any) = obj match {
    case _: JsObject => false
    case _: JsArray => false
    case _ => true
  }

  def eval(javascriptCode: String, variables: Map[String, Any] = Map.empty): Option[JsValue] = {


    withContext[Any] { context =>
      variables.filter(v => injectableByRhino(v._2)).foreach { variable =>
        val jsVar = variable._2 match {
          case jsString: JsString => Context.javaToJS(jsString.value, scope)
          case jsNumber: JsNumber => Context.javaToJS(jsNumber.value, scope)
          case _ => Context.javaToJS(variable._2, scope)
        }
        scope.put(variable._1, scope.wrapped, jsVar)
      }

      val objectVars =
        variables
          .filterNot(v => injectableByRhino(v._2))
          .toList.map(v => s"""var ${v._1} = ${Json.toJson(v._2.asInstanceOf[JsValue])};""").mkString("\n")

      context.evaluateString(scope, s"$objectVars\n$javascriptCode", "RhinoContext.eval(String)", 1, null) match {
        case obj: Object if obj == Context.getUndefinedValue() => None
        case obj: Object => Some(obj)
        case _ => None
      }
    }.flatMap(value =>
      value match {
        case Some(value) => Some(toJsValue(value))
        case _ => None
      })
  }

  private[rhinos] def using[X <: {def close()}, A](resource : X)(f : X => A) = {
     try {
       f(resource)
     } finally {
       ignoring(classOf[Exception]) {
         resource.close()
       }
     }
  }
}