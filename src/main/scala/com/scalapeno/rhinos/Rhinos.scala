package com.scalapeno

import org.slf4j.LoggerFactory
import org.mozilla.javascript._

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

}
