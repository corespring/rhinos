package com.scalapeno;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class SandboxContextFactory extends ContextFactory {

  @Override
  protected Context makeContext() {
    Context context = super.makeContext();
    context.setWrapFactory(new SandboxWrapFactory());
    context.setClassShutter(new SandboxClassShutter());
    return context;
  }

}
