package org.wisterious.javach.processor;

import java.util.*;

public class CannotFindSymbolErrorHandler implements ErrorHandler {
	
	static HashSet<String> methods; {
		methods = new HashSet<String>();
		methods.add("println");
		methods.add("printf");
	}
	
	static HashSet<String> classes; {
		classes = new HashSet<String>();
		classes.add("String");
		classes.add("void");
		classes.add("boolean");
		classes.add("int");
		classes.add("long");
		classes.add("double");
		classes.add("float");
		classes.add("byte");
		classes.add("char");
    classes.add("return");
	}
	
	String errorMessage;
	
	public boolean accepts(String errorMessage) {
		this.errorMessage = errorMessage;
		return errorMessage.startsWith("cannot find symbol");
	}
	
	public String process(int lineNumber, String code) {
		CodeScanner cs = new CodeScanner(code);
		String[] temp = errorMessage.split(" ");
		String symbol = temp[4];
		String name = temp[5];
		if(symbol.equals("variable")) {
      cs.goToEndOfLine(lineNumber);
      if(cs.searchOnLine("=")) {
        cs.nextWord();
        if(cs.nextWord().equals("(")) {
          return "you may have used the equal sign in your method call";
        }
      }
			return "did you mistype '" + name + "'? or you may have forgotten to declare it";
		}
		if(symbol.equals("method")) {
			String missingMethod = Tools.getMethodName(name);
			int min = 6;
			String best = "";
			for(String method : methods) {
				if(method.equals(missingMethod)) {
					return "you probably forgot to type the fully qualified name of the method, or are passing it the wrong parameters";
				}
				int dist = Tools.levenshteinDistance(method, missingMethod);
				if(dist < min) {
					min = dist;
					best = method;
				}
			}
			if(min < 6) {
				return "you probably misspelled " + best + " as " + name;
			}
			return "you may have mistyped the '" + missingMethod + "', or you may be passing wrong parameters";
		}
		if(symbol.equals("class")) {
			int min = 3;
			String best = "";
			for(String clas : classes) {
				int dist = Tools.levenshteinDistance(clas, name);
				if(dist < min) {
					min = dist;
					best = clas;
				}
			}
			if(min < 3) {
				return "you probably misspelled " + best + " as " + name;
			}
			return "you may have mistyped '" + name + "' or you have forgotten to import the required package";
		}
		return errorMessage;
	}
}
