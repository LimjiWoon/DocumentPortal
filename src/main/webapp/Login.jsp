<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="css/bootstrap.min.css" >
<link rel="stylesheet" href="css/custom.css">
<link rel="stylesheet" href="css/custom-background.css">
<title>Lucis 문서 관리 포탈</title>
</head>
<body>
  <% session.invalidate(); %>
  <nav class="navbar navbar-expand-lg bg-body-tertiary rounded">
    <div class="container-fluid">
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbars" aria-controls="navbars" aria-expanded="false">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse d-lg-flex" id="navbars">
        <a class="navbar-brand col-lg-3 me-0" href="Main">루키스 문서 관리</a>
        <ul class="navbar-nav col-lg-6 justify-content-lg-center">
          <li class="nav-item">
            <a class="nav-link active" aria-current="page" href="Main">홈</a>
          </li>
          <li class="nav-item">
            <a class="nav-link disabled" aria-current="page" href="Main">고객사 관리</a>
          </li>
          <li class="nav-item">
            <a class="nav-link disabled" aria-current="page" href="Main">문서 관리</a>
          </li>
        </ul>
        <div class="d-lg-flex col-lg-3 justify-content-lg-end">
          <a class="nav-link" href="Login">
            <button class="btn btn-primary">로그인</button>
          </a>
        </div>
      </div>
    </div>
  </nav>
	

  <div class="custom-container my-5 t-c">
    <div class="col-lg-4"></div>
    <div class="p-5 text-center bg-light rounded-3">
      <div class="jumbotron">
        <form method="post" action="Login">
          <h3 class="t-c">로그인하십시오</h3>
          <div class="input-group mb-3">
            <span class="input-group-text t-c w-90p" id="basic-addon1">아이디</span>
            <input type="text" class="form-control" pattern="[A-Za-z0-9]*" placeholder="아이디" aria-label="userID" aria-describedby="basic-addon1" name="userID" maxlength="20" required>
          </div>
          
          <div class="input-group mb-3">
            <span class="input-group-text t-c w-90p" id="basic-addon1">비밀번호</span>
            <input type="password" class="form-control"  placeholder="비밀번호" aria-label="userPassword" aria-describedby="basic-addon1"  name="userPassword" maxlength="20" required>
          </div>
          
          <input type="submit" class="btn btn-primary form-control" value="로그인">
        </form>
      </div>
    </div>
    <div class="col-lg-4"></div>
  </div>

  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/userlogic.js"></script>
</body>
</html>