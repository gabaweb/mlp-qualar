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
    <nav class="navbar navbar-expand-sm navbar-dark fixed-top bg-dark">
      <a class="navbar-brand" href="./">Inteligenciar: QUALAR</a>
      <button class="navbar-toggler d-lg-none" type="button" data-toggle="collapse" data-target="#navbarsExampleDefault" aria-controls="navbarsExampleDefault" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarsExampleDefault">
        <ul class="navbar-nav ml-auto">
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle active" href="#" id="dropdown01" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Painel de Controle</a>
            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdown01">
              <a class="dropdown-item<%if(name.equals("Prever")){%> active<%}%>" href="./">Prever</a>
              <a class="dropdown-item<%if(name.equals("Treinar")){%> active<%}%>" href="./treinar">Treinar</a>
              <a class="dropdown-item<%if(name.equals("Extrair")){%> active<%}%>" href="./extrair">Extrair</a>
              <a class="dropdown-item<%if(name.equals("Tratar")){%> active<%}%>" href="./tratar">Tratar</a>
              <a class="dropdown-item<%if(name.equals("Entradas")){%> active<%}%>" href="./entradas">Entradas</a>
            </div>
            <li class="nav-item">
            	<a class="nav-link" href="./">Configurações</a>
          	</li>
          </li>
        </ul>
      </div>
    </nav>

    <div class="container-fluid">
      <div class="row">
        <nav class="col-sm-3 col-md-2 d-none d-sm-block bg-light sidebar">
          <ul class="nav nav-pills flex-column">
            <li class="nav-item">
              <a class="nav-link<%if(name.equals("Prever")){%> active<%}%>" href="./">Prever</a>
            </li>
            <li class="nav-item">
              <a class="nav-link<%if(name.equals("Treinar")){%> active<%}%>" href="./treinar">Treinar</a>
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