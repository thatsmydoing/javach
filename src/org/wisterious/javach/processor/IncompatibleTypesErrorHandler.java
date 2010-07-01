package org.wisterious.javach.processor;

public class IncompatibleTypesErrorHandler implements ErrorHandler {
	
	String errorMessage;
	
	public boolean accepts(String errorMessage) {
		this.errorMessage = errorMessage;
		return errorMessage.startsWith("incompatible types");
	}
	
	public String process(int lineNumber, String code) {
		CodeScanner cs = new CodeScanner(code);
		cs.goToEndOfLine(lineNumber);
		String[] temp = errorMessage.split(" ");
		String found = temp[4];
		String expected = temp[7];
		if(found.equals("void")) {
			return "there is nothing to assign since the method you are using returns void";
		}
		if(found.equals("<nulltype>")) {
			return "you cannot assign null to a primitive type";
		}
		if(cs.searchOnLine("switch") && expected.equals("int")) {
			return "you may only use int-types in a switch statement";
		}
		if((cs.searchOnLine("if") || cs.searchOnLine("while") || cs.searchOnLine("for")) && expected.equals("boolean")) {
			if(cs.searchOnLine("=") && cs.nextWord().equals("=")) {
				return "you probably meant '==' instead of just '='";
			}
			else {
				return "you may only use booleans in an if statement";
			}
		}
		if(cs.searchOnLine("return")) {
			return "the variable you are returning has a different type from the method return type";
		}
		return "unknown";
	}
}