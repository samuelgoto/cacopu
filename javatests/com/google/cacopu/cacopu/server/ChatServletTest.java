package com.cacopu.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.common.base.Optional;

import junit.framework.TestCase;

public class ChatServletTest extends TestCase {
  // private final LocalServiceTestHelper helper =
  //  new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  
  // LocalDatastoreServiceTestConfig foo = null;
  
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  
  public void testDataStore() {
    //ChatServlet.PersistedKeyValueStore store = new ChatServlet.PersistedKeyValueStore(
    //    "", datastore);
    //store.set(KeyValues.of("foo", "bar"));
    //Optional<String> result = store.get(KeyValues.of("foo"));
    //assertTrue(result.isPresent());
    //assertEquals("bar", result.get());
  }
  
  public void testParsingJid() {
    String jid = "samuelgoto@gmail.com/gmail.34EC6B24";
    assertEquals("samuelgoto@gmail.com", jid.split("/")[0]);
  }
}
