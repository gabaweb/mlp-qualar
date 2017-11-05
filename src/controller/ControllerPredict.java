package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.encog.Encog;
import org.encog.ml.MLRegression;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import model.MLP;

@WebServlet("")
public class ControllerPredict extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ControllerPredict() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
			connection.setAutoCommit(false);

			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM CONFIGURACOES");
			
			rs.next();
			
			int station = 113;
			boolean useOutputVariableToPredict = true;
	        int numOfVariables = 1;
	        int inputWindowSize = Integer.parseInt(rs.getString("inputWindowSize"));
	        int hiddenLayerNeurons = Integer.parseInt(rs.getString("hiddenLayerNeurons"));
	        int predictWindowSize = 1;
	    	String validatingTimeWindow = rs.getString("validatingTimeWindow");
	    	String trainingTimeWindow = rs.getString("trainingTimeWindow");
	    	
			rs.close();
			stmt.close();
			connection.close();
	        
	        ArrayList<NormalizedField> normalizations = new ArrayList<>();
	        
	        normalizations.add(new NormalizedField(NormalizationAction.Normalize, "MP", 100, 0, 1, 0));
	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "TEMP", 50, 0, 1, 0));
	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "UR", 100, 0, 1, 0));
	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "VV", 10, 0, 1, 0));

	        MLP mlp = new MLP(useOutputVariableToPredict, numOfVariables, inputWindowSize, hiddenLayerNeurons, predictWindowSize, normalizations, station, validatingTimeWindow, trainingTimeWindow);
	        
			MLRegression model = mlp.loadModel();
			
			double prediction = mlp.predict(model);
			
			Encog.getInstance().shutdown();
	        
	        System.out.println(prediction);
			
			request.setAttribute("message", prediction);
			
			getServletContext().getRequestDispatcher("/WEB-INF/prever.jsp").forward(request, response);
		
		} catch (SQLException | ClassNotFoundException e) {
			throw new ServletException(e);
		}
		
	}
	
}
