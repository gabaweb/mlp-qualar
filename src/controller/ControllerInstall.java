package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Install;

@WebServlet("/install")
public class ControllerInstall extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ControllerInstall() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			
			Class.forName("org.sqlite.JDBC");

			Install install = new Install();
			
			install.createDatabase();
			
		} catch (ClassNotFoundException | SQLException e) {
			throw new ServletException(e);
		}
		
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
		
		out.print("Instalado!");
		out.print("<br><br>");
		out.print("<a href='./'>Voltar</a>");
		
	}


}
