package com.cacopu.server;

import java.io.IOException;

import com.cacopu.server.Bots.Bot;
import com.cacopu.server.KeyValues.Key;
import com.cacopu.server.KeyValues.KeyValue;
import com.cacopu.server.Parsers.CompositeParser;
import com.cacopu.server.Parsers.Parser;
import com.cacopu.server.Parsers.RegexKeyParser;
import com.cacopu.server.Parsers.RegexKeyValueParser;
import com.cacopu.server.KeyValueStore;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

class ReminderBot implements Bot {
  private static final String TOKEN = "([\\w\\s\\(\\)-]*\\w)";
  private static final String PERIOD = "\\s*(\\.|\\!)?$";
  private static final String QUESTION = "\\s*(\\.|\\?)?$";

  private final Parser<KeyValue<String, String>> keyValueParser;
  private final Parser<Key<String>> keyParser;
  private final KeyValueStore<String, String> keyValueStore;

  ReminderBot(
      Parser<KeyValue<String, String>> keyValueParser,
      Parser<Key<String>> keyParser,
      KeyValueStore<String, String> keyValueStore) {
    this.keyValueParser = keyValueParser;
    this.keyParser = keyParser;
    this.keyValueStore = keyValueStore;
  }

  @Inject
  ReminderBot(KeyValueStore<String, String> keyValueStore) {
    this(keyValueParser(), keyParser(), keyValueStore);
  }

  static Parser<KeyValue<String, String>> keyValueParser() {
    return new CompositeParser<KeyValue<String, String>>(
        ImmutableSet.<Parser<KeyValue<String, String>>>of(
            new RegexKeyValueParser("Remind me that my " + TOKEN + " is " + TOKEN + PERIOD),
            new RegexKeyValueParser("my " + TOKEN + " is " + TOKEN + PERIOD),
            new RegexKeyValueParser("Set " + TOKEN + " to " + TOKEN + PERIOD),
            new RegexKeyValueParser("" + TOKEN + " :?= " + TOKEN + PERIOD)
        ));
  }

  static Parser<Key<String>> keyParser() {
    return new CompositeParser<Key<String>>(
        ImmutableSet.<Parser<Key<String>>>of(
            new RegexKeyParser("What is my " + TOKEN + QUESTION),
            new RegexKeyParser("What's my " + TOKEN + QUESTION),
            new RegexKeyParser("(\\w+)\\s*(\\?)+$")
        ));
  }

  @Override
  public Optional<String> parse(String input) {
    Optional<KeyValue<String, String>> keyValue = keyValueParser.parse(input);
    try {
      if (keyValue.isPresent()) {
        // set key value
        keyValueStore.set(keyValue.get());

        keyValueStore.set(KeyValues.of(
            keyValue.get().key().toLowerCase(), keyValue.get().value()));

        keyValueStore.set(KeyValues.of(
            keyValue.get().key().toUpperCase(), keyValue.get().value()));
        
        return Optional.of("Got it! Saved " + keyValue.get().key() + " as " +
            keyValue.get().value() + ".");
      }

      Optional<Key<String>> key = keyParser.parse(input);

      if (key.isPresent()) {
        // get key's value
        Optional<String> value = keyValueStore.get(key.get());
        if (value.isPresent()) {
          return Optional.of("Your " + key.get().key() + " is " +
              value.get() + ".");
        } else {
          return Optional.of("Sorry, I don't think you ever told me what '" +
              key.get().key() + "' was.");
        }
      }
    } catch (IOException e) {
      return Optional.absent();
    }

    return Optional.absent();
  }
}
