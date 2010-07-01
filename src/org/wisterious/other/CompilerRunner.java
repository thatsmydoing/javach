import javax.tools.*;
import java.util.*;
import java.io.*;

public class CompilerRunner {
  public static void main(String[] args) throws Exception {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    
    ArrayList<File> files = new ArrayList<File>();
    for(String s : args) {
      if(s.endsWith(".java")) {
        files.add(new File(s));
      }
    }
    
    Iterable<? extends JavaFileObject> compilationUnits =
        fileManager.getJavaFileObjectsFromFiles(files);
    
    compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();
    
    for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
      String message = diagnostic.getMessage(null);
      int pos = message.indexOf(":");
      pos = message.indexOf(":", pos+1) + 2;
      message = message.substring(pos);
      
      System.out.format("%d %d %s%n",
        diagnostic.getLineNumber(),
        diagnostic.getColumnNumber(),
        message);
    }
    
    fileManager.close();
  }
}