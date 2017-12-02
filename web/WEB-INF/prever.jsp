<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.Statement"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="javax.activation.DataSource"%>
<%@ page import="javax.naming.Context"%>
<%@ page import="javax.naming.InitialContext"%>
<%@ page import="javax.naming.NamingException"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.DecimalFormatSymbols"%>
<%@ page import="java.util.Locale"%>
<% String name = "Previsão em tempo real"; %>
<%@ include file="header.jsp"%>
<script>
window.onload = function () {

var chart = new CanvasJS.Chart("chartContainer", {
	animationEnabled: true,
	theme: "light1",
	axisY:{
		title: "Média da concentração de MP10 em 24 horas (µg/m3)",
		includeZero: false
	},
	toolTip:{   
		content: "{y}"      
	},
	axisX:{
		title: "Tempo (horas)",
        interval: 24
	},
	data: [{        
		type: "line",
		lineColor: "black",
		markerColor: "black",
		dataPoints: [
			<%
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
			otherSymbols.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("#.00", otherSymbols);

			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM (SELECT * FROM ENTRADAS_TRATADAS_24 WHERE ID_ESTACAO = 113 order by horario desc LIMIT 10) ORDER BY HORARIO ASC");

			rs.next();
			%>
			{ x: -216, y: <%=df.format(rs.getDouble("VALOR"))%> },
			<%
			rs.next();
			%>
			{ x: -192, y: <%=df.format(rs.getDouble("VALOR"))%> },
			<%
			rs.next();
			%>
			{ x: -168, y: <%=df.format(rs.getDouble("VALOR"))%> },
			<%
			rs.next();
			%>
			{ x: -144, y: <%=df.format(rs.getDouble("VALOR"))%> },
			<%
			rs.next();
			%>
			{ x: -120, y: <%=df.format(rs.getDouble("VALOR"))%> },
			<%
			rs.next();
			%>
			{ x: -96, y: <%=df.format(rs.getDouble("VALOR"))%> },
			<%
			rs.next();
			%>
			{ x: -72, y: <%=df.format(rs.getDouble("VALOR"))%> },
			<%
			rs.next();
			%>
			{ x: -48, y: <%=df.format(rs.getDouble("VALOR"))%> },
			<%
			rs.next();
			%>
			{ x: -24, y: <%=df.format(rs.getDouble("VALOR"))%> },
			<%
			rs.next();
			%>
			{ x: 0, y: <%=df.format(rs.getFloat("VALOR"))%> , lineColor: "blue",},
			{ x: +24, y: <%=df.format(request.getAttribute("message"))%> , markerColor: "blue" }
			<%
rs.close();
stmt.close();
connection.close();
%>
		]
	}]
});
chart.render();

}
</script>
<%
df = new DecimalFormat("#.00");

connection = DriverManager.getConnection("jdbc:sqlite:database.db");
stmt = connection.createStatement();
rs = stmt.executeQuery("SELECT HORARIO FROM ENTRADAS_TRATADAS WHERE ID_ESTACAO = 113 order by horario DESC LIMIT 1");
%>
			<h1><%=name%></h1>
			<%
				if (request.getAttribute("message") != null) {
					
					Double previsao = (Double) request.getAttribute("message");

					if (previsao <= 50) {
				%>
				<div class="alert alert-success" role="alert">Qualidade prevista: Boa</div>
				<%
					}
					else if (previsao > 50 && previsao <= 150) {
				%>
				<div class="alert alert-warning" role="alert">Qualidade prevista: Regular</div>
				<%
					}
					else if (previsao > 150 && previsao <= 250) {
				%>
				<div class="alert alert-warning" role="alert">Qualidade prevista: Inadequada</div>
				<%
					}
					else if (previsao > 250 && previsao <= 420) {
				%>
				<div class="alert alert-warning" role="alert">Qualidade prevista: Má</div>
				<%
					}
					else if (previsao > 420) {
				%>
				<div class="alert alert-danger" role="alert">Qualidade prevista: Péssima</div>
				<%
					}
			%>
			<p><strong>Última atualização:</strong> <%=rs.getString("HORARIO")%></p>
			<p><strong>Média de concentração de MP10 prevista para as próximas 24 horas:</strong> <%=df.format(previsao)%> (µg/m3)</p>
			<%
				}
			
			rs.close();
			stmt.close();
			connection.close();
			%>
			<div id="chartContainer" style="height: 370px; width: 100%;"></div>
			<script src="./js/canvasjs.min.js"></script>
		</main>
	</div>
</div>
<%@ include file="footer.jsp"%>