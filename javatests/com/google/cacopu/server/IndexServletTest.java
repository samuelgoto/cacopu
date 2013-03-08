package com.cacopu.server;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import static org.easymock.EasyMock.expectLastCall;

import com.cacopu.server.IndexServlet;
import com.cacopu.testing.MockingTestCase;

import junit.framework.TestCase;

public class IndexServletTest extends MockingTestCase {
  HttpServletRequest req = mock(HttpServletRequest.class);

  public void testParsingIds() {
    req.getRequestURI();
    expectLastCall().andReturn("/1234");
    
    req.getRequestURI();
    expectLastCall().andReturn("/");

    req.getRequestURI();
    expectLastCall().andReturn("/abc");

    req.getRequestURI();
    expectLastCall().andReturn("/1234/hello-world");

    replay();
    
    IndexServlet servlet = new IndexServlet();

    assertEquals(Integer.valueOf(1234), servlet.parseId(req));
    assertEquals(null, servlet.parseId(req));
    assertEquals(null, servlet.parseId(req));
    assertEquals(Integer.valueOf(1234), servlet.parseId(req));
  }
}
