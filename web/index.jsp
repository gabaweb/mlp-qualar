<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.Statement"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="javax.activation.DataSource"%>
<%@ page import="javax.naming.Context"%>
<%@ page import="javax.naming.InitialContext"%>
<%@ page import="javax.naming.NamingException"%>
<%@ include file="header.jsp"%>
<h1>Entradas</h1>
<h2>Médias a cada 24 horas</h2>
<div class="table-responsive">
	<table class="table table-striped">
		<thead>
			<tr>
				<th style="width:33%">Janela</th>
				<th style="width:34%">Valor</th>
			</tr>
		</thead>
		<tbody>
			<%
				Class.forName("org.sqlite.JDBC");
				Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM ENTRADAS_TRATADAS_24 WHERE ID_ESTACAO = 113 order by horario desc LIMIT 4");

				while (rs.next()) {
			%>
			<tr>
				<td><%=rs.getInt("JANELA")+1%></td>
				<td><%=rs.getString("VALOR")%></td>
			</tr>
			<%
				}
			%>
		<tbody>
	</table>
	<%
		rs.close();
		stmt.close();
		connection.close();
	%>
<h2>Dados horários tratados de 4 janelas</h2>
	<table class="table table-striped">
		<thead>
			<tr>
				<th style="width:50%">Horário</th>
				<th style="width:50%">Valor</th>
			</tr>
		</thead>
		<tbody>
			<%
				connection = DriverManager.getConnection("jdbc:sqlite:database.db");
				
				stmt = connection.createStatement();
				rs = stmt.executeQuery("SELECT * FROM ENTRADAS_TRATADAS WHERE ID_ESTACAO = 113 order by horario desc LIMIT 96");

				while (rs.next()) {
			%>
			<tr>
				<td><%=rs.getString("HORARIO")%></td>
				<td><%=rs.getString("VALOR")%></td>
			</tr>
			<%
				}
			%>
		<tbody>
	</table>
	<%
		rs.close();
		stmt.close();
		connection.close();
	%>

</div>
<%@ include file="footer.jsp"%>