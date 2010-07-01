package org.wisterious.javach.processor;

public class BracketExpectedErrorHandler implements ErrorHandler {
	
	String errorMessage;
	
	public boolean accepts(String errorMessage) {
		this.errorMessage = errorMessage;
		return errorMessage.equals("'[' expected") || 
			errorMessage.equals("'(' or '[' expected") ||
			errorMessage.equals("'(' expected");
	}
	
	public String process(int lineNumber, String code) {
		CodeScanner cs = new CodeScanner(code);
		
		cs.goToEndOfLine(lineNumber);
		int newIndex = 0;
		if(cs.searchBackward("new")) {
			newIndex = cs.getIndex();
		}
		
		cs.goToEndOfLine(lineNumber);
		cs.searchBackward("void");
		if(cs.getIndex() > newIndex) {
			cs.nextWord();
			String word2 = cs.nextWord();
			String word3 = cs.nextWord();
			if(word3 != null) {
				if(word3.equals("{")) {
					return "'(' expected after " + word2;
				}
				else if(word3.equals("[")) {
					return "use a parenthesis '(', not a bracket '['";
				}
				else if(word3.equals(";")) {
          return "void cannot be used as a variable type";
        }
        else for(String modifier : BraceExpectedErrorHandler.modifiers) {
					if(Tools.levenshteinDistance(modifier, word3) < 2) {
						return "you forgot the parameter list and opening '{'";
					}
				}
      }
      for(String modifier : BraceExpectedErrorHandler.modifiers) {
        if(Tools.levenshteinDistance(modifier, word2) < 2) {
          return "you may be using too many modifiers";
        }
      }
      cs.goToEndOfLine(lineNumber);
      if(cs.searchOnLine("if") || cs.searchOnLine("while") || cs.searchOnLine("for")) {
        return "did you forget to give a parameter list in your if/for/while statement";
      }
			return "spaces are not allowed in the method declaration";
		}
		else {
			if(errorMessage.equals("'[' expected")) {
				return "you forgot to specify the size of the array, or you are using new with a primitive incorrectly";
			}
			else {
				return "you are using new incorrectly or forgot to specify the size of the array";
			}
		}
	}
}
