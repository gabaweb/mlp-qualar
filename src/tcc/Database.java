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
		statement.executeUpdate("CREATE TABLE `ENTRADAS` (\r\n" + 
				"	`ID`	INTEGER NOT NULL,\r\n" + 
				"	`ID_ESTACAO`	INTEGER NOT NULL,\r\n" + 
				"	`ID_VARIAVEL`	INTEGER NOT NULL,\r\n" + 
				"	`HORARIO`	TEXT NOT NULL,\r\n" + 
				"	`VALOR`	REAL,\r\n" + 
				"	PRIMARY KEY(`ID`)\r\n" + 
				");");
		
		statement.executeUpdate("DROP TABLE IF EXISTS ENTRADAS_TRATADAS");
		statement.executeUpdate("CREATE TABLE `ENTRADAS_TRATADAS` (\r\n" + 
				"	`ID`	INTEGER NOT NULL,\r\n" + 
				"	`ID_ESTACAO`	INTEGER NOT NULL,\r\n" + 
				"	`ID_VARIAVEL`	INTEGER NOT NULL,\r\n" + 
				"	`HORARIO`	TEXT NOT NULL,\r\n" + 
				"	`VALOR`	REAL,\r\n" + 
				"	PRIMARY KEY(`ID`)\r\n" + 
				");");
		
		statement.executeUpdate("DROP TABLE IF EXISTS VARIAVEIS");
		statement.executeUpdate("CREATE TABLE `VARIAVEIS` (\r\n" + 
				"	`ID`	INTEGER NOT NULL,\r\n" + 
				"	`NOME`	TEXT NOT NULL,\r\n" + 
				"	PRIMARY KEY(`ID`)\r\n" + 
				");");
		statement.executeUpdate("INSERT INTO VARIAVEIS (NOME) VALUES ('MP10')");
		
		statement.executeUpdate("DROP TABLE IF EXISTS ESTACOES");
		statement.executeUpdate("CREATE TABLE `ESTACOES` (\r\n" + 
				"	`ID`	INTEGER NOT NULL,\r\n" + 
				"	`NOME`	TEXT,\r\n" + 
				"	PRIMARY KEY(`ID`)\r\n" + 
				");");
		statement.executeUpdate("INSERT INTO ESTACOES (NOME) VALUES ('Limeira')");
		statement.executeUpdate("INSERT INTO ESTACOES (NOME) VALUES ('Piracicaba')");
		statement.executeUpdate("INSERT INTO ESTACOES (NOME) VALUES ('Campinas Taquaral')");
		statement.executeUpdate("INSERT INTO ESTACOES (NOME) VALUES ('Campinas Centro')");

		connection.close();

	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		Database database = new Database();
	
		database.create();
		
	}

}