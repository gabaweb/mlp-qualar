package tcc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		Connection connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		Statement statement = connection.createStatement();

		statement.executeUpdate("drop table if exists ENTRADAS");
		statement.executeUpdate("CREATE TABLE ENTRADAS (ID INTEGER PRIMARY KEY NOT NULL, ID_VARIAVEL INTEGER NOT NULL, VALOR REAL, HORARIO TEXT NOT NULL)");
		statement.executeUpdate("insert into person values(1, 'leo')");
		
		ResultSet rs = statement.executeQuery("select * from person");
		
		while (rs.next()) {
			// read the result set
			System.out.println("name = " + rs.getString("name"));
			System.out.println("id = " + rs.getInt("id"));
		}
		
	}
	
}