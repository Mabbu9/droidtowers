/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.happydroids.server.HappyDroidService;
import com.happydroids.server.TestHappyDroidService;
import org.junit.runners.model.InitializationError;

public class TowerGameTestRunner extends GdxTestRunner {
  public TowerGameTestRunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  @Override
  protected void beforeTestRun() {
    super.beforeTestRun();
    HappyDroidService.setInstance(new TestHappyDroidService());
  }
}
