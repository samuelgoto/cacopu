package com.cacopu.server;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class KeyValues {
  interface Key<K> {
    K key();
  }

  interface KeyValue<K, V> extends Key<K> {
    V value();
  }

  static Key<String> of(final String key) {
    return new Key<String>() {
      @Override
      public String key() {
        return key;
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public boolean equals(Object to) {
        if (this == to) {
          return true;
        }
        if (!(to instanceof Key)) {
          return false;
        }
        Key<Object> toKey = (Key<Object>) to;
        return key().equals(toKey.key());
      }

      @Override
      public String toString() {
        return "[" + key() + "]";
      }
    };
  }

  static KeyValue<String, String> of(final String key, final String value) {
    return new KeyValue<String, String>() {
      @Override
      public String key() {
        return key;
      }

      @Override
      public String value() {
        return value;
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public boolean equals(Object to) {
        if (this == to) {
          return true;
        }
        if (!(to instanceof KeyValue)) {
          return false;
        }
        KeyValue<Object, Object> toKeyValue = (KeyValue<Object, Object>) to;
        return key().equals(toKeyValue.key()) &&
          value.equals(toKeyValue.value());
      }
      
      @Override
      public String toString() {
        return "[" + key() + ": " + value() + "]";
      }
    };
  }
  
  static class FakeKeyValueStore implements KeyValueStore<String, String> {
    private final Map<String, String> store = Maps.newHashMap();

    @Override
    public Optional<String> get(Key<String> key) {
      System.out.println("Fetching: " + key.key());
      String result = store.get(key.key());
      return result != null ? Optional.of(result) : Optional.<String>absent();
    }

    @Override
    public void set(KeyValue<String, String> keyValue) {
      System.out.println("Storing: " + keyValue.key());
      store.put(keyValue.key(), keyValue.value());
    }
  }
}
