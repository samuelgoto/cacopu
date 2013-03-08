package com.cacopu.server;

import junit.framework.TestCase;

public class ParsersTest extends TestCase {
  public void testTrimming() {
    assertEquals("a b", Parsers.trim("a  b"));
    assertEquals("a", Parsers.trim("a "));
    assertEquals("a", Parsers.trim(" a"));
    assertEquals("a", Parsers.trim("a  "));
    assertEquals("a", Parsers.trim("  a"));
    assertEquals("hello world how is it going ?",
        Parsers.trim(" hello world how  is it going  ? "));
  }  
}
