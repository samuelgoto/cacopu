package com.cacopu.server;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.eq;

import com.cacopu.server.CheckoutServlet.ProductBuilder;
import com.cacopu.server.CheckoutServlet.Renderer;
import com.cacopu.testing.MockingTestCase;
import com.cacopu.testing.SoyMapDataMatcher;
import com.google.common.collect.ImmutableMap;
import com.google.template.soy.data.SoyMapData;

import junit.framework.TestCase;

public class CheckoutServletTest extends MockingTestCase {
  HttpServletRequest req = mock(HttpServletRequest.class);
  HttpServletResponse resp = mock(HttpServletResponse.class);
  ProductBuilder builder = mock(ProductBuilder.class);
  Renderer renderer = mock(Renderer.class);
  
  public void testStep1() throws Exception {
    expectRequest(ImmutableMap.of("id", "1234"));
    
    builder.build(1234L);
    expectLastCall().andReturn(new SoyMapData());

    resp.getWriter();
    expectLastCall().andReturn(new PrintWriter(System.out));
    
    renderer.render(eq("cacopu.checkoutHtml"), SoyMapDataMatcher.of(
        new SoyMapData("product", new SoyMapData())));
    expectLastCall().andReturn("");
    
    replay();
    CheckoutServlet servlet = new CheckoutServlet(builder, renderer);
    servlet.doGet(req, resp);
  }
  
  public void testStep2() throws Exception {
    expectRequest(ImmutableMap.<String, String>builder()
        .put("id", "1234")
        .put("confirm", "")
        .put("buyer.name", "john")
        .put("buyer.email", "john@doe.com")
        .put("buyer.phone", "911")
        .put("buyer.zip", "999")
        .build());
    
    builder.build(1234L);
    expectLastCall().andReturn(new SoyMapData());

    resp.getWriter();
    expectLastCall().andReturn(new PrintWriter(System.out));
    
    renderer.render(eq("cacopu.reviewHtml"), SoyMapDataMatcher.of(
        new SoyMapData(
            "product", new SoyMapData(),
            "buyer", new SoyMapData(
                "name", "john",
                "phone", "911",
                "email", "john@doe.com",
                "zip", "999"
                )
        )
        ));
    expectLastCall().andReturn("");
    
    replay();
    CheckoutServlet servlet = new CheckoutServlet(builder, renderer);
    servlet.doPost(req, resp);
  }
  
  private void expectRequest(Map<String, String> parameters) {
    for (Entry<String, String> entry : parameters.entrySet()) {
      req.getParameter(entry.getKey());
      expectLastCall().andStubReturn(entry.getValue());
    }
  }
}
