package com.cacopu.testing;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import junit.framework.TestCase;

public class MockingTestCase extends TestCase {
  private static final IMocksControl control = EasyMock.createControl();
  
  public void setUp() {
    control.reset();
  }
  
  public void tearDown() {
    control.verify();
  }
  
  protected static <K> K mock(Class<K> clazz) {
    return control.createMock(clazz);
  }

  protected void replay() {
    control.replay();
  }
}
