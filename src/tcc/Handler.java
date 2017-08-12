package tcc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class Handler {

	private void clear(String filePath) throws IOException {

		File inputFile = new File(filePath);
		File outputFile = new File("cleaned_" + filePath);

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

		String line;

		for (int x = 0; (line = reader.readLine()) != null; x++) {
			if (line.contains(";24:00;")) {

				writer.write(line.replace(";24:00;", ";00:00;"));
				writer.newLine();

			} else if (x > 7) {

				writer.write(line);
				writer.newLine();

			}

		}

		reader.close();
		writer.close();

	}

	private ArrayList<ArrayList<String>> read(String filePath) throws IOException {

		ArrayList<ArrayList<String>> data = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(filePath));

		for (String linha = br.readLine(); linha != null; linha = br.readLine()) {

			String[] colunas = linha.split(";");
			ArrayList<String> colunasList = new ArrayList<>(Arrays.asList(colunas));
			data.add(colunasList);
		}

		br.close();

		return data;

	}

	private void save(ArrayList<ArrayList<String>> data) throws SQLException {

		Connection connection = DriverManager.getConnection("jdbc:sqlite:sample.db");

		for (ArrayList<String> row : data) {

			PreparedStatement pstmt = connection.prepareStatement("INSERT INTO ENTRADAS(ID_VARIAVEL, VALOR, HORARIO) VALUES(?, ?, ?)");
		    pstmt.setInt(1, 1);
		    //pstmt.setString(row.get(1), 2);
			pstmt.executeUpdate();

		}

	}

	public static void main(String[] args) throws IOException {

		Handler handler = new Handler();

		handler.clear("281_12_01-01-2015_31-12-2016.csv");

		ArrayList<ArrayList<String>> data = handler.read("281_12_01-01-2015_31-12-2016.csv");

	}

}
