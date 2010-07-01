package org.wisterious.javach;

import java.util.*;
import java.io.*;
import org.wisterious.javach.processor.*;

public class JavacRunner {
  static Processor p;

  public static void main(String[] args) throws Exception {
    p = new Processor();
		p.addHandler(new BraceExpectedErrorHandler());
		p.addHandler(new BracketExpectedErrorHandler());
		p.addHandler(new IncompatibleTypesErrorHandler());
		p.addHandler(new CannotFindSymbolErrorHandler());
    p.addHandler(new SemicolonExpectedErrorHandler());
  
    String[] params = new String[args.length+1];
    params[0] = "javac";
    for(int i = 0; i < args.length; i++) {
      params[i+1] = args[i];
    }
    try {
      Runtime rt = Runtime.getRuntime();
      Process p = rt.exec(params);
      Scanner sc = new Scanner(p.getErrorStream());
      
      ArrayList<String> output = new ArrayList<String>();
      
      while(sc.hasNextLine()) {
        output.add(sc.nextLine());
      }
      
      if(output.size() >= 3) {
        parseMessage(output, 0);
      }
    }
    catch (Exception e) {
      System.exit(0);
    }
  }
  
  public static void parseMessage(List<String> output, int start) throws Exception {
    String message = output.get(start);
    String column = output.get(start+2);
    int pos = message.indexOf(":");
    String fileName = message.substring(0, pos);
    Scanner reader = new Scanner(new File(fileName));
    StringBuilder sb = new StringBuilder();
    while(reader.hasNextLine()) {
      sb.append(reader.nextLine() + "\n");
    }
    int lineNumber = Integer.parseInt(message.substring(pos+1, message.indexOf(":", pos+1)));
    pos = message.indexOf(":", pos+1) + 2;
    message = message.substring(pos);
    if(message.equals("incompatible types")) {
      String found = output.get(start+1).substring(output.get(start+1).indexOf(":")+2);
      String expected = output.get(start+2).substring(output.get(start+2).indexOf(":")+2);
      message += String.format(" - found %s but expected %s", found, expected);
      column = output.get(start+4);
    }
    if(message.equals("cannot find symbol")) {
      String symbol = output.get(start+1);
      symbol = symbol.substring(symbol.indexOf(":")+1);
      message += " -" + symbol;
      column = output.get(start+4);
    }
    int columnNumber = column.indexOf("^") + 1;
    System.out.printf("Line: %d, Column: %d, Error: %s%n", lineNumber, columnNumber, message);
    
    System.out.println(p.process(message, lineNumber, sb.toString()));
  }
}