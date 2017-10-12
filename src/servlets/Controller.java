package servlets;

import java.io.IOException;
import java.io.PrintWriter;
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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import tcc.Extractor;
import tcc.Handler;
import tcc.MLP;

@WebServlet("/")
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Controller() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			
			int station = 113;
			String file = "113_Piracicaba_MP10";

			Extractor extractor = new Extractor();
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
												
//			Handler handler = new Handler();
//			handler.clear(file + ".csv");
//			ArrayList<ArrayList<String>> data = handler.read("cleaned_" + file + ".csv");
//			handler.save(data, station);
//			handler.removeMissingData();
//						
//			boolean useOutputVariableToPredict = true;
//	        int numOfVariables = 1;
//	        int inputWindowSize = 4;
//	        int hiddenLayerNeurons = 22;
//	        int predictWindowSize = 1;
//	        
//	        ArrayList<NormalizedField> normalizations = new ArrayList<>();
//	        
//	        normalizations.add(new NormalizedField(NormalizationAction.Normalize, "MP", 100, 0, 1, 0));
//	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "TEMP", 50, 0, 1, 0));
//	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "UR", 100, 0, 1, 0));
//	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "VV", 10, 0, 1, 0));
//
//	        double prediction = new MLP(useOutputVariableToPredict, numOfVariables, inputWindowSize, hiddenLayerNeurons, predictWindowSize, normalizations, station).execute();
//	        
//	        System.out.println(prediction);
//	        
//	        response.setContentType("text/html");
//	        PrintWriter out = response.getWriter();
//			
//			out.print("Previsão: "+ prediction);

		
		} catch (SQLException | ClassNotFoundException e) {
			throw new ServletException(e);
		}
		
	}
	
}
