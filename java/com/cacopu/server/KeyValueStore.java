package com.cacopu.server;

import java.io.IOException;

import com.cacopu.server.KeyValues.Key;
import com.cacopu.server.KeyValues.KeyValue;
import com.google.common.base.Optional;

interface KeyValueStore<K, V> {
  public void set(KeyValue<K, V> keyValue) throws IOException;
  public Optional<V> get(Key<K> key) throws IOException;
}

