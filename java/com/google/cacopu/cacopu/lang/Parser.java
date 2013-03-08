package com.cacopu.lang;

import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class Parser {
  private static final LexicalizedParser lp = LexicalizedParser.loadModel(
      "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");

  public interface Solution {
    void solve();
  }
  
  static Solution nullSolution() {
    return new Solution() {
      @Override
      public void solve() {
      }
    };
  }
  
  public Solution parse(String sentence) {
   Tree tree = lp.apply(tokenize(sentence));
   return nullSolution();
  }
  
  List<CoreLabel> tokenize(String sentence) {
    TokenizerFactory<CoreLabel> tokenizerFactory = 
      PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
    List<CoreLabel> rawWords = 
      tokenizerFactory.getTokenizer(new StringReader(sentence)).tokenize();
    return rawWords;
  }
  
  TregexMatcher match(Tree tree, String regex) {
    TregexPattern p = TregexPattern.compile(regex);
    TregexMatcher matcher = p.matcher(tree);
    return matcher;
  }
}
