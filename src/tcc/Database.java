package tcc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	public void create() throws ClassNotFoundException, SQLException {

		Connection connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		Statement statement = connection.createStatement();

		statement.executeUpdate("drop table if exists ENTRADAS");
		statement.executeUpdate("CREATE TABLE ENTRADAS (ID INTEGER PRIMARY KEY NOT NULL, ID_VARIAVEL INTEGER NOT NULL, VALOR REAL, HORARIO TEXT NOT NULL)");

		connection.close();

	}

}