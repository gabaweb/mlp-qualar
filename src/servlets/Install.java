package servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tcc.Database;

@WebServlet("/install")
public class Install extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Install() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			Class.forName("org.sqlite.JDBC");

			Database database = new Database();
			
			database.create();
		} catch (ClassNotFoundException | SQLException e) {
			throw new ServletException(e);
		}
		
	}


}
