package org.wisterious.javach.processor;

public class Tools {
	
	public static String getMethodName(String method) {
		return method.substring(0, method.indexOf("("));
	}
	
	public static int levenshteinDistance(String wordA, String wordB) {
		// d is a table with m+1 rows and n+1 columns
		int m = wordA.length();
		int n = wordB.length();
		int[][] d = new int[m+1][n+1];
	  
		for(int i = 0; i <= m; i++) {
			d[i][0] = i;
		}
		for(int j = 0; j <= n; j++) {
			d[0][j] = j;
		}	  
		for(int j = 1; j <= n; j++) {
			for(int i = 1; i <= m; i++) {
				if(wordA.charAt(i-1) == wordB.charAt(j-1)) {
					d[i][j] = d[i-1][j-1];
				}
				else {
					d[i][j] = Math.min(Math.min(d[i-1][j]+1, d[i][j-1]+1), d[i-1][j-1]+1);
				}
			}
		}
	  
	   return d[m][n];
	}
}
