package com.cacopu.server;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cacopu.server.KeyValues.Key;
import com.cacopu.server.KeyValues.KeyValue;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public class Parsers {
  interface Parser<K> {
    Optional<K> parse(String input);    
  }

  static abstract class RegexParser<K> implements Parser<K> {
    private final Pattern pattern;

    RegexParser(Pattern pattern) {
      this.pattern = pattern;
    }

    RegexParser(String pattern) {
      this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public Optional<K> parse(String input) {
      Matcher matcher = pattern.matcher(trim(input));
      
      if (!matcher.matches()) {
        return Optional.absent();
      }
      
      return Optional.of(matches(matcher));
    }
    
    protected abstract K matches(Matcher matcher);
  }

  static String trim(String input) {
    return input.replaceAll("\\s+", " ")
        .replaceAll("\\s+$", "")
        .replaceAll("^\\s+", "");
  }
  
  static class RegexKeyValueParser extends RegexParser<KeyValue<String, String>> {
    RegexKeyValueParser(String pattern) {
      super(pattern);
    }

    @Override
    protected KeyValue<String, String> matches(Matcher matcher) {
      return KeyValues.of(matcher.group(1), matcher.group(2));
    }
  }

  static class RegexKeyParser extends RegexParser<Key<String>> {
    RegexKeyParser(String pattern) {
      super(pattern);
    }

    @Override
    protected Key<String> matches(Matcher matcher) {
      return KeyValues.of(matcher.group(1));
    }
  }

  static class CompositeParser<K> implements Parser<K> {
    private final ImmutableList<Parser<K>> parsers;
    
    CompositeParser(Set<Parser<K>> parsers) {
      this.parsers = ImmutableList.copyOf(parsers);
    }
    
    @Override
    public Optional<K> parse(String input) {
      for (Parser<K> parser : parsers) {
        Optional<K> result = parser.parse(input);
        if (result.isPresent()) {
          return result;
        }
      }
      return Optional.absent();
    }
  }
}
