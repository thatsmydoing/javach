package org.wisterious.javach.processor;

public class CodeScanner {
	String code;
	int index;
	
	public CodeScanner(String text) {
		this.code = text;
		index = 0;
	}
	
	public void goToEndOfLine(int line) {
		index = -1;
		while(line-- > 0) {
			index = code.indexOf("\n", index+1);
		}
    if(index == -1) index = 0;
	}
	
	public boolean searchForward(String word) {
		int temp = code.indexOf(word, index+1);
		if(temp > 0) {
			index = temp;
			return true;
		}
		return false;
	}
	
	public boolean searchBackward(String word) {
		int temp = code.lastIndexOf(word, index-1);
		if(temp > 0) {
			index = temp;
			return true;
		}
		return false;
	}
	
	protected void goToStartOfWord() {
		if(isWhitespace(index)) {
			do {
				index++;
			} while(isWhitespace(index));
		}
		/*
		else {
			while(isNotWhitespace(index-1)) {
				index--;
			}
		}*/
	}
	
	protected void goToEndOfWord() {
		
		if(isNotWhitespace(index)) {
      if(!isIdentifier(index)) {
        //index++;
        return;
      }
			while(isIdentifier(index+1)) {
				index++;
			}
		}
    else {
      while(isWhitespace(index)) {
        index--;
      }
    }
	}
	
	protected boolean isWhitespace(int index) {
		if(index < 0) return false;
		if(index >= code.length()) return false;
		return Character.isWhitespace(code.charAt(index));
	}
	
	protected boolean isIdentifier(int index) {
		if(index < 0) return false;
		if(index >= code.length()) return false;
		String curr = code.substring(index, index+1);
		return !curr.matches("[=\\+\\-\\*\\/\\{\\}\\(\\);\\s]");
	}
	
	protected boolean isNotWhitespace(int index) {
		if(index < 0) return false;
		if(index >= code.length()) return false;
		return !Character.isWhitespace(code.charAt(index));
	}
	
	public String nextWord() {
		goToStartOfWord();
		if(index == code.length()) {
			return null;
		}
		int start = index;
		goToEndOfWord();
		String retval = code.substring(start, index+1);
		index++;
		return retval;
	}
	
	public String previousWord() {
		goToEndOfWord();
		if(index == code.length()) {
			return null;
		}
		int start = index;
		goToStartOfWord();
		String retval = code.substring(index, start+1);
		index--;
		return retval;
	}
	
	public boolean searchOnLine(String word) {
		int temp = index;
		if(code.charAt(temp) == '\n') temp--;
		int low = code.lastIndexOf("\n", temp);
		int high = code.indexOf("\n", temp);
		int result = code.indexOf(word, low);
		if(result < high && result >= 0) {
			index = result;
			return true;
		}
		return false;
		
	}
	
	public boolean searchForward(String word, int wordLimit) {
		int initial = index;
		for(int i = 0; i < wordLimit; i++) {
			String nw = nextWord();
			if(nw == null) {
				break;
			}
			else if(nw.equals(word)) {
				return true;
			}
		}
		index = initial;
		return false;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
  
  public String getLine(int line) {
    return getLines(line, line);
  }
  
  public String getLines(int start, int end) {
    goToEndOfLine(start-1);
    int startIndex = getIndex();
    goToEndOfLine(end);
    int endIndex = getIndex();
    return code.substring(startIndex, endIndex);
  }
	
	public static void main(String[] args) {
		String test = "int a= 5+67;";
		CodeScanner cs = new CodeScanner(test);
		System.out.println(cs.nextWord());
		System.out.println(cs.nextWord());
		System.out.println(cs.nextWord());
		System.out.println(cs.nextWord());
		System.out.println(cs.nextWord());
		System.out.println(cs.previousWord());
	}	
}
