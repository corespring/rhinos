package com.scalapeno;

import org.corespring.javascript.ClassShutter;

public class SandboxClassShutter implements ClassShutter {

  public boolean visibleToScripts(String fullClassName) {
    return false;
  }

}
