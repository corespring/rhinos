package com.scalapeno.rhinos

import org.specs2.mutable._

import spray.json._
import spray.json.DefaultJsonProtocol._


class RhinosRuntimeSpec extends SpecificationWithJUnit    {
  
  "RhinosRuntime.eval[T](...)" should {
    var rhinos: RhinosRuntime = null
    
    step {
      rhinos = new RhinosRuntime()
    }

    "return None when the script is empty" in {
      val result = rhinos.eval("""""")

      result must beNone
    }

    "throw an exception when the script is not valid" in {
      rhinos.eval("""var x = bla bla bla""") must throwA[Exception]
    }

    "return None when the script does not return a result" in {
      val result = rhinos.eval("""var x = 42;""")

      result must beNone
    }

    "return None when the script returns a Javascript null" in {
      val result = rhinos.eval("""var b = null; b;""")

      result must beNone
    }

    "return Some(true) when the script returns a Javascript true" in {
      val result = rhinos.eval("""var b = true; b;""")
      result match {
        case Some(jsBoolean: play.api.libs.json.JsBoolean) if jsBoolean.value == true => success
        case Some(value) => failure("was not true")
        case _ => failure("No result!")
      }
    }

    "return Some(false) when the script returns a Javascript false" in {
      val result = rhinos.eval("""var b = false; b;""")
      result match {
        case Some(jsBoolean: play.api.libs.json.JsBoolean) if jsBoolean.value == false => success
        case Some(value) => failure("was not false")
        case _ => failure("No result!")
      }
    }

    "return Some[JsNumber] when the script returns a Javascript number" in {
      val result = rhinos.eval("""var n = 3; n;""")
      result match {
        case Some(jsNumber: play.api.libs.json.JsNumber) if jsNumber.value == 3 => success
        case Some(value) => failure("was not 3")
        case _ => failure("No result!")
      }
    }

    "return Some[JsNumber] when the script returns a Javascript number" in {
      val result = rhinos.eval("""var n = 3141521424; n;""")
      result match {
        case Some(jsNumber: play.api.libs.json.JsNumber) if jsNumber.value == 3141521424L => success
        case Some(value) => failure("was not 3141521424")
        case _ => failure("No result!")
      }
    }

    "return Some[JsNumber] and the script returns a Javascript number" in {
      val result = rhinos.eval("""var n = 3.1415; n;""")
      result match {
        case Some(jsNumber: play.api.libs.json.JsNumber) if jsNumber.value == BigDecimal(3.1415) => success
        case Some(value) => failure("was not 3.1415")
        case _ => failure("No result!")
      }
    }

    "return Some[JsNumber] when the script returns a Javascript number" in {
      val result = rhinos.eval("""var n = 1313133.141586193338; n;""")
      result match {
        case Some(jsNumber: play.api.libs.json.JsNumber) if jsNumber.value == BigDecimal(1313133.141586193338) => success
        case Some(value) => failure("was not 1313133.141586193338")
        case _ => failure("No result!")
      }
    }


    "return Some[JsString] when the script returns a character" in {
      val result = rhinos.eval("""var c = 'c'; c;""")
      result match {
        case Some(jsString: play.api.libs.json.JsString) if jsString.value == "c" => success
        case Some(value) => failure("""was not "c"""")
        case _ => failure("No result!")
      }
    }

    "return Some[String] when T = String and the script returns a Javascript string" in {
      val result = rhinos.eval("""var s = "some string!"; s;""")
      result match {
        case Some(jsString: play.api.libs.json.JsString) if jsString.value == "some string!" => success
        case Some(value) => failure("""was not "some string!"""")
        case _ => failure("No result!")
      }
    }

    "return Some[List[Int]] when T = List[Int] and the script returns a Javascript array of numbers" in {
      val result = rhinos.eval("""var a = [1,2,3]; a;""")
      result match {
        case Some(jsArray: play.api.libs.json.JsArray) if jsArray.value == "some string!" => success
        case Some(jsArray: play.api.libs.json.JsArray) => { println(jsArray.value); failure }
        case _ => failure("No result!")
      }
    }
//
//    "return None when T = List[String] and the script returns a Javascript array of numbers" in {
//      val result = rhinos.eval("""var a = [1,2,3]; a;""")
//
//      result must beNone
//    }
//
//    "return Some[JsArray] when T = JsArray and the script returns a Javascript array" in {
//      val result = rhinos.eval("""var a = [1,2,3]; a;""")
//
//      result must beSome[JsArray]
//      result.get must beEqualTo(JsArray(JsNumber(1), JsNumber(2), JsNumber(3)))
//    }
//
//    "return Some[Map[String, Int]] when T = Map[String, Int] and the script returns a compatible Javascript object" in {
//      val result = rhinos.eval("""var o = {"name1": 40, "name2": 2}; o;""")
//
//      result must beSome[Map[String, Int]]
//      result.get must beEqualTo(Map("name1" -> 40, "name2" -> 2))
//    }
//
//    "return Some[JsObject] when T = JsObject and the script returns a Javascript object" in {
//      val result = rhinos.eval("""var o = {"name1": 40, "name2": "2"}; o;""")
//
//      result must beSome[JsObject]
//      result.get must beEqualTo(JsObject("name1" -> JsNumber(40), "name2" -> JsString("2")))
//    }
//
//    "return Some(CustomObject) when T = CustomObject and the script returns a compatible Javascript object" in {
//      case class CustomObject(name1: String, name2:Boolean)
//      implicit val customObjectFormat = jsonFormat2(CustomObject)
//
//      val result = rhinos.eval("""var o = {"name1": "value", "name2": true}; o;""")
//
//      result must beSome[CustomObject]
//      result.get must beEqualTo(CustomObject("value", true))
//    }
//
//    "return Some[List[CustomObject]] when T = List[CustomObject] and the script returns a Javascript array of compatible objects" in {
//      case class CustomObject(name1: String, name2:Boolean)
//      implicit val customObjectFormat = jsonFormat2(CustomObject)
//
//      val result = rhinos.eval("""
//        var a = [{"name1": "value1", "name2": true},
//                 {"name1": "value2", "name2": false}];
//        a;
//      """)
//
//      result must beSome[List[CustomObject]]
//      result.get must beEqualTo(List(CustomObject("value1", true), CustomObject("value2", false)))
//    }
//
//    "return Some[JsArray[JsObject]] when T = JsArray and the script returns a Javascript array of objects" in {
//      val result = rhinos.eval("""
//        var a = [{"name1": "value1", "name2": true},
//                 {"name1": "value2", "name2": false}];
//        a;
//      """)
//
//      result must beSome[JsArray]
//      result.get must beEqualTo(
//        JsArray(
//          JsObject("name1" -> JsString("value1"), "name2" -> JsBoolean(true)),
//          JsObject("name1" -> JsString("value2"), "name2" -> JsBoolean(false))
//        ))
//    }
//
//    "return Some(CompoundObject) when T = CompoundObject and the script returns a Javascript object with nested objects" in {
//      case class CompoundObject(a: String, b:Int, c:Double, d:Boolean, e:Boolean, f:Option[String], g:List[Int], h:NestedObject)
//      case class NestedObject(h1:Long, h2:DoublyNestedObject)
//      case class DoublyNestedObject(h2a:List[String])
//
//      implicit val doublyNestedFormat = jsonFormat1(DoublyNestedObject)
//      implicit val nestedFormat = jsonFormat2(NestedObject)
//      implicit val compoundFormat = jsonFormat(CompoundObject, "a", "b", "c", "d", "e", "f", "g", "h")
//
//      val result = rhinos.eval ("""
//        var func = function(a, b) {return a + b;};
//
//        var bla = {
//          "a": "string",
//          "b": func(15, 27),
//          "c": 3.1415,
//          "d": false,
//          "e": true,
//          "f": null,
//          "g": [1, 2, 3],
//          "h": {
//            "h1": 1,
//            "h2": {
//              "h2a": ["4", "5", "6"]
//            }
//          }
//        };
//
//        bla
//      """)
//
//      result must beSome[CompoundObject]
//      result.get must beEqualTo(
//        CompoundObject(
//          a = "string",
//          b = 42,
//          c = 3.1415,
//          d = false,
//          e = true,
//          f = None,
//          g = List(1, 2, 3),
//          h = NestedObject(
//            h1 = 1,
//            h2 = DoublyNestedObject(
//              h2a = List("4", "5", "6"))))
//      )
//    }
//  }
//
//  "RhinosRuntime.evalFile[T](...)" should {
//    import java.io.File
//
//    var rhinos: RhinosRuntime = null
//
//    step {
//      rhinos = new RhinosRuntime()
//    }
//
//    "not throw an exception when the path doesn't exist" in {
//      val result = rhinos.evalFile[Unit]("/tmp/non-existing-path.js")
//
//      result must beNone
//    }
//
//    "not throw an exception when the file doesn't exist" in {
//      val result = rhinos.evalFile[Unit](new File("/tmp/non-existing-path.js"))
//
//      result must beNone
//    }
//
//    "eval the file and return None when the file does not return anything" in {
//      val url = this.getClass.getClassLoader.getResource("scripts/test-functions.js")
//      val file = new File(url.toURI)
//
//      val result = rhinos.evalFile[Double](file)
//
//      result must beNone
//    }
//
//    "eval the file and return the converted return value" in {
//      val url = this.getClass.getClassLoader.getResource("scripts/script-with-return-value.js")
//      val file = new File(url.toURI)
//
//      val result = rhinos.evalFile[Int](file)
//
//      result must beSome[Int]
//      result.get must beEqualTo(42)
//    }
//
//    "eval functions from a file and make them available to later calls to eval()" in {
//      val url = this.getClass.getClassLoader.getResource("scripts/test-functions.js")
//      val file = new File(url.toURI)
//
//      rhinos.evalFile[Unit](file)
//
//      val result = rhinos.eval("""var r = add2(add(10, 30)); r;""")
//
//      result must beSome[Double]
//      result.get must beEqualTo(42.0)
//    }
//
//    "eval 3rd party JS lib from a file and make it available to later calls to eval()" in {
//      val url = this.getClass.getClassLoader.getResource("scripts/underscore.js")
//      val file = new File(url.toURI)
//
//      rhinos.evalFile[Unit](file)
//
//      val result = rhinos.eval("""
//        var mapped = _.map([1, 2, 3], function(num) { return num * 3; });
//
//        mapped;
//      """)
//
//      result must beSome[List[Int]]
//      result.get must beEqualTo(List(3, 6, 9))
//    }
//  }
//
//  "RhinosRuntime.evalFileOnClasspath[T](...)" should {
//    var rhinos: RhinosRuntime = null
//
//    step {
//      rhinos = new RhinosRuntime()
//    }
//
//    "not throw an exception when the path doesn't exist" in {
//      val result = rhinos.evalFileOnClasspath[Unit]("non-existing-path.js")
//
//      result must beNone
//    }
//
//    "eval the file and return None when the file does not return anything" in {
//      val result = rhinos.evalFileOnClasspath[Double]("scripts/test-functions.js")
//
//      result must beNone
//    }
//
//    "eval the file and return the converted return value" in {
//      val result = rhinos.evalFileOnClasspath[Int]("scripts/script-with-return-value.js")
//
//      result must beSome[Int]
//      result.get must beEqualTo(42)
//    }
//
//    "eval functions from a file and make them available to later calls to eval()" in {
//      rhinos.evalFileOnClasspath[Unit]("scripts/test-functions.js")
//
//      val result = rhinos.eval("""var r = add2(add(10, 30)); r;""")
//
//      result must beSome[Double]
//      result.get must beEqualTo(42.0)
//    }
//
//    "eval 3rd party JS lib from a file and make it available to later calls to eval()" in {
//      rhinos.evalFileOnClasspath[Unit]("scripts/underscore.js")
//
//      val result = rhinos.eval("""
//        var mapped = _.map([1, 2, 3], function(num) { return num * 3; });
//
//        mapped;
//      """)
//
//      result must beSome[List[Int]]
//      result.get must beEqualTo(List(3, 6, 9))
//    }
  }
  
}
