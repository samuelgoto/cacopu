package com.cacopu.server;

import com.cacopu.server.Bots.Bot;
import com.cacopu.server.KeyValues;
import com.cacopu.server.KeyValues.Key;
import com.cacopu.server.KeyValues.KeyValue;
import com.google.common.base.Optional;

import junit.framework.TestCase;

public class ReminderBotTest extends TestCase {

  
  public void testUnderstandsReminder() {
    checkResult("Remind me that my ssn is 12345", KeyValues.of("ssn", "12345"));
    checkResult("remind me that my ssn is 12345", KeyValues.of("ssn", "12345"));
    checkResult("Remind me that my ssn is 12345.", KeyValues.of("ssn", "12345"));
    checkResult("Remind me that my ssn is 12345!", KeyValues.of("ssn", "12345"));
    checkResult("Remind me that my ssn is 12345 .", KeyValues.of("ssn", "12345"));
    checkResult("Remind  me that my ssn  is 12345 ", KeyValues.of("ssn", "12345"));
    checkResult("my ssn is 12345.", KeyValues.of("ssn", "12345"));
    checkResult("My ssn is 12345.", KeyValues.of("ssn", "12345"));
    checkResult("ssn = 12345.", KeyValues.of("ssn", "12345"));
    checkResult("ssn := 12345.", KeyValues.of("ssn", "12345"));
    checkResult("Set ssn to 12345.", KeyValues.of("ssn", "12345"));
    checkResult("Remind me that my phone number is 12345",
        KeyValues.of("phone number", "12345"));
    checkResult("Remind me that my phone number is 650 12345",
        KeyValues.of("phone number", "650 12345"));
    checkResult("Remind me that my phone number is (650) 450-6457",
        KeyValues.of("phone number", "(650) 450-6457"));
  }

  private void checkResult(String phrase, KeyValue<String, String> result) {
    Optional<KeyValue<String, String>> keyValue = ReminderBot.keyValueParser().parse(
      phrase);
    assertTrue(keyValue.isPresent());
    assertEquals(result, keyValue.get());
  }
  
  private void checkQuery(String phrase, Key<String> result) {
    Optional<Key<String>> keyValue = ReminderBot.keyParser().parse(phrase);
    assertTrue(keyValue.isPresent());
    assertEquals(result, keyValue.get());
  }

  public void testUnderstandsQuestions() {
    checkQuery("What is my SSN ?", KeyValues.of("SSN"));
    checkQuery("What's my SSN ?", KeyValues.of("SSN"));
    checkQuery("SSN ?", KeyValues.of("SSN"));
  }

  public void testSearchForAnswers() {
    Bot bot = bot();

    Optional<String> set = bot.parse("My SSN is 12345.");
    assertTrue(set.isPresent());
    assertEquals("Got it! Saved SSN as 12345.", set.get());
    
    Optional<String> get = bot.parse("What's my ssn?");
    assertTrue(get.isPresent());
    assertEquals("Your ssn is 12345.", get.get());
  }
  
  public void testStoresAndRetrieves() {
    Bot bot = bot();

    Optional<String> set = bot.parse("My SSN is 12345.");
    assertTrue(set.isPresent());
    assertEquals("Got it! Saved SSN as 12345.", set.get());
    
    Optional<String> get = bot.parse("What's my SSN?");
    assertTrue(get.isPresent());
    assertEquals("Your SSN is 12345.", get.get());
  }

  public void testAcknoledgesWhenItDoesNotKnow() {
    Optional<String> result = bot().parse("What's my SSN?");
    assertTrue(result.isPresent());
    assertEquals(result.get(), "Sorry, I don't think you ever told me what 'SSN' was.");
    // TODO(goto): provide a "did you mean" feature.
  }

  public void testDealsWithInputsItDoesntUnderstand() {
    checkDoesNotUnderstand("foo bar.");
    checkDoesNotUnderstand("hi");
    checkDoesNotUnderstand("how are you ?");
    checkDoesNotUnderstand("what's a computer?");    
  }

  private void checkDoesNotUnderstand(String input) {
    Optional<String> result = bot().parse(input);
    assertFalse(result.isPresent());
  }
  
  private Bot bot() {
    return Bots.compose(new ReminderBot(new KeyValues.FakeKeyValueStore()));
  }
}
