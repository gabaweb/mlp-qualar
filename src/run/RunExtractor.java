package run;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;

import tcc.Extractor;

public class RunExtractor {
	
	public static void main(String[] args) throws ClientProtocolException, IOException, SQLException {

		int station = 113;
		String file = "113_Piracicaba_MP10";

		Extractor extractor = new Extractor();
		extractor.login();

		Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
		connection.setAutoCommit(false);
		
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT DATE(HORARIO) HORARIO FROM ENTRADAS WHERE ID_ESTACAO = " + station + " ORDER BY HORARIO DESC LIMIT 1;");
		String lastdate = rs.getString("HORARIO");
		LocalDate datetime = LocalDate.parse(lastdate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		lastdate = datetime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();

		extractor.saveFile(extractor.extract(station, "12", lastdate, dateFormat.format(date)), file + ".csv");

	}

}
