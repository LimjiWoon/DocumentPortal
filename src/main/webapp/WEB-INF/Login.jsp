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
  <title>루키스 문서 관리 - 로그인</title>
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
	

  <div class="center-container">
    <div class="inner-container">
      <form method="post" action="Login">
        <table class="table table-dark-line t-c custom-table">
          <thead class="table-dark">
            <tr>
              <td colspan="2"><h3 class="t-c"><b>로그인</b></h3>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="t-c bg-gray"><b>아이디</b></td>
              <td><input type="text" class="form-control" pattern="[A-Za-z0-9]*" placeholder="아이디" 
                aria-label="userID" name="userID" maxlength="20" required></td>
            </tr>
            <tr>
              <td class="t-c bg-gray"><b>비밀번호</b></td>
              <td><input type="password" class="form-control"  placeholder="비밀번호" 
                aria-label="userPassword" name="userPassword" maxlength="20" required></td>
            </tr>
          </tbody>
        </table>
        <button type="submit" class="btn btn-secondary form-control">로그인</button>
      </form>
    </div>
  </div>

  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/userlogic.js"></script>
</body>
</html>