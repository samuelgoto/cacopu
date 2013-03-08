package com.cacopu.server;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cacopu.server.Bots.Bot;
import com.cacopu.server.KeyValues.Key;
import com.cacopu.server.KeyValues.KeyValue;
import com.cacopu.server.MailServlet.Chat;
import com.cacopu.server.ServerModule.Jid;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.xmpp.Message;

class ChatServlet implements PostHandler {
  private static final Logger logger = Logger.getLogger(
      ChatServlet.class.getName());

  @Inject Bot bot;
  @Inject Message message;
  @Inject Jid jid;

  // TODO(goto): how do you guarantee that an attacker won't create
  // a POST message here and insert attack JIDs ?
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    logger.info("Incoming chat from " + jid.id());

    Optional<String> response = bot.parse(message.getBody());
    
    if (response.isPresent()) {
      new Chat().send(jid.id(), response.get());
      return;
    }

    new Chat().send(jid.id(), "Crap. I dunno what happened :(");
  }

  @RequestScoped
  static class PersistedKeyValueStore implements KeyValueStore<String, String> {
    private final Jid jid;
    private final DatastoreService datastore;

    @Inject
    PersistedKeyValueStore(
        Jid jid,
        DatastoreService datastore) {
      this.jid = jid;
      this.datastore = datastore;
    }
    
    @Override
    public Optional<String> get(Key<String> key) throws IOException {
      Query query = new Query("Fact")
          .addFilter("user", Query.FilterOperator.EQUAL, jid.id())
          .addFilter("key", Query.FilterOperator.EQUAL, key.key());
      
      PreparedQuery pq = datastore.prepare(query);
      
      for (Entity result : pq.asIterable()) {
        String value = (String) result.getProperty("value");
        return Optional.of(value);
      }
      
      return Optional.absent();
    }

    @Override
    public void set(KeyValue<String, String> keyValue) throws IOException {
      Entity fact = new Entity("Fact");
      Date creationDate = new Date();
      fact.setProperty("creation", creationDate);
      fact.setProperty("user", jid.id());
      fact.setProperty("key", keyValue.key());
      fact.setProperty("value", keyValue.value());
      
      datastore.put(fact);
    }
  }
}
