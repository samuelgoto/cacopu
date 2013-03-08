package com.cacopu.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

class WolframAlpha {
  private final DocumentBuilder db;
  private final Api api;

  interface Api {
    Reader fetch(String query) throws IOException;
  }

  static class WebApi implements Api {
    final String APP_ID = "UAE542-X7Y2GT8QRQ";
    @Override
    public Reader fetch(String query) throws IOException {
      URL url = new URL("http://api.wolframalpha.com/v2/query?input=" +
          URLEncoder.encode(query) +
          "&appid=" + APP_ID);
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(url.openStream()));
      return reader;
    }
  }

  @Inject
  WolframAlpha(Api api) throws ParserConfigurationException {
    this.api = api;

    DocumentBuilderFactory factory =
      DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    // Enables validation of xml document.
    factory.setValidating(true);
    db = factory.newDocumentBuilder();
  }

  private void assertResult(Document doc) {
    NodeList list = doc.getElementsByTagName("queryresult");
    Preconditions.checkState(list.getLength() == 1, "Only one result allowed");

    String error = list.item(0).getAttributes().getNamedItem("error").getTextContent();
    Preconditions.checkState("false".equals(error), "No errors");
  }

  private String plainText(Node node) {
    NodeList plainText = ((Element) node).getElementsByTagName("plaintext");

    Preconditions.checkState(plainText.getLength() > 0);

    return plainText.item(0).getTextContent();
  }

  private Optional<String> value(Node node, String key) {
    if (node.getNodeType() == Node.ELEMENT_NODE &&
        node.getAttributes().getNamedItem("id") != null &&
        key.equals(node.getAttributes().getNamedItem("id").getTextContent())) {
      return Optional.of(plainText(node));
    }

    return Optional.absent();
  }

  public String solve(String query) throws SAXException, IOException {
    InputSource data = new InputSource(api.fetch(query));

    Document doc = db.parse(data);

    assertResult(doc);

    NodeList list = doc.getElementsByTagName("pod");

    for (int i = 0; i < list.getLength(); i++) {
      Node node = list.item(i);
      Optional<String> value = value(node, "Definition:WordData");
      if (value.isPresent()) {
        return value.get();
      }
    }

    for (int i = 0; i < list.getLength(); i++) {
      Node node = list.item(i);
      Optional<String> value = value(node, "Result");
      if (value.isPresent()) {
        return value.get();
      }
    }

    for (int i = 0; i < list.getLength(); i++) {
      Node node = list.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        return plainText(node);
      }
    }

    NodeList didYouMeans = doc.getElementsByTagName("didyoumean");

    for (int i = 0; i < didYouMeans.getLength();) {
      Node node = didYouMeans.item(i);
      return "Did you mean '" + node.getTextContent().trim() + "'?";
    }

    throw new IOException("result not found");
  }

  private String domToString(Node node) {
    try {
      TransformerFactory transFactory = TransformerFactory.newInstance();
      Transformer transformer = transFactory.newTransformer();
      StringWriter buffer = new StringWriter();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.transform(new DOMSource(node),
          new StreamResult(buffer));
      return buffer.toString();
    } catch (TransformerConfigurationException e) {
      return "";
    } catch (TransformerException e) {
      return "";
    }
  }
}
