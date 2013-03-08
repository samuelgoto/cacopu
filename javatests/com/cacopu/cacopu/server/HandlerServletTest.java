package com.cacopu.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cacopu.testing.MockingTestCase;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;

import static org.easymock.EasyMock.expect;

public class HandlerServletTest extends MockingTestCase {
  PostHandler postHandler = mock(PostHandler.class);
  GetHandler getHandler = mock(GetHandler.class);
  GetHandler allHandler = mock(GetHandler.class);
  
  HttpServletRequest request = mock(HttpServletRequest.class);
  HttpServletResponse response = mock(HttpServletResponse.class);
  
  public void testGetDispatching() throws Exception {
    expect(request.getServletPath()).andReturn("/get").anyTimes();
    
    getHandler.doGet(request, response);
    
    replay();
    
    HandlerServlet servlet = handler();
    
    servlet.doGet(request, response);
  }
  
  public void testPostDispatching() throws Exception {
    expect(request.getServletPath()).andReturn("/post").anyTimes();
    
    postHandler.doPost(request, response);
    
    replay();
    
    HandlerServlet servlet = handler();
    
    servlet.doPost(request, response);
  }
  
  public void test404s() throws Exception {
    expect(request.getServletPath()).andReturn("404").anyTimes();
    
    response.sendError(HttpServletResponse.SC_NOT_FOUND);
    
    replay();
    
    HandlerServlet servlet = handler();
    
    servlet.doGet(request, response);
  }
  
  public void testAllHandler() throws Exception {
    expect(request.getServletPath()).andReturn("/matchsanything").anyTimes();

    allHandler.doGet(request, response);
    
    replay();
    
    HandlerServlet servlet = handler();
    
    servlet.doGet(request, response);
  }
  
  public void testRegex() {
    Pattern p = Pattern.compile("(^/post$)|(^/get$)|(^.*$)");
    
    replay();
    
    Matcher m1 = p.matcher("/post");
    assertTrue(m1.matches());
    assertNotNull(m1.group(1));
    assertNull(m1.group(2));
    assertNull(m1.group(3));

    Matcher m2 = p.matcher("/get");
    assertTrue(m2.matches());
    assertNull(m2.group(1));
    assertNotNull(m2.group(2));
    assertNull(m2.group(3));

    Matcher m3 = p.matcher("/");
    assertTrue(m3.matches());
    assertNull(m3.group(1));
    assertNull(m3.group(2));
    assertNotNull(m3.group(3));

    Matcher m4 = p.matcher("/post2");
    assertTrue(m4.find());
    assertNull(m4.group(1));
    assertNull(m4.group(2));
    assertNotNull(m4.group(3));
  }
  
  public void testRegexDispatcher() {
    HandlerServlet.RegexDispatcher.Handler<String> handler1 = mock(
        HandlerServlet.RegexDispatcher.Handler.class);
    HandlerServlet.RegexDispatcher.Handler<String> handler2 = mock(
        HandlerServlet.RegexDispatcher.Handler.class);
    HandlerServlet.RegexDispatcher.Handler<String> handler3 = mock(
        HandlerServlet.RegexDispatcher.Handler.class);

    expect(handler1.handle("/foobar")).andReturn("foo");

    expect(handler2.handle("/get")).andReturn("bar");
    
    expect(handler3.handle("/post")).andReturn("hello");
    
    replay();
    
    HandlerServlet.RegexDispatcher<String> dispatcher =
        new HandlerServlet.RegexDispatcher<String>(ImmutableMap.of(
            "/get", handler2,
            "/post", handler3,
            "/.*", handler1
            ));

    assertEquals("foo", dispatcher.match("/foobar"));
    assertEquals("bar", dispatcher.match("/get"));
    assertEquals("hello", dispatcher.match("/post"));
    assertNull(dispatcher.match("404"));
  }
  
  private HandlerServlet handler() {
    return Guice.createInjector(new ServerModule.HandlerModule() {
      @Override
      void serve() {
        post("/post").toInstance(postHandler);
        get("/get").toInstance(getHandler);
        get("/.*").toInstance(allHandler);
      }
    }).getInstance(HandlerServlet.class);
  }
}
