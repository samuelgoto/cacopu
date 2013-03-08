package com.cacopu.lang;

class Bot {
  String tell(String input) {
    return "";
  }
  
  static class Parser {
    Sentence parse(String input) {
      return null;
    }
  }
  
  interface Sentence {
    Subject subject();
    Verb verb();
    Noun noun();
  }
  
  interface Subject {
    
  }
  
  interface Verb {
    
  }
  
  interface Noun {
    
  }
  
  interface Adjective {
    
  }
  
  interface Adverb {
    
  }
}
