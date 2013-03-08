package com.cacopu.lang;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import junit.framework.TestCase;

public class EnglishTest extends TestCase {
  public void testSimpleSentece() throws Exception {
    //LexicalizedParser lp = LexicalizedParser.loadModel(
    //    "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    Tree tree = null; // new Parser().parse("My phone is (650) 4506457");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    tree.pennPrint(new PrintWriter(out, true));
    System.out.println(new String(out.toByteArray()));
    
    TregexMatcher result = new Parser().match(tree, "S < (NP < /PRP\\$/ < NN=noun) < (VP < (VBZ < is) < (NP < CD=value))");

    assertTrue(result.find());
    String noun = result.getNode("noun").firstChild().value();
    assertEquals(1, result.getNode("noun").children().length);
    assertEquals("phone", noun);
    String value = result.getNode("value").firstChild().value();
    assertEquals(1, result.getNode("value").children().length);
    value = value
        .replaceAll("-LRB-", "(")
        .replaceAll("-RRB-", ")")
        // &nbsp;
        .replaceAll("\\u00A0", " ");
    assertEquals("(650) 4506457", value);
    result.getMatch().pennPrint();
  }

  
  private Document createDocument(Tree tree) throws ParserConfigurationException {
    DocumentBuilderFactory domFactory = 
      DocumentBuilderFactory.newInstance();
    domFactory.setNamespaceAware(true); 
    DocumentBuilder builder = domFactory.newDocumentBuilder();
    Document doc = builder.newDocument();
    doc.appendChild(createElement(doc, tree));
    return doc;
  }

  private Element createElement(Document doc, Tree tree) {
    CoreLabel label = (CoreLabel) tree.label();
    System.out.println("[" + label.value() + "]");
    Element result = doc.createElement(label.value()
        .replace('$', 'P')
        .replace(".", "DOT"));
    result.setAttribute("content", label.word());
    result.setNodeValue("foo");
    
    if (tree.isLeaf() || tree.isPreTerminal()) {
      return result;
    }

    for (Tree child : tree.children()) {
      result.appendChild(createElement(doc, child));
    }
    
    return result;
  }
  
  public void testXPath() throws Exception {
    DocumentBuilderFactory domFactory = 
      DocumentBuilderFactory.newInstance();
    domFactory.setNamespaceAware(true); 
    domFactory.setValidating(true);
    DocumentBuilder builder = domFactory.newDocumentBuilder();
    Document doc = builder.newDocument();
    Element el = doc.createElement("person");
    el.setAttribute("content", "foo");
    doc.appendChild(el);
    System.out.println(doc);
    XPath xpath = XPathFactory.newInstance().newXPath();
    // XPath Query for showing all nodes value
    XPathExpression expr = xpath.compile("//person");
    Object result = expr.evaluate(doc, XPathConstants.NODESET);
    NodeList nodes = (NodeList) result;
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      String value = node.getAttributes().getNamedItem("content").getNodeValue();
      System.out.println(value);
    }
  }

}
