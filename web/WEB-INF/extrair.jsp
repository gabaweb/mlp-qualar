<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<% 	String name = "Extrair"; %>
<%@ include file="header.jsp"%>
			<%
				if (request.getAttribute("message") != null) {
			%>
			<h1><%=name%></h1>
			<p><%=request.getAttribute("message")%></p>
			<%
				}
			%>
		</main>
	</div>
</div>
<%@ include file="footer.jsp"%>