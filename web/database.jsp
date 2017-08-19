<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.Statement"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="javax.activation.DataSource"%>
<%@ page import="javax.naming.Context"%>
<%@ page import="javax.naming.InitialContext"%>
<%@ page import="javax.naming.NamingException"%>
<table>
	<tr>
		<td>ID</td>
		<td>ID_VARIAVEL</td>
		<td>HORARIO</td>
		<td>VALOR</td>
	</tr>
	<%
		Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection("jdbc:sqlite:/sample.db");
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM ENTRADAS_TRATADAS");

		while (rs.next()) {
	%>
	<tr>
		<td>
			<%=rs.getInt("ID")%>
		</td>
		<td>
			<%=rs.getInt("ID_VARIAVEL")%>
		</td>
		<td>
			<%=rs.getString("HORARIO")%>
		</td>
		<td>
			<%=rs.getString("VALOR")%>
		</td>
	</tr>
	<%
		}
	%>
</table>
<%
	rs.close();
	stmt.close();
	connection.close();
%>