package com.cacopu.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.cacopu.server.Bots.Bot;
import com.cacopu.server.ChatServlet.PersistedKeyValueStore;
import com.cacopu.server.WolframAlpha.Api;
import com.cacopu.server.WolframAlpha.WebApi;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;

public class ServerModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Api.class).to(WebApi.class);
    bind(new TypeLiteral<KeyValueStore<String, String>>() {}).to(
        PersistedKeyValueStore.class);
    bind(DatastoreService.class).toInstance(
        DatastoreServiceFactory.getDatastoreService());

    install(new HandlerModule() {
      @Override
      protected void serve() {
        get("/").to(IndexServlet.class);
        get("/\\d+(?:/.*)?").to(IndexServlet.class);
        post("/home").to(CreateServlet.class);
        get("/home").to(CreateServlet.class);
        get("/checkout").to(CheckoutServlet.class);
        post("/checkout").to(CheckoutServlet.class);
        post("/task").to(TaskServlet.class);
        post("/_ah/xmpp/message/chat/").to(ChatServlet.class);
        post("/_ah/xmpp/subscription/subscribe/").to(SubscribeServlet.class);
      }
    });

    install(new ServletModule() {
      @Override protected void configureServlets() {
        // serves all urls but /_ah
        serveRegex("^/(?!_ah).*").with(HandlerServlet.class);
        // serves xmpp urls
        serveRegex("/_ah/xmpp/.*").with(HandlerServlet.class);
      }
    });
  }

  static abstract class HandlerModule extends AbstractModule {
    protected void configure() {
      MapBinder.newMapBinder(binder(), String.class, GetHandler.class);
      MapBinder.newMapBinder(binder(), String.class, PostHandler.class);
      
      // configure handlers.
      serve();
    }
    
    abstract void serve();
    
    protected LinkedBindingBuilder<GetHandler> get(String path) {
      return MapBinder.newMapBinder(binder(), String.class, GetHandler.class)
          .addBinding(path);
    }
    protected LinkedBindingBuilder<PostHandler> post(String path) {
      return MapBinder.newMapBinder(binder(), String.class, PostHandler.class)
          .addBinding(path);
    }
  }
  
  @Provides
  @RequestScoped
  Message message(HttpServletRequest request) throws IOException {
    XMPPService xmpp = XMPPServiceFactory.getXMPPService();
    return xmpp.parseMessage(request);
  }
  
  interface Jid {
    String id();
  }

  @Provides
  @RequestScoped
  Jid jid(final Message message) {
      return new Jid() {
      @Override
      public String id() {
        return message.getFromJid().getId().split("/")[0];
      }
    };
  }
  
  @Provides
  Bot bot(ReminderBot reminderBot, WolphramAlphaBot wolphramBot) {
    String defaultMessage = "Sorry, I don't understand that. " +
    "You can say something like \"Remind me my SSN is 1234.\" or \"What's my SSN?\".";

    return Bots.compose(
        reminderBot,
        wolphramBot,
        Bots.alwaysResponds(defaultMessage));
  }
}
