package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Install {

	public void createDatabase() throws ClassNotFoundException, SQLException {

		Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
		Statement statement = connection.createStatement();

		statement.executeUpdate("DROP TABLE IF EXISTS ENTRADAS");
		statement.executeUpdate("CREATE TABLE `ENTRADAS` (`ID` INTEGER NOT NULL, `ID_ESTACAO` INTEGER NOT NULL, `ID_VARIAVEL` INTEGER NOT NULL, `HORARIO` TEXT NOT NULL, `VALOR` REAL, PRIMARY KEY(`ID`, `ID_ESTACAO`, `ID_VARIAVEL`, `HORARIO`));");
		
		statement.executeUpdate("DROP TABLE IF EXISTS ENTRADAS_TRATADAS");
		statement.executeUpdate("CREATE TABLE `ENTRADAS_TRATADAS` (`ID` INTEGER NOT NULL, `ID_ESTACAO` INTEGER NOT NULL, `ID_VARIAVEL` INTEGER NOT NULL, `HORARIO` TEXT NOT NULL, `VALOR` REAL, PRIMARY KEY(`ID`, `ID_ESTACAO`, `ID_VARIAVEL`, `HORARIO`));");
		
		statement.executeUpdate("DROP TABLE IF EXISTS VARIAVEIS");
		statement.executeUpdate("CREATE TABLE `VARIAVEIS` (`ID` INTEGER NOT NULL, `NOME` TEXT NOT NULL, PRIMARY KEY(`ID`));");
		statement.executeUpdate("INSERT INTO VARIAVEIS (NOME) VALUES ('MP10')");
		
		statement.executeUpdate("DROP TABLE IF EXISTS ESTACOES");
		statement.executeUpdate("CREATE TABLE `ESTACOES` (`ID` INTEGER NOT NULL, `NOME` TEXT NOT NULL, PRIMARY KEY(`ID`));");
		
		statement.executeUpdate("INSERT INTO ESTACOES (ID, NOME) VALUES (281, 'Limeira')");
		statement.executeUpdate("INSERT INTO ESTACOES (ID, NOME) VALUES (113, 'Piracicaba')");
		statement.executeUpdate("INSERT INTO ESTACOES (ID, NOME) VALUES (276, 'Campinas Taquaral')");
		
		statement.executeUpdate("DROP VIEW IF EXISTS ENTRADAS_TRATADAS_DIARIO");
		statement.executeUpdate("CREATE VIEW ENTRADAS_TRATADAS_DIARIO AS SELECT ID, ID_ESTACAO, ID_VARIAVEL, DATE(HORARIO) HORARIO, AVG(VALOR) VALOR FROM ENTRADAS_TRATADAS GROUP BY ID_ESTACAO, DATE(HORARIO)");

		statement.executeUpdate("DROP VIEW IF EXISTS ENTRADAS_TRATADAS_24");
		statement.executeUpdate("CREATE VIEW `ENTRADAS_TRATADAS_24` AS SELECT ((((strftime('%s', HORARIO) - strftime('%s', ULTIMA))/-3600)%24 - ((strftime('%s', HORARIO) - strftime('%s', ULTIMA))/-3600))/-24) JANELA, id, id_estacao, id_variavel, horario, AVG(VALOR) VALOR FROM (Select id, id_estacao, id_variavel, horario, (select horario from ENTRADAS_TRATADAS order by horario desc LIMIT 1) ULTIMA, valor From ENTRADAS_TRATADAS order by horario desc) GROUP BY JANELA");
		
		statement.executeUpdate("DROP TABLE IF EXISTS CONFIGURACOES");
		statement.executeUpdate("CREATE TABLE `CONFIGURACOES` (`ESTACAO` INTEGER, `inputWindowSize` INTEGER, `hiddenLayerNeurons` INTEGER, `validatingTimeWindow` TEXT, `trainingTimeWindow` TEXT);");

		statement.close();
		connection.close();

	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		Install install = new Install();
	
		install.createDatabase();
		
	}

}