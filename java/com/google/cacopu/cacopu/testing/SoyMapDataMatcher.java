package com.cacopu.testing;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

import com.google.template.soy.data.SoyMapData;


public class SoyMapDataMatcher implements IArgumentMatcher {
  private final SoyMapData expected;
  
  SoyMapDataMatcher(SoyMapData expected) {
    this.expected = expected;
  }
  
  public static SoyMapData of(SoyMapData expected) {
    EasyMock.reportMatcher(new SoyMapDataMatcher(expected));
    return null;
  }
  
  @Override
  public void appendTo(StringBuffer buffer) {
    buffer.append("contains(" + expected + ")");
  }
  
  private static boolean contains(SoyMapData first, SoyMapData second) {
    for (String key : first.getKeys()) {
      if (first.get(key) instanceof SoyMapData) {
        if (second.get(key) == null ||
            !(second.get(key) instanceof SoyMapData)) {
          return false;
        }
        if (!contains((SoyMapData) first.get(key), (SoyMapData) second.get(key))) {
          return false;
        }
      } else if (!first.get(key).equals(second.get(key))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean matches(Object to) {
    return contains(expected, (SoyMapData) to);
  }
}
