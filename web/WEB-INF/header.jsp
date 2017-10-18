<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="./favicon.ico">

<title>Inteligenciar: QUALAR</title>

<!-- Bootstrap core CSS -->
<link href="./css/bootstrap.min.css" rel="stylesheet">

<!-- Custom styles for this template -->
<link href="./dashboard.css" rel="stylesheet">
</head>

  <body>
    <nav class="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
      <a class="navbar-brand" href="#">Inteligenciar: QUALAR</a>
    </nav>

    <div class="container-fluid">
      <div class="row">
        <nav class="col-sm-3 col-md-2 d-none d-sm-block bg-light sidebar">
          <ul class="nav nav-pills flex-column">
            <li class="nav-item">
              <a class="nav-link<%if(name.equals("Treinar e Prever")){%> active<%}%>" href="./">Treinar e Prever</a>
            </li>
            <li class="nav-item">
              <a class="nav-link<%if(name.equals("Extrair")){%> active<%}%>" href="./extrair">Extrair</a>
            </li>
            <li class="nav-item">
              <a class="nav-link<%if(name.equals("Tratar")){%> active<%}%>" href="./tratar">Tratar</a>
            </li>
            <li class="nav-item">
              <a class="nav-link<%if(name.equals("Entradas")){%> active<%}%>" href="./entradas">Entradas</a>
            </li>
          </ul>
        </nav>

			<main class="col-sm-9 ml-sm-auto col-md-10" role="main">