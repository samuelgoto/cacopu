package com.cacopu.server;

import com.cacopu.testing.MockingTestCase;
import com.google.appengine.api.datastore.DatastoreService;

public class PersistedKeyValueStoreTest extends MockingTestCase {
  DatastoreService dataService = mock(DatastoreService.class);
  
  public void testGet() throws Exception {
    replay();
    // KeyValueStore<String, String> store = new ChatServlet.PersistedKeyValueStore(
    //    "", dataService);
  }
}
