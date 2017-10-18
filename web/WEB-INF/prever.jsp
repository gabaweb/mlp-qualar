<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<% String name = "Treinar e Prever"; %>
<%@ include file="header.jsp"%>
			<h1><%=name%></h1>
			<form action="./" method="post">
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
				<button type="submit" class="btn btn-primary">Treinar e
					Prever</button>
			</form>
			<%
				if (request.getAttribute("message") != null) {
			%>
			<h2>Resultado</h2>
			<p>
				Média para as proximas 24 horas: <%=request.getAttribute("message")%></p>
			<%
				}
			%>
		</main>
	</div>
</div>
<%@ include file="footer.jsp"%>