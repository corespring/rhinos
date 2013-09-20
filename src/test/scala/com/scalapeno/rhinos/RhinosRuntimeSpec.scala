package com.scalapeno.rhinos

import org.specs2.mutable._
import play.api.libs.json._

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

    "return Some[String] when the script returns a Javascript string" in {
      val result = rhinos.eval("""var s = "some string!"; s;""")
      result match {
        case Some(jsString: play.api.libs.json.JsString) if jsString.value == "some string!" => success
        case Some(value) => failure("""was not "some string!"""")
        case _ => failure("No result!")
      }
    }

    "with variables" in {

      "puts String in scope" in {
        val stringValue = "great!"
        new RhinosRuntime().eval("str;", Map("str" -> stringValue)) match {
          case Some(jsString: JsString) => {
            jsString.value === stringValue
            success
          }
          case _ => failure("Fail!")
        }
      }

      "puts JsString in scope" in {
        val stringValue = "great!"
        new RhinosRuntime().eval("str;", Map("str" -> JsString(stringValue))) match {
          case Some(jsString: JsString) => {
            jsString.value === stringValue
            success
          }
          case _ => failure("Fail!")
        }
      }

      def parseDouble(numberLike: Any) =
        try {
          numberLike match {
            case jsNumber: JsNumber => jsNumber.value.toDouble
            case _ => numberLike.toString.toDouble
          }
        } catch {
          case _ => throw new IllegalArgumentException(s"Error parsing double from $numberLike")
        }

      def testNumber(number: Any) = {
        new RhinosRuntime().eval("number;", Map("number" -> number)) match {
          case Some(jsNumber: JsNumber) => jsNumber.value.toDouble === parseDouble(number); success
          case _ => failure(s"Failed for value $number")
        }
      }

      "puts numbers in scope" in {
        testNumber(3)
        testNumber(3L)
        testNumber(3.14159)
      }

      "puts JsNumbers in scope" in {
        testNumber(JsNumber(3))
        testNumber(JsNumber(3L))
        testNumber(JsNumber(3.14159))
      }

      "puts JsObjects in scope" in {
        val jsObject = Json.obj(
          "val1" -> "one",
          "val2" -> 2
        )
        new RhinosRuntime().eval("object;", Map("object" -> jsObject)) match {
          case Some(jsObject: JsObject) => {
            (jsObject \ "val1").asInstanceOf[JsString].value === "one"
            (jsObject \ "val2").asInstanceOf[JsNumber].value === 2
            success
          }
          case Some(thingElse) => { println(thingElse.getClass.toString); println(thingElse); success }
          case _ => failure(s"Failed for value $jsObject")
        }
      }

      "puts JsArrays in scope" in {
        val array = Json.arr(1, 2, 3)
        new RhinosRuntime().eval("array;", Map("array" -> array)) match {
          case Some(jsArray: JsArray) => success
          case _ => failure
        }
      }

    }


  }
  
}
