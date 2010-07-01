package org.wisterious.javach;

import au.com.bytecode.opencsv.*;
import java.io.*;
import java.util.*;
import javax.tools.*;
import org.wisterious.javach.processor.*;

public class Tester {
  static FileFilter dirFilter = new FileFilter() {
    public boolean accept(File name) {
      return name.isDirectory();
    }
  };
  
  static FileFilter sourceFilter = new FileFilter() {
    public boolean accept(File file) {
      if(file.isFile()) {
        return file.getName().indexOf(".java") >= 0;
      }
      return false;
    }
  };
  
  static CSVWriter gcjWriter;
  static CSVWriter javacWriter;
  static CSVWriter javachWriter;
  
  public static void main(String[] args) throws Exception {
    gcjWriter = new CSVWriter(new FileWriter("gcj.csv"));
    javacWriter = new CSVWriter(new FileWriter("javac.csv"));
    javachWriter = new CSVWriter(new FileWriter("javach.csv"));
    
    String[] headings = new String[5];
    headings[0] = "ErrorType";
    headings[1] = "FileName";
    headings[2] = "Line";
    headings[3] = "Col";
    headings[4] = "Message";
    
    gcjWriter.writeNext(headings);
    javacWriter.writeNext(headings);
    javachWriter.writeNext(headings);
    
    File currentDirectory = new File(args[0]);
    for(File directory : currentDirectory.listFiles(dirFilter)) {
      for(File source : directory.listFiles(sourceFilter)) {
        runJavac(source);
        runGCJ(source);
        runJavach(source);
      }
    }
    gcjWriter.close();
    javacWriter.close();
    javachWriter.close();
  }
  
  public static void runJavac(File source) throws Exception {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    
    ArrayList<File> files = new ArrayList<File>();
    files.add(source);
    
    Iterable<? extends JavaFileObject> compilationUnits =
        fileManager.getJavaFileObjectsFromFiles(files);
    
    compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();
    
    for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
      String message = diagnostic.getMessage(null);
      int pos = message.indexOf(":");
      pos = message.indexOf(":", pos+1) + 2;
      message = message.substring(pos);
      
      String[] writeVal = new String[5];
      writeVal[0] = source.getParentFile().getName();
      writeVal[1] = source.getName();
      writeVal[2] = diagnostic.getLineNumber() + "";
      writeVal[3] = diagnostic.getColumnNumber() + "";
      writeVal[4] = message;
      
      javacWriter.writeNext(writeVal);
      break;
    }
    
    fileManager.close();
  }
  
  public static void runGCJ(File source) throws Exception {
    String[] params = new String[2];
    params[0] = "gcj";
    params[1] = source.getPath();
  
    Runtime rt = Runtime.getRuntime();
    Process p = rt.exec(params);
    Scanner sc = new Scanner(p.getErrorStream());
    
    ArrayList<String> output = new ArrayList<String>();
    
    while(sc.hasNextLine()) {
      output.add(sc.nextLine());
    }
    
    if(output.size() >= 3) {
      int start = 0;
      String[] writeVal = new String[5];
      writeVal[0] = source.getParentFile().getName();
      writeVal[1] = source.getName();
      while(start < output.size()) {
        String message = output.get(start++);
        int pos = message.indexOf("error: ");
        if(pos < 0) continue;
        
        pos = message.indexOf(":");
        writeVal[2] = message.substring(pos+1, message.indexOf(":", pos+1));
        pos = message.indexOf("error: ", pos+1) + 7;
        writeVal[4] = message.substring(pos);
        start += 1;
        writeVal[3] = output.get(start).indexOf("^") - 2 + "";
        
        gcjWriter.writeNext(writeVal);
        return;
      }
    }
  }
  
  public static void runJavach(File source) throws Exception {		
		String[] params = new String[2];
    params[0] = "javac";
    params[1] = source.getPath();
  
    Runtime rt = Runtime.getRuntime();
    Process p = rt.exec(params);
    Scanner sc = new Scanner(p.getErrorStream());
    
    ArrayList<String> output = new ArrayList<String>();
    
    while(sc.hasNextLine()) {
      output.add(sc.nextLine());
    }
    
    if(output.size() >= 3) {
      int start = 0;
      String[] writeVal = new String[5];
      writeVal[0] = source.getParentFile().getName();
      writeVal[1] = source.getName();
      while(start < output.size()) {
        String message = output.get(start);
        String column = output.get(start+2);
        int pos = message.indexOf(":");
        
        Scanner reader = new Scanner(new File(source.getPath()));
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
        
        writeVal[2] = lineNumber + "";
        writeVal[3] = columnNumber + "";
        
        Processor pr = new Processor();
        pr.addHandler(new BraceExpectedErrorHandler());
        pr.addHandler(new BracketExpectedErrorHandler());
        pr.addHandler(new IncompatibleTypesErrorHandler());
        pr.addHandler(new CannotFindSymbolErrorHandler());
        pr.addHandler(new SemicolonExpectedErrorHandler());
        writeVal[4] = pr.process(message, lineNumber, sb.toString());
        
        javachWriter.writeNext(writeVal);
        return;
      }
    }
  }
}