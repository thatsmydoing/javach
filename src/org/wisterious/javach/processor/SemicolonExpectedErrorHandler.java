package org.wisterious.javach.processor;

public class SemicolonExpectedErrorHandler implements ErrorHandler {
  public boolean accepts(String errorMessage) {
    return errorMessage.equals("';' expected");
  }
  
  public String process(int lineNumber, String code) {
    
  
    CodeScanner cs = new CodeScanner(code);
    
    
    
    if(code.matches("(?s).*\\)\\s*\\(.*")) {
      return "you have probably used a '(' instead of a '{'";
    }
    
    String localizedCode = cs.getLines(lineNumber - 1, lineNumber + 1);
    
    if(localizedCode.matches("(?s).*(\\d+|\\((\\d+|\\w+)\\))(\\w+|\\((\\d+|\\w+)\\)).*")) {
      return "please use * for multiplication";
    }
    
    if(localizedCode.matches("(?s).*\\w+\\s+[xX]\\s+\\w+.*")) {
      return "please use * for multiplication";
    }
    
    if(localizedCode.matches("(?s).*\\w\\s*(\\+\\+|\\-\\-)\\s*[^);\\s].*")) {
      return "the ++ or -- is a unary operator";
    }
    
    if(localizedCode.matches("(?s).*\\w+[a-zA-Z](\\(.*\\))?:\\s+.*")) {
      return "you may have used a : instead of a ;";
    }
    
    if(localizedCode.matches("(?s).*for\\(.*,.*\\).*")) {
      return "you may have used a , instead of a ;";
    }
    
    if(localizedCode.matches("(?s).*\\w+,\\w+.*")) {
      return "you may have used a , instead of a .";
    }
    
    String lineCode = cs.getLine(lineNumber).trim();
    lineCode = lineCode.replace("public ", "");
    lineCode = lineCode.replace("private ", "");
    lineCode = lineCode.replace("protected ", "");
    lineCode = lineCode.replace("static ", "");
    lineCode = lineCode.replace("abstract ", "");
    lineCode = lineCode.replace("final ", "");
    lineCode = lineCode.replace("synchronized ", "");
        
    if(lineCode.matches("\\w+ \\w+ \\w+\\(.*\\).*")) {
      return "you may have used a space in your method name";
    }
    
    if(lineCode.matches("\\w+ \\w+ \\w+;.*")) {
      return "you may have used a space in your variable name";
    }
    
    cs.goToEndOfLine(lineNumber);
    
    if(cs.searchOnLine(")")) {
      cs.nextWord();      
      String nextWord = cs.nextWord();
      if(nextWord.equals("{")) {
        cs.goToEndOfLine(lineNumber - 1);
        if(!cs.previousWord().equals("}")) {
          return "you may have forgotten to close the previous block";
        }
        else if(nextWord.equals(";")) {
          if(cs.getLines(lineNumber - 3, lineNumber + 1).matches("(?s).*[^\\s\\{,]\\s*\\n.*")) {
            return "the previous line does not have a semicolon";
          }
        }
      }
    }
    else {
      if(cs.getLines(lineNumber - 3, lineNumber + 1).matches("(?s).*[^\\s\\{,]\\s*\\n.*")) {
        return "the previous line does not have a semicolon";
      }
    }
    
    return "semicolon expected";
  }
}