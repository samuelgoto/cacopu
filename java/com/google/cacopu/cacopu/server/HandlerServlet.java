package com.cacopu.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
class HandlerServlet extends HttpServlet {
  private final RegexDispatcher<PostHandler> postDispatcher;
  private final RegexDispatcher<GetHandler> getDispatcher;
  
  @Inject
  HandlerServlet(
      Map<String, Provider<PostHandler>> postHandlers,
      Map<String, Provider<GetHandler>> getHandlers) {

    ImmutableMap.Builder<String, RegexDispatcher.Handler<PostHandler>> post =
      ImmutableMap.builder();
    for (final Map.Entry<String, Provider<PostHandler>> entry : postHandlers.entrySet()) {
      post.put(entry.getKey(), new RegexDispatcher.Handler<PostHandler>() {
        @Override
        public PostHandler handle(String match) {
          return entry.getValue().get();
        }
      });
    }
    
    ImmutableMap.Builder<String, RegexDispatcher.Handler<GetHandler>> get =
      ImmutableMap.builder();
    for (final Map.Entry<String, Provider<GetHandler>> entry : getHandlers.entrySet()) {
      get.put(entry.getKey(), new RegexDispatcher.Handler<GetHandler>() {
        @Override
        public GetHandler handle(String match) {
          return entry.getValue().get();
        }
      });
    }
    
    this.postDispatcher = new RegexDispatcher<PostHandler>(post.build());
    this.getDispatcher = new RegexDispatcher<GetHandler>(get.build());
  }
  
  static class RegexDispatcher<K> {
    private final Pattern p;
    private final List<Map.Entry<String, Handler<K>>> entries;
    
    interface Handler<K> {
      K handle(String match);
    }
    
    RegexDispatcher(Map<String, Handler<K>> patterns) {
      this.entries = Lists.newArrayList(patterns.entrySet().iterator());

      StringBuilder regex = new StringBuilder();
      for (int i = 0; i < entries.size(); i++) {
        Map.Entry<String, Handler<K>> entry = entries.get(i); 
        regex.append("(^" + entry.getKey() + "$)");
        if (i != (entries.size() - 1)) {
          regex.append("|");
        }
      }
      
      this.p = Pattern.compile(regex.toString());
    }
    
    K match(String entry) {
      Matcher m = p.matcher(entry);
      while (m.find()) {
        for (int i = 0; i < entries.size(); i++) {
          if (m.group(i + 1) != null) {
            return entries.get(i).getValue().handle(entry);
          }
        }
      }
      return null;
    }
  }
  
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PostHandler handler = postDispatcher.match(req.getServletPath());
    if (handler == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    handler.doPost(req, resp);
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    GetHandler handler = getDispatcher.match(req.getServletPath());
    if (handler == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    handler.doGet(req, resp);
  }
}
