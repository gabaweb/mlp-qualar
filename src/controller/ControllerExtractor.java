package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.ModelExtractor;

@WebServlet("/extrair")
public class ControllerExtractor extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ControllerExtractor() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			
			int station = 113;
			String file = System.getenv("APP_MLP_QUALAR_HOME")+"113_Piracicaba_MP10";

			ModelExtractor extractor = new ModelExtractor();
			extractor.login();
			
			Class.forName("org.sqlite.JDBC");

			Connection connection = DriverManager.getConnection("jdbc:sqlite:"+System.getenv("APP_MLP_QUALAR_HOME")+"database.db");
			connection.setAutoCommit(false);
			ResultSet rs = connection.createStatement().executeQuery("SELECT DATE(HORARIO) HORARIO FROM ENTRADAS WHERE ID_ESTACAO = " + station + " ORDER BY HORARIO DESC LIMIT 1;");
			String lastdate;
			if (rs.isClosed()) {
				lastdate = "2018-12-04";
			}else {
				lastdate = rs.getString("HORARIO");
			}
			rs.close();
			connection.close();
			
			LocalDate datetime = LocalDate.parse(lastdate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			lastdate = datetime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date date = new Date();
						
			extractor.saveFile(extractor.extract(station, "12", lastdate, dateFormat.format(date)), file + ".csv");
		
		} catch (SQLException | ClassNotFoundException e) {
			throw new ServletException(e);
		}
        
        request.setAttribute("message", "Extraido com sucesso.");
		
		getServletContext().getRequestDispatcher("/WEB-INF/extrair.jsp").forward(request, response);
		
	}
	
}
