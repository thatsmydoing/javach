import java.util.*;

public class GCJRunner {
  

  public static void main(String[] args) {
    String[] params = new String[args.length+1];
    params[0] = "gcj";
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
        parseMessage(output);
      }
    }
    catch (Exception e) {
      System.exit(0);
    }
  }
  
  public static void parseMessage(List<String> output) {
    int start = 0;
    while(start < output.size()) {
      String message = output.get(start++);
      int pos = message.indexOf("error: ");
      if(pos < 0) continue;
      
      pos = message.indexOf(":");
      int lineNumber = Integer.parseInt(message.substring(pos+1, message.indexOf(":", pos+1)));
      pos = message.indexOf("error: ", pos+1) + 7;
      message = message.substring(pos);
      start += 1;
      int columnNumber = output.get(start).indexOf("^") - 2;
      System.out.printf("%d %d %s%n", lineNumber, columnNumber, message);
    }
  }
}