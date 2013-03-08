package com.cacopu.server;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class WolframAlphaTest extends TestCase {

  public void testQuery() throws Exception {
    String xml = "" +
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<queryresult success='true' error='false'>" +
    "  <pod id='Result'>" +
    "    <subpod>" +
    "      <plaintext>hello world</plaintext>" +
    "    </subpod>" +
    "  </pod>" +
    "</queryresult>";

    String result = new WolframAlpha(api(xml)).solve("foo bar");

    assertEquals("hello world", result);
  }

  public void testResponses() throws Exception {
    String xml = "" +
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<queryresult success='true' error='false'>" +
    "  <pod title='Input interpretation'>" +
    "  </pod>" +
    "  <pod title='Response' id='Result'>" +
    "    <subpod>" +
    "      <plaintext>My name is Wolfram|Alpha</plaintext>" +
    "    </subpod>" +
    "  </pod>" +
    "</queryresult>";

    String result = new WolframAlpha(api(xml)).solve("What's your name ?");

    assertEquals("My name is Wolfram|Alpha", result);
  }

  public void testMultipleSubpods() throws Exception {
    String xml = "" +
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<queryresult success='true' error='false'>" +
    "  <pod title='Input interpretation'>" +
    "  </pod>" +
    "  <pod title='Response' id='Result'>" +
    "    <subpod>" +
    "      <plaintext>My name is Wolfram|Alpha</plaintext>" +
    "    </subpod>" +
    "    <subpod>" +
    "      <plaintext>Also known as the most aswesome machine :)</plaintext>" +
    "    </subpod>" +
    "  </pod>" +
    "</queryresult>";

    String result = new WolframAlpha(api(xml)).solve("What's your name ?");

    assertEquals("My name is Wolfram|Alpha", result);
  }

  public void testGetsFirstPod() throws Exception {
    String xml = "" +
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<queryresult success='true' error='false'>" +
    "  <pod>" +
    "    <subpod>" +
    "      <plaintext>This is the first pod</plaintext>" +
    "    </subpod>" +
    "  </pod>" +
    "</queryresult>";

    String result = new WolframAlpha(api(xml)).solve("foo bar");

    assertEquals("This is the first pod", result);
  }

  public void testDidYouMean() throws Exception {
    String xml = "" +
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<queryresult success='false' error='false'>" +
    "  <didyoumeans count='1'>" +
    "    <didyoumean score='0.5'>" +
    "      who is thiago" +
    "    </didyoumean>" +
    "  </didyoumeans>" +
    "</queryresult>";

    String result = new WolframAlpha(api(xml)).solve("who is thiago teodoro?");

    assertEquals("Did you mean 'who is thiago'?", result);
  }

  public void testFailure() throws Exception {
    String xml = "" +
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<queryresult success='false' error='true'>" +
    "  <pod id='Result'>" +
    "    <subpod>" +
    "      <plaintext>hello world</plaintext>" +
    "    </subpod>" +
    "  </pod>" +
    "</queryresult>";

    try {
      new WolframAlpha(api(xml)).solve("foo bar");
      fail("Should've thrown an exception.");
    } catch (IllegalStateException expected) {
    }
  }
  
  public void testWebApi() throws Exception {
    assertEquals("195 million people  (world rank: 5th)  (2010 estimate)",
        answer("brazilian population"));
    assertEquals("1 | noun | an expression of greeting\n2 | interjection | - A friendly, informal, casual greeting said when meeting someone.",
        answer("hi"));
    assertEquals("1 | noun | a machine for performing calculations automatically\n2 | noun | an expert at calculation or operating calculating machines",
        answer("what is a computer ?"));
    assertEquals("4.409 lb  (pounds)",
        answer("two kgs in pounds"));
    assertEquals("-9.444 °C  (degrees Celsius)", answer("15 F in celsius"));
  }

  private String answer(String question) throws SAXException, IOException, ParserConfigurationException {
    return new WolframAlpha(new WolframAlpha.WebApi()).solve(question);
  }

  private WolframAlpha.Api api(final Reader reader) {
    return new WolframAlpha.Api() {
      @Override
      public Reader fetch(String query) {
        return reader;
      }
    };
  }

  private WolframAlpha.Api api(final String xml) {
    return api(new StringReader(xml));
  }
}
