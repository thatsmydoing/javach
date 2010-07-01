package org.wisterious.javach;

import org.wisterious.javach.processor.*;
import java.sql.*;

public class DBRunner {
	public static void main(String[] args) throws Exception {
		Processor p = new Processor();
		p.addHandler(new BraceExpectedErrorHandler());
		p.addHandler(new BracketExpectedErrorHandler());
		p.addHandler(new IncompatibleTypesErrorHandler());
		p.addHandler(new CannotFindSymbolErrorHandler());
		
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:" + args[0]);
		PreparedStatement ps = conn.prepareStatement(
			"SELECT id, fileContents, messageLineNumber, messageText FROM CompileDelta WHERE" +
			" id > ? AND messageType = 'ERROR' AND messageText LIKE ? LIMIT 1");
		int counter = Integer.parseInt(args[2]);
		
		int prevID = 0;
		String messageText, fileContents;
		int messageLineNumber;
		
		ps.setInt(1, prevID);
		ps.setString(2, args[1] + "%");
		
		ResultSet rs = ps.executeQuery();
		while(rs.next()) {
			if(counter == 0) break;
			else counter--;
			prevID = rs.getInt(1);
			messageText = rs.getString(4);
			messageLineNumber = rs.getInt(3);
			fileContents = rs.getString(2);
			rs.close();
			
			System.out.printf("%d %s line %d%n", prevID, messageText, messageLineNumber);
			System.out.println(p.process(messageText, messageLineNumber, fileContents));
			System.out.println(fileContents);
			System.out.println();
			
			
			ps.setInt(1, prevID);
			rs = ps.executeQuery();
		}
		ps.close();
		conn.close();
	}
}
