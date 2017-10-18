package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import tcc.MLP;

@WebServlet("")
public class ControllerMLP extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ControllerMLP() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/prever.jsp").forward(request, response);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			
			int station = 113;
			
			boolean useOutputVariableToPredict = true;
	        int numOfVariables = 1;
	        int inputWindowSize = Integer.parseInt(request.getParameter("inputWindowSize"));
	        int hiddenLayerNeurons = Integer.parseInt(request.getParameter("hiddenLayerNeurons"));
	        int predictWindowSize = 1;
	    	String validatingTimeWindow = request.getParameter("validatingTimeWindow");
	    	String trainingTimeWindow = request.getParameter("trainingTimeWindow");
	        
	        
	        ArrayList<NormalizedField> normalizations = new ArrayList<>();
	        
	        normalizations.add(new NormalizedField(NormalizationAction.Normalize, "MP", 100, 0, 1, 0));
	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "TEMP", 50, 0, 1, 0));
	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "UR", 100, 0, 1, 0));
	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "VV", 10, 0, 1, 0));

	        double prediction = new MLP(useOutputVariableToPredict, numOfVariables, inputWindowSize, hiddenLayerNeurons, predictWindowSize, normalizations, station, validatingTimeWindow, trainingTimeWindow).execute();
	        
	        System.out.println(prediction);
			
			request.setAttribute("message", prediction);
			
			getServletContext().getRequestDispatcher("/WEB-INF/prever.jsp").forward(request, response);
		
		} catch (SQLException | ClassNotFoundException e) {
			throw new ServletException(e);
		}
		
	}
	
}
