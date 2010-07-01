package org.wisterious.javach.processor;

public interface ErrorHandler {
	public boolean accepts(String errorMessage);
	public String process(int lineNumber, String code); 
}
