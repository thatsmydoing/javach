package org.wisterious.javach.bluejplugin;

import org.wisterious.javach.processor.*;

import bluej.extensions.*;
import bluej.extensions.event.*;
import bluej.extensions.editor.*;

import java.util.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BluejPlugin extends Extension implements CompileListener {
  Processor p;
  BlueJ bluej = null;
  JFrame frame;
  JTextField messageField;
  
  public BluejPlugin() {
    p = new Processor();
    p.addHandler(new BraceExpectedErrorHandler());
		p.addHandler(new BracketExpectedErrorHandler());
		p.addHandler(new IncompatibleTypesErrorHandler());
		p.addHandler(new CannotFindSymbolErrorHandler());
    p.addHandler(new SemicolonExpectedErrorHandler());
  }

  public void startup(BlueJ bluej) {
    bluej.addCompileListener(this);
    this.bluej = bluej;
    
    // Frame parent = null;
    // try {
      // parent = bluej.getCurrentPackage().getFrame();
    // }
    // catch(Exception e) {
      // parent = bluej.getCurrentFrame();
    // }
    
    frame = new JFrame() {
      public void frameInit() {
        this.setTitle("Javach - additional help for error messages");
        this.setSize(400, 60);
        this.setVisible(true);
        messageField = new JTextField();
        this.add(messageField);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      }
    };
  }
  
  public void terminate() {}
  
  public boolean isCompatible() {
    return true;
  }
  
  public void compileError(CompileEvent event) {
    String message = event.getErrorMessage();
    
    
    int lineNumber = event.getErrorLineNumber();
    File target = null;
    for(File f : event.getFiles()) {
      target = f;
      break;
    }
    if(target != null) {

      try {
        Scanner reader = new Scanner(target);
        StringBuilder sb = new StringBuilder();
        while(reader.hasNextLine()) {
          sb.append(reader.nextLine() + "\n");
        }
        String result = p.process(message, lineNumber, sb.toString());
        messageField.setText(result);
        frame.toFront();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public void compileFailed(CompileEvent event) {
    
  }
  
  public void compileStarted(CompileEvent event) {}
  
  public void compileSucceeded(CompileEvent event) {}
  
  public void compileWarning(CompileEvent event) {}
  
  public String getVersion() {
    return "2010.06.12";
  }
  
  public String getName() {
    return "Javach Extension";
  }
  
  
  
  public String getDescription() {
    return "A helper for the java compiler, which aids in determining " +
            "the actual error more precisely.";
  }
  
  public URL getURL() {
    return null;
  }
}