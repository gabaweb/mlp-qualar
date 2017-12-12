package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import model.ModelExtractor;
import model.ModelHandler;

public class ControllerListener implements Runnable {

	@Override
	public void run() {

		int station = 113;
		String file = "113_Piracicaba_MP10";

		ModelExtractor extractor = new ModelExtractor();
		try {
			extractor.login();
		
		Class.forName("org.sqlite.JDBC");

		Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
		connection.setAutoCommit(false);
		ResultSet rs = connection.createStatement().executeQuery("SELECT DATE(HORARIO) HORARIO FROM ENTRADAS WHERE ID_ESTACAO = " + station + " ORDER BY HORARIO DESC LIMIT 1;");
		String lastdate = rs.getString("HORARIO");
		rs.close();
		connection.close();
		
		LocalDate datetime = LocalDate.parse(lastdate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		lastdate = datetime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
					
		extractor.saveFile(extractor.extract(station, "12", lastdate, dateFormat.format(date)), file + ".csv");
											
		ModelHandler handler = new ModelHandler();
		handler.clear(file + ".csv");
		ArrayList<ArrayList<String>> data = handler.read("cleaned_" + file + ".csv");
		handler.save(data, station);
		handler.removeMissingData();
		
		} catch (IOException | ClassNotFoundException | SQLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Job executado...");
		
	}
}