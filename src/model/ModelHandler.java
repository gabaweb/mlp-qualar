package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class ModelHandler {

	public void clear(String filePath) throws IOException {

		File inputFile = new File(filePath);
		File outputFile = new File(System.getenv("APP_MLP_QUALAR_HOME")+"cleaned_113_Piracicaba_MP10.csv");

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

		String line;

		for (int x = 0; (line = reader.readLine()) != null; x++) {
			if (line.contains(";01:00;")) {

				writer.write(line.replace(";01:00;", ";00:00;"));
				writer.newLine();

			} else if (line.contains(";02:00;")) {

				writer.write(line.replace(";02:00;", ";01:00;"));
				writer.newLine();

			} else if (line.contains(";03:00;")) {

				writer.write(line.replace(";03:00;", ";02:00;"));
				writer.newLine();

			} else if (line.contains(";04:00;")) {

				writer.write(line.replace(";04:00;", ";03:00;"));
				writer.newLine();

			} else if (line.contains(";05:00;")) {

				writer.write(line.replace(";05:00;", ";04:00;"));
				writer.newLine();

			} else if (line.contains(";06:00;")) {

				writer.write(line.replace(";06:00;", ";05:00;"));
				writer.newLine();

			} else if (line.contains(";07:00;")) {

				writer.write(line.replace(";07:00;", ";06:00;"));
				writer.newLine();

			} else if (line.contains(";08:00;")) {

				writer.write(line.replace(";08:00;", ";07:00;"));
				writer.newLine();

			} else if (line.contains(";09:00;")) {

				writer.write(line.replace(";09:00;", ";08:00;"));
				writer.newLine();

			} else if (line.contains(";10:00;")) {

				writer.write(line.replace(";10:00;", ";09:00;"));
				writer.newLine();

			} else if (line.contains(";11:00;")) {

				writer.write(line.replace(";11:00;", ";10:00;"));
				writer.newLine();

			} else if (line.contains(";12:00;")) {

				writer.write(line.replace(";12:00;", ";11:00;"));
				writer.newLine();

			} else if (line.contains(";13:00;")) {

				writer.write(line.replace(";13:00;", ";12:00;"));
				writer.newLine();

			} else if (line.contains(";14:00;")) {

				writer.write(line.replace(";14:00;", ";13:00;"));
				writer.newLine();

			} else if (line.contains(";15:00;")) {

				writer.write(line.replace(";15:00;", ";14:00;"));
				writer.newLine();

			} else if (line.contains(";16:00;")) {

				writer.write(line.replace(";16:00;", ";15:00;"));
				writer.newLine();

			} else if (line.contains(";17:00;")) {

				writer.write(line.replace(";17:00;", ";16:00;"));
				writer.newLine();

			} else if (line.contains(";18:00;")) {

				writer.write(line.replace(";18:00;", ";17:00;"));
				writer.newLine();

			} else if (line.contains(";19:00;")) {

				writer.write(line.replace(";19:00;", ";18:00;"));
				writer.newLine();

			} else if (line.contains(";20:00;")) {

				writer.write(line.replace(";20:00;", ";19:00;"));
				writer.newLine();

			} else if (line.contains(";21:00;")) {

				writer.write(line.replace(";21:00;", ";20:00;"));
				writer.newLine();

			} else if (line.contains(";22:00;")) {

				writer.write(line.replace(";22:00;", ";21:00;"));
				writer.newLine();

			} else if (line.contains(";23:00;")) {

				writer.write(line.replace(";23:00;", ";22:00;"));
				writer.newLine();

			} else if (line.contains(";24:00;")) {

				writer.write(line.replace(";24:00;", ";23:00;"));
				writer.newLine();

			} else if (x > 7) {

				writer.write(line);
				writer.newLine();

			}

		}

		reader.close();
		writer.close();

	}

	public ArrayList<ArrayList<String>> read(String filePath) throws IOException {

		ArrayList<ArrayList<String>> data = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(filePath));

		for (String linha = br.readLine(); linha != null; linha = br.readLine()) {

			String[] colunas = linha.split(";");
			ArrayList<String> colunasList = new ArrayList<>(Arrays.asList(colunas));
			data.add(colunasList);
		}

		br.close();
		
		for (int x = data.size()-1; data.get(x).size() != 3; x--) {
			
			data.remove(x);
		
		}

		return data;

	}

	public void save(ArrayList<ArrayList<String>> data, int station) throws SQLException, ParseException {

		Connection connection = DriverManager.getConnection("jdbc:sqlite:"+System.getenv("APP_MLP_QUALAR_HOME")+"database.db");
		connection.setAutoCommit(false);

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("select horario from ENTRADAS WHERE ID_ESTACAO = " + station + " order by horario desc LIMIT 1;");
		LocalDateTime lastDateTime;
		if (rs.isClosed()) {
			lastDateTime = null;
		}else {
			lastDateTime = LocalDateTime.parse(rs.getString("horario"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		}
		rs.close();
		stmt.close();

		PreparedStatement pstmt = connection.prepareStatement("INSERT INTO ENTRADAS (ID_ESTACAO, ID_VARIAVEL, HORARIO, VALOR) VALUES(?, ?, ?, ?)");

		DateFormat csvFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		DateFormat sqliteFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		LocalDateTime csvDateTime;

		for (ArrayList<String> row : data) {

			csvDateTime = LocalDateTime.parse(row.get(0) + " " + row.get(1), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

			if (lastDateTime == null || csvDateTime.isAfter(lastDateTime)) {

				pstmt.setInt(1, station);
				pstmt.setInt(2, 1);

				pstmt.setString(3, sqliteFormat.format(csvFormat.parse(row.get(0) + " " + row.get(1))));

				if (row.size() == 3) {

					pstmt.setString(4, row.get(2));

				} else {

					pstmt.setString(4, null);

				}

				pstmt.addBatch();

			}

		}

		pstmt.executeBatch();

		connection.commit();
		
		pstmt.close();

		connection.close();

	}

	public void removeMissingData() throws SQLException, ClassNotFoundException {

		Connection connection = DriverManager.getConnection("jdbc:sqlite:"+System.getenv("APP_MLP_QUALAR_HOME")+"database.db");

		connection.setAutoCommit(false);
		
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("DELETE FROM ENTRADAS_TRATADAS");

		PreparedStatement pstmt = connection.prepareStatement("INSERT INTO ENTRADAS_TRATADAS (ID_ESTACAO, ID_VARIAVEL, HORARIO, VALOR) VALUES(?, ?, ?, ?)");

		stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM ENTRADAS");

		String lastNotNull = null;

		rs.next();

		String x = rs.getString("VALOR");

		while (x == null) {
			rs.next();
			x = rs.getString("VALOR");
		}

		lastNotNull = rs.getString("VALOR");

		rs.close();

		rs = stmt.executeQuery("SELECT * FROM ENTRADAS");

		while (rs.next()) {

			x = rs.getString("VALOR");

			if (x == null) {

				pstmt.setInt(1, rs.getInt("ID_ESTACAO"));
				pstmt.setInt(2, rs.getInt("ID_VARIAVEL"));
				pstmt.setString(3, rs.getString("HORARIO"));
				pstmt.setString(4, lastNotNull);

			} else {

				lastNotNull = rs.getString("VALOR");
				pstmt.setInt(1, rs.getInt("ID_ESTACAO"));
				pstmt.setInt(2, rs.getInt("ID_VARIAVEL"));
				pstmt.setString(3, rs.getString("HORARIO"));
				pstmt.setString(4, rs.getString("VALOR"));

			}

			pstmt.addBatch();

		}

		pstmt.executeBatch();

		connection.commit();

		connection.close();

	}

}
