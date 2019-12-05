package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ModelInstall {

	public void createDatabase() throws ClassNotFoundException, SQLException {

		Connection connection = DriverManager.getConnection("jdbc:sqlite:"+System.getenv("APP_MLP_QUALAR_HOME")+"database.db");
		
		Statement statement = connection.createStatement();
		statement.executeUpdate("DROP TABLE IF EXISTS ENTRADAS");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("CREATE TABLE `ENTRADAS` (`ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `ID_ESTACAO` INTEGER NOT NULL, `ID_VARIAVEL` INTEGER NOT NULL, `HORARIO` TEXT NOT NULL, `VALOR` REAL);");
		statement.close();

		statement = connection.createStatement();
		statement.executeUpdate("DROP TABLE IF EXISTS ENTRADAS_TRATADAS");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("CREATE TABLE `ENTRADAS_TRATADAS` (`ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `ID_ESTACAO` INTEGER NOT NULL, `ID_VARIAVEL` INTEGER NOT NULL, `HORARIO` TEXT NOT NULL, `VALOR` REAL);");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("DROP TABLE IF EXISTS VARIAVEIS");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("CREATE TABLE `VARIAVEIS` (`ID` INTEGER NOT NULL, `NOME` TEXT NOT NULL, PRIMARY KEY(`ID`));");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("INSERT INTO VARIAVEIS (NOME) VALUES ('MP10')");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("DROP TABLE IF EXISTS ESTACOES");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("CREATE TABLE `ESTACOES` (`ID` INTEGER NOT NULL, `NOME` TEXT NOT NULL, PRIMARY KEY(`ID`));");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("INSERT INTO ESTACOES (ID, NOME) VALUES (281, 'Limeira')");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("INSERT INTO ESTACOES (ID, NOME) VALUES (113, 'Piracicaba')");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("INSERT INTO ESTACOES (ID, NOME) VALUES (276, 'Campinas Taquaral')");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("DROP VIEW IF EXISTS ENTRADAS_TRATADAS_DIARIO");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("CREATE VIEW ENTRADAS_TRATADAS_DIARIO AS SELECT ID, ID_ESTACAO, ID_VARIAVEL, DATE(HORARIO) HORARIO, AVG(VALOR) VALOR FROM ENTRADAS_TRATADAS GROUP BY ID_ESTACAO, DATE(HORARIO)");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("DROP VIEW IF EXISTS ENTRADAS_TRATADAS_24");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("CREATE VIEW `ENTRADAS_TRATADAS_24` AS SELECT ((((strftime('%s', HORARIO) - strftime('%s', ULTIMA))/-3600)%24 - ((strftime('%s', HORARIO) - strftime('%s', ULTIMA))/-3600))/-24) JANELA, id, id_estacao, id_variavel, horario, AVG(VALOR) VALOR FROM (Select id, id_estacao, id_variavel, horario, (select horario from ENTRADAS_TRATADAS order by horario desc LIMIT 1) ULTIMA, valor From ENTRADAS_TRATADAS order by horario desc) GROUP BY JANELA");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("DROP TABLE IF EXISTS CONFIGURACOES");
		statement.close();
		
		statement = connection.createStatement();
		statement.executeUpdate("CREATE TABLE `CONFIGURACOES` (`ESTACAO` INTEGER, `inputWindowSize` INTEGER, `hiddenLayerNeurons` INTEGER, `validatingTimeWindow` TEXT, `trainingTimeWindow` TEXT);");
		statement.close();
		
		statement.close();
		connection.close();

	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		ModelInstall install = new ModelInstall();
	
		install.createDatabase();
		
	}

}