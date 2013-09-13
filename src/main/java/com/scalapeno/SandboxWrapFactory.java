package com.scalapeno;

import org.corespring.javascript.Context;
import org.corespring.javascript.Scriptable;
import org.corespring.javascript.WrapFactory;

public class SandboxWrapFactory extends WrapFactory {

  public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class staticType) {
    return new SandboxNativeJavaObject(scope, javaObject, staticType);
  }

}
