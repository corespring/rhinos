package com.scalapeno.rhinos

import scala.util.control.Exception._

import java.io._

import org.slf4j.LoggerFactory
import org.mozilla.javascript._
import scala.collection.immutable.ListMap
import scala.collection.JavaConversions._

trait RhinosEvaluationSupport { self: RhinosJsonSupport =>
  val scope: RhinosScope

  import play.api.libs.json._

  def eval(javascriptCode: String): Option[JsValue] = {
    withContext[Any] { context =>
      context.evaluateString(scope, javascriptCode, "RhinoContext.eval(String)", 1, null) match {
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

  private def toJsValue(input: Any): JsValue = input match {
    case b: Boolean => JsBoolean(b)
    case i: Int => JsNumber(i)
    case l: Long => JsNumber(l)
    case f: Float => JsNumber(f)
    case d: Double => JsNumber(d)
    case s: String => JsString(s)

    case o: NativeObject => toJsObject(o)
    case a: NativeArray => toJsArray(a)
    case w: Wrapper => toJsValue(w.unwrap())

    case u: Undefined => JsNull
    case null => JsNull
    case other@_ => {
      log.warn("Cannot convert '%s' to a JsValue. Returning None.".format(other))

      JsNull
    }
  }

//  private def toJsObject(nativeObject: NativeObject): JsObject = {
//    nativeObject.entrySet
//  }


  private def toJsObject(nativeObject: NativeObject): JsObject = {
    println(nativeObject.entrySet())
    JsObject(nativeObject.entrySet.toList.map(entry => (entry.getKey.toString, toJsValue(entry.getValue))))
  }

  private def toJsArray(nativeArray: NativeArray): JsArray = {
    new JsArray(nativeArray.iterator().map(item => toJsValue(item)).toList)
  }

  def evalReader[T : spray.json.JsonReader](reader: Reader): Option[T] = {
    using(reader) { r =>
      withContext[Any] { context =>
        context.evaluateReader(scope, r, "RhinoContext.eval(Reader)", 1, null)
      }
    }.flatMap(value => toScala[T](value))
  }
  
  def evalFile[T : spray.json.JsonReader](path: String): Option[T] = evalFile(new File(path))(implicitly[spray.json.JsonReader[T]])
  def evalFile[T : spray.json.JsonReader](file: File): Option[T] = {
    if (file != null && file.exists) {
      evalReader(new FileReader(file))(implicitly[spray.json.JsonReader[T]])
    } else {
      log.warn("Could not evaluate Javascript file %s because it does not exist.".format(file))

      None
    }
  }

  def evalFileOnClasspath[T : spray.json.JsonReader](path: String): Option[T] = {
    val in = this.getClass.getClassLoader.getResourceAsStream(path)

    if (in != null) {
      evalReader(new BufferedReader(new InputStreamReader(in)))(implicitly[spray.json.JsonReader[T]])
    } else {
      log.warn("Could not evaluate Javascript file %s because it does not exist on the classpath.".format(path))

      None
    }
  }

  // ==========================================================================
  // Implementation Details
  // ==========================================================================
  
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