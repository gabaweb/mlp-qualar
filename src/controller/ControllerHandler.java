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

		try {
			
			int station = 113;
			String file = "113_Piracicaba_MP10";
												
			ModelHandler handler = new ModelHandler();
			handler.clear(file + ".csv");
			ArrayList<ArrayList<String>> data = handler.read("cleaned_" + file + ".csv");
			handler.save(data, station);
			handler.removeMissingData();
		
		} catch (SQLException | ClassNotFoundException | ParseException e) {
						
			request.setAttribute("message", e);
			
			getServletContext().getRequestDispatcher("/tratar.jsp").forward(request, response);
			
		}
		
        request.setAttribute("message", "Tratado com sucesso.");
		
		getServletContext().getRequestDispatcher("/WEB-INF/tratar.jsp").forward(request, response);
		
	}
	
}
