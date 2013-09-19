package com.scalapeno.rhinos

import org.mozilla.javascript._

import scala.collection.JavaConversions._
import play.api.libs.json._
import scala.Some

object NativeJavaObj {
  def unapply(x: Any): Option[Any] = x match {
    case w: Wrapper => Some(w.unwrap())
    case _ => None
  }
}

trait RhinosJsonSupport {

  def toJsValue(input: Any): JsValue = input match {
    case j: JsValue => j
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

  private def toJsObject(nativeObject: NativeObject): JsObject = {
    println(nativeObject.entrySet())
    JsObject(nativeObject.entrySet.toList.map(entry => (entry.getKey.toString, toJsValue(entry.getValue))))
  }

  private def toJsArray(nativeArray: NativeArray): JsArray = {
    new JsArray(nativeArray.iterator().map(item => toJsValue(item)).toList)
  }

}