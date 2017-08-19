package tcc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	public void create() throws ClassNotFoundException, SQLException {

		Connection connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		Statement statement = connection.createStatement();

		statement.executeUpdate("DROP TABLE IF EXISTS ENTRADAS");
		statement.executeUpdate("CREATE TABLE ENTRADAS (ID INTEGER PRIMARY KEY NOT NULL, ID_VARIAVEL INTEGER NOT NULL, HORARIO TEXT NOT NULL, VALOR REAL)");
		statement.executeUpdate("DROP TABLE IF EXISTS ENTRADAS_TRATADAS");
		statement.executeUpdate("CREATE TABLE ENTRADAS_TRATADAS (ID INTEGER PRIMARY KEY NOT NULL, ID_VARIAVEL INTEGER NOT NULL, HORARIO TEXT NOT NULL, VALOR REAL)");
		statement.executeUpdate("DROP TABLE IF EXISTS VARIAVEIS");
		statement.executeUpdate("CREATE TABLE VARIAVEIS (ID INTEGER PRIMARY KEY NOT NULL, NOME TEXT NOT NULL)");
		statement.executeUpdate("INSERT INTO VARIAVEIS(ID, NOME) VALUES (NULL, 'MP10')");

		connection.close();

	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		Database database = new Database();
	
		database.create();
		
	}

}