package com.cacopu.server;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.cacopu.server.Bots.Bot;
import com.google.common.base.Optional;
import com.google.inject.Inject;

class WolphramAlphaBot implements Bot {
  private final WolframAlpha wolfram;
  
  @Inject
  WolphramAlphaBot(WolframAlpha wolframAlpha) {
    this.wolfram = wolframAlpha;
  }
  
  @Override
  public Optional<String> parse(String input) {
    try {
      return Optional.of(wolfram.solve(input));
    } catch (SAXException e) {
      return Optional.absent();
    } catch (IOException e) {
      return Optional.absent();
    } catch (IllegalStateException e) {
      return Optional.of("Oops, sorry :( something went wrong :(");
    }
  }
}
