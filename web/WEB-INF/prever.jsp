<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.Statement"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="javax.activation.DataSource"%>
<%@ page import="javax.naming.Context"%>
<%@ page import="javax.naming.InitialContext"%>
<%@ page import="javax.naming.NamingException"%>
<% String name = "Prever"; %>
<%@ include file="header.jsp"%>
<script>
window.onload = function () {

var chart = new CanvasJS.Chart("chartContainer", {
	animationEnabled: true,
	theme: "light1",
	axisY:{
		includeZero: false
	},
	toolTip:{   
		content: "{y}"      
	},
	axisX:{
        interval: 24
	},
	data: [{        
		type: "line",
		lineColor: "black",
		markerColor: "black",
		dataPoints: [
			<%
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM (SELECT * FROM ENTRADAS_TRATADAS_24 WHERE ID_ESTACAO = 113 order by horario desc LIMIT 10) ORDER BY HORARIO ASC");

			rs.next();
			%>
			{ x: -216, y: <%=rs.getString("VALOR")%> },
			<%
			rs.next();
			%>
			{ x: -192, y: <%=rs.getString("VALOR")%> },
			<%
			rs.next();
			%>
			{ x: -168, y: <%=rs.getString("VALOR")%> },
			<%
			rs.next();
			%>
			{ x: -144, y: <%=rs.getString("VALOR")%> },
			<%
			rs.next();
			%>
			{ x: -120, y: <%=rs.getString("VALOR")%> },
			<%
			rs.next();
			%>
			{ x: -96, y: <%=rs.getString("VALOR")%> },
			<%
			rs.next();
			%>
			{ x: -72, y: <%=rs.getString("VALOR")%> },
			<%
			rs.next();
			%>
			{ x: -48, y: <%=rs.getString("VALOR")%> },
			<%
			rs.next();
			%>
			{ x: -24, y: <%=rs.getString("VALOR")%> },
			<%
			rs.next();
			%>
			{ x: 0, y: <%=rs.getString("VALOR")%> , lineColor: "red",},
			{ x: +24, y: <%=request.getAttribute("message")%> , markerColor: "red" }
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
			<h1><%=name%></h1>
			<%
				if (request.getAttribute("message") != null) {
			%>
			<p>Média para as proximas 24 horas: <%=request.getAttribute("message")%></p>
			<%
				}
			%>
			<div id="chartContainer" style="height: 370px; width: 100%;"></div>
			<script src="./js/canvasjs.min.js"></script>
		</main>
	</div>
</div>
<%@ include file="footer.jsp"%>