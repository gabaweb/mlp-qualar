<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<% String name = "Treinar"; %>
<%@ include file="header.jsp"%>
			<h1><%=name%></h1>
			<%
				if (request.getAttribute("message") != null) {
			%>
			<div class="alert alert-primary" role="alert"><%=request.getAttribute("message")%></div>
			<%
				}
			%>
			<form action="./treinar" method="post">
				<div class="form-group">
					<label for="validatingTimeWindow">Janela Validação</label>
					<input
						type="text" class="form-control" id="validatingTimeWindow"
						name="validatingTimeWindow" value="-6 months">
				</div>
				<div class="form-group">
					<label for="trainingTimeWindow">Janela Treinamento</label>
					<input
						type="text" class="form-control" id="trainingTimeWindow"
						name="trainingTimeWindow" value="-6 months">
				</div>
				<div class="form-group">
					<label for="trainingTimeWindow">Janela de Entrada</label>
					<input
						type="text" class="form-control" id="inputWindowSize"
						name="inputWindowSize" value="4">
				</div>
				<div class="form-group">
					<label for="trainingTimeWindow">Número de Neurônios na Camada Oculta</label>
					<input type="text" class="form-control"
						id="hiddenLayerNeurons" name="hiddenLayerNeurons" value="22">
				</div>
				<button type="submit" class="btn btn-primary">Treinar</button>
			</form>
		</main>
	</div>
</div>
<%@ include file="footer.jsp"%>