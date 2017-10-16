package controller;

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/prever")
public class ControllerMLP extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ControllerMLP() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			
			int station = 113;
			
			boolean useOutputVariableToPredict = true;
	        int numOfVariables = 1;
	        int inputWindowSize = 4;
	        int hiddenLayerNeurons = 22;
	        int predictWindowSize = 1;
	        
	        ArrayList<NormalizedField> normalizations = new ArrayList<>();
	        
	        normalizations.add(new NormalizedField(NormalizationAction.Normalize, "MP", 100, 0, 1, 0));
	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "TEMP", 50, 0, 1, 0));
	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "UR", 100, 0, 1, 0));
	        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "VV", 10, 0, 1, 0));

	        double prediction = new MLP(useOutputVariableToPredict, numOfVariables, inputWindowSize, hiddenLayerNeurons, predictWindowSize, normalizations, station).execute();
	        
	        System.out.println(prediction);
	        
	        response.setContentType("text/html");
	        PrintWriter out = response.getWriter();
			
			out.print("Média para as proximas 24 horas: "+ prediction);
			out.print("<br><br>");
			out.print("<a href='./'>Voltar</a>");

		
		} catch (SQLException | ClassNotFoundException e) {
			throw new ServletException(e);
		}
		
	}
	
}
