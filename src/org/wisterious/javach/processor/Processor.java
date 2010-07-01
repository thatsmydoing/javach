package org.wisterious.javach.processor;

import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.io.File;

public class Processor {
	
	private Set<ErrorHandler> handlers;

	public Processor() {
		handlers = new HashSet<ErrorHandler>();
	}
	
	public void addHandler(ErrorHandler h) {
		handlers.add(h);
	}
	
	public String process(String errorMessage, int lineNumber, String code) {
		for(ErrorHandler h : handlers) {
			if(h.accepts(errorMessage)) {
				return h.process(lineNumber, code);
			}
		}
		return "unhandled: " + errorMessage;
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String errorMessage = args[0];
		int lineNumber = Integer.parseInt(args[1]);
		StringBuilder sb = new StringBuilder();
		if(sc.hasNextLine()) sc.nextLine();
		while(sc.hasNextLine()) {
			sb.append(sc.nextLine());
			sb.append("\n");
		}
		
		Processor p = new Processor();
		p.addHandler(new BraceExpectedErrorHandler());
		p.addHandler(new BracketExpectedErrorHandler());
		p.addHandler(new IncompatibleTypesErrorHandler());
		p.addHandler(new CannotFindSymbolErrorHandler());
		System.out.println(p.process(errorMessage, lineNumber, sb.toString()));
	}
}
