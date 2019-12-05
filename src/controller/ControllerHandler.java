package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.ModelHandler;

@WebServlet("/tratar")
public class ControllerHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ControllerHandler() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Boolean exception = false;

		try {
			
			Class.forName("org.sqlite.JDBC");
			
			int station = 113;
			String file = System.getenv("APP_MLP_QUALAR_HOME")+"113_Piracicaba_MP10";
												
			ModelHandler handler = new ModelHandler();
			handler.clear(file + ".csv");
			ArrayList<ArrayList<String>> data = handler.read(System.getenv("APP_MLP_QUALAR_HOME")+"cleaned_113_Piracicaba_MP10.csv");
			handler.save(data, station);
			handler.removeMissingData();
		
		} catch (SQLException | ClassNotFoundException | ParseException e) {
			exception = true;
			System.out.println("message: " + e);
						
		}
		
		if(!exception){
		
	        request.setAttribute("message", "Tratado com sucesso.");
			
			getServletContext().getRequestDispatcher("/WEB-INF/tratar.jsp").forward(request, response);
		
		}
		
	}
	
}
