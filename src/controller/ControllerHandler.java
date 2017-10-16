package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tcc.Handler;

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
												
			Handler handler = new Handler();
			handler.clear(file + ".csv");
			ArrayList<ArrayList<String>> data = handler.read("cleaned_" + file + ".csv");
			handler.save(data, station);
			handler.removeMissingData();

		
		} catch (SQLException | ClassNotFoundException | ParseException e) {
			throw new ServletException(e);
		}
		
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
		
		out.print("Tratado!");
		out.print("<br><br>");
		out.print("<a href='./'>Voltar</a>");
		
	}
	
}
