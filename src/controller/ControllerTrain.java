package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import model.ModelMLP;

@WebServlet("/treinar")
public class ControllerTrain extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ControllerTrain() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/treinar.jsp").forward(request, response);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			
			int station = 113;
			
			boolean useOutputVariableToPredict = true;
	        int numOfVariables = 1;
	        int inputWindowSize = Integer.parseInt(request.getParameter("inputWindowSize"));
	        int hiddenLayerNeurons = Integer.parseInt(request.getParameter("hiddenLayerNeurons"));
	        int predictWindowSize = 1;
	    	String validatingTimeWindow = "-" + request.getParameter("validatingTimeWindow") + " months";
	    	String trainingTimeWindow = "-" + request.getParameter("trainingTimeWindow") + " months";
			
	    	Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
			connection.setAutoCommit(false);
			
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("DELETE FROM CONFIGURACOES");
			stmt.close();

			PreparedStatement pstmt = connection.prepareStatement("INSERT INTO CONFIGURACOES (inputWindowSize, hiddenLayerNeurons, validatingTimeWindow, trainingTimeWindow) VALUES(?, ?, ?, ?)");

			pstmt.setInt(1, Integer.parseInt(request.getParameter("inputWindowSize")));
			pstmt.setInt(2, Integer.parseInt(request.getParameter("hiddenLayerNeurons")));
			pstmt.setString(3, request.getParameter("validatingTimeWindow"));
			pstmt.setString(4, request.getParameter("trainingTimeWindow"));
			
			pstmt.addBatch();
			pstmt.executeBatch();
			pstmt.close();
			connection.commit();
			connection.close();
	        
	        ArrayList<NormalizedField> normalizations = new ArrayList<>();
	        
	        normalizations.add(new NormalizedField(NormalizationAction.Normalize, "MP", 100, 0, 1, 0));
	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "TEMP", 50, 0, 1, 0));
	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "UR", 100, 0, 1, 0));
	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "VV", 10, 0, 1, 0));

	        ModelMLP mlp = new ModelMLP(useOutputVariableToPredict, numOfVariables, inputWindowSize, hiddenLayerNeurons, predictWindowSize, normalizations, station, validatingTimeWindow, trainingTimeWindow);
	        
	        TemporalMLDataSet trainingData = mlp.createTrainingDataSet();
			TemporalMLDataSet validadingData = mlp.createValidadingDataSet();

			mlp.trainAndSaveModel(trainingData, validadingData, MLMethodFactory.TYPE_FEEDFORWARD, "?:B->SIGMOID->" + hiddenLayerNeurons + ":B->SIGMOID->?" , MLTrainFactory.TYPE_RPROP, "");
			
			request.setAttribute("message", "Treinado com sucesso.");
			
			getServletContext().getRequestDispatcher("/WEB-INF/treinar.jsp").forward(request, response);
			
		
		} catch (SQLException | ClassNotFoundException e) {
			throw new ServletException(e);
		}
		
	}
	
}
