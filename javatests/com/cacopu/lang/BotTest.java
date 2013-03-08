package com.cacopu.lang;

import junit.framework.TestCase;

public class BotTest extends TestCase {
  public void testDefines() {
    // Not working:
    // the phone number of my mom is 33969322
    // my mom's phone number is FOO
    
    // Types
    assertParse("SSN 1234.456-78",
        "");
    assertParse("phone (650) 450 6457",
        "");
    assertParse("address 360 Santa Elena Terrace, Sunnyvale, CA",
        "");
    assertParse("My phone is 650 450 6457",
        "");
    assertParse("Dani's phone is 650 450 6457",
    "");

    // Syntaxes
    assertParse("phone (650) 450 6457",
        "");
    assertParse("My phone number is (650) 450 6457",
        "");
    assertParse("My phone is (650) 450 6457",
        "");
    assertParse("phone = (650) 450 6457",
        "");
    assertParse("Remind that my phone number is (650) 450 6457",
        "");
    assertParse("define phone as (650) 450 6457",
        "");
    assertParse("record phone as (650) 450 6457",
        "");
    assertParse("let phone be (650) 450 6457",
        "");
    assertParse("save phone as (650) 450 6457",
        "");
  }

  public void testQueries() {
    assertParse("what is my phone number ?",
        "");
    assertParse("what's my phone number ?",
        "");
    assertParse("what's my phone number, please ?",
        "");
    assertParse("please, what's my phone number ?",
        "");
    assertParse("emma, what's my phone number ?",
        "");
    assertParse("dear, what's my phone number ?",
        "");
    assertParse("can I please have my phone number ?",
        "");
    assertParse("what's my phone # ?",
        "");
    assertParse("my phone number ?",
        "");
    assertParse("phone number ?",
        "");
    assertParse("phone ?",
        "");
    assertParse("get my phone number.",
        "");
    assertParse("fetch my phone number.",
        "");
  }
  
  public void testReminders() {
    assertParse("ping me in 2 minutes",
        "");
    assertParse("remind me of my wife's birthday next week",
        "");
    assertParse("ping me 2 minutes before my meetings today",
        "");
  }

  public void testDates() {
    assertParse("now",
        "");
    assertParse("today",
        "");
    assertParse("tomorrow",
        "");
    assertParse("yesterday",
        "");
    assertParse("in 2 seconds",
        "");
    assertParse("in 2 minutes",
        "");
    assertParse("in 2 days",
        "");
    assertParse("in 2 weeks",
        "");
    assertParse("in 2 years",
        "");
    assertParse("in a second",
        "");
    assertParse("in a day",
        "");
    assertParse("in a week",
        "");
    assertParse("in a year",
        "");
    assertParse("next week",
        "");
    assertParse("next monday",
        "");
    assertParse("2 weeks from today",
        "");
    assertParse("5/2/1982",
        "");
    assertParse("May, 2nd 1982",
        "");
    assertParse("2nd of May, 1982",
        "");
  }
  
  public void testSettings() {
    assertParse("ping me 2 minutes before my meetings",
        "");
    assertParse("ping me over IM",
        "");
    assertParse("ping me over email",
        "");
    assertParse("stop IMing me",
        "");
    assertParse("stop emailing me",
        "");
  }
  
  public void testControls() {
   assertParse("help",
       ""); 
   assertParse("help me",
       "");
  }
  
  public void testCommands() {
    assertParse("call home",
        "");
  }

  private void assertParse(String command, String expected) {
    assertEquals(expected, new Bot().tell(command));
  }
}
