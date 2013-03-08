package com.cacopu.server;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class Bots {
  
  interface Bot {
    Optional<String> parse(String input);
  }
  
  static class CompositeBot implements Bot {
    private final ImmutableList<Bot> bots;
    
    CompositeBot(List<Bot> bots) {
      this.bots = ImmutableList.copyOf(bots);
    }
    
    @Override
    public Optional<String> parse(String input) {
      for (Bot bot : bots) {
        Optional<String> output = bot.parse(input); 
        if (output.isPresent()) {
          return output;
        }
      }
      return Optional.absent();
    }
  }

  public static Bot alwaysResponds(final String message) {
    return new Bot() {
      @Override
      public Optional<String> parse(String input) {
        return Optional.of(message);
      }    
    };
  }
  
  
  static Bot compose(Bot... bots) {
    return new CompositeBot(Lists.newArrayList(bots));
  }
  
  //static Bot bot(KeyValueStore<String, String> store) {
  //  return new CompositeBot(ImmutableList.<Bot>of(
  //      new ReminderBot(keyValueParser(), keyParser(), store),
  //      new WolphramAlphaBot()
  //      ));
  //}
}
