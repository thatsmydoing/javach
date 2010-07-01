package org.wisterious.javach.processor;

import java.io.File;
import java.util.Scanner;
import java.util.HashSet;

public class BraceExpectedErrorHandler implements ErrorHandler {

	static HashSet<String> modifiers; {
		modifiers = new HashSet<String>();
		modifiers.add("public");
		modifiers.add("private");
		modifiers.add("protected");
		modifiers.add("int");
		modifiers.add("double");
		modifiers.add("String");
		modifiers.add("float");
		modifiers.add("long");
		modifiers.add("byte");
		modifiers.add("char");
		modifiers.add("boolean");
	}

	public boolean accepts(String errorMessage) {
		return errorMessage.equals("'{' expected");
	}
	
	public String process(int lineNumber, String code) {
		CodeScanner cs = new CodeScanner(code);
		cs.goToEndOfLine(lineNumber);
		cs.searchBackward("class");		
		boolean inClass = false;
		boolean foundBrace = cs.searchBackward("{");
		if(foundBrace) inClass = cs.searchBackward("class");
		if(foundBrace && !inClass) cs.searchForward("class");
		if(inClass) cs.searchForward("class");
		cs.nextWord();
		cs.nextWord();
		String word3 = cs.nextWord();
		String word4 = cs.nextWord();
		if(word3.equals("(")) {
			if(inClass) {
				return "you may have used class as a type";
			}
			if(word4.equals(")")) {
				return "you may have put parameters '()' in the class declaration";
			}
			return "you may have used a parentheses '(', instead of a brace '{'";
		}
		if(word3.equals("[")) {
			return "you may have used a bracket '[', instead of a brace '{'";
		}
		if(word3.equals(";")) {
			if(word4.equals("{") || word4.equals("[") || word4.equals("(")) {
				return "there may be an extra semicolon in the class declaration";
			}
			return "you may have used class as a type";
		}
		if(word3.equals("throws")) {
			return "you may have put a throws statement in the class declaration";
		}
		if(word3.equals("=")) {
			return "you may have used class as a type";
		}
		for(String modifier : modifiers) {
			if(Tools.levenshteinDistance(modifier, word3) < 2) {
				return "you forgot the opening '{'";
			}
		}
		return "you may have used a space in the class name";
	}
	
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(new File(args[0]));
		String s = "";
		while(sc.hasNextLine()) {
			s += sc.nextLine() + "\n";
		}
		BraceExpectedErrorHandler beeh = new BraceExpectedErrorHandler();
		System.out.println(beeh.process(Integer.parseInt(args[1]), s));
	}
}
