<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%! public String name = "Treinamento da rede neural"; %>
<%@ include file="header.jsp"%>
			<h1><%=name%></h1>
			<%
				if (request.getAttribute("message") != null) {
			%>
			<div class="alert alert-secondary" role="alert"><%=request.getAttribute("message")%></div>
			<%
				}
			%>
			<form action="./treinar" method="post">
				<div class="form-group">
					<label for="validatingTimeWindow">Janela de Validação (em meses)</label>
					<input
						type="text" class="form-control" id="validatingTimeWindow"
						name="validatingTimeWindow" value="6">
				</div>
				<div class="form-group">
					<label for="trainingTimeWindow">Janela de Treinamento (em meses)</label>
					<input
						type="text" class="form-control" id="trainingTimeWindow"
						name="trainingTimeWindow" value="6">
				</div>
				<div class="form-group">
					<label for="trainingTimeWindow">Quantidade de Dados de Entrada</label>
					<input
						type="text" class="form-control" id="inputWindowSize"
						name="inputWindowSize" value="4">
				</div>
				<div class="form-group">
					<label for="trainingTimeWindow">Quantidade de Neurônios na Camada Oculta</label>
					<input type="text" class="form-control"
						id="hiddenLayerNeurons" name="hiddenLayerNeurons" value="22">
				</div>
				<button type="submit" class="btn btn-primary">Treinar</button>
			</form>
		</main>
	</div>
</div>
<%@ include file="footer.jsp"%>