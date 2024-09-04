<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta http-equiv="Content-Type" content="text/html" charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="css/bootstrap.min.css" >
<link rel="stylesheet" href="css/custom.css">
<title>사용자 등록</title>
</head>
<body>
  <c:set var="user" value="${sessionScope.user}" />

  <c:if test="${user == null or user.userCode != 0}">
    <script>
      alert("비정상적인 접근");
      location.href = 'Main.jsp';
    </script>
  </c:if>
  
  <c:if test="${message != null}">
    <script>
      alert("${message}");
    </script>
  </c:if>
 
  <nav class="navbar navbar-expand-lg bg-body-tertiary rounded">
    <div class="container-fluid">
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbars" aria-controls="navbars" aria-expanded="false">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse d-lg-flex" id="navbars">
        <a class="navbar-brand col-lg-3 me-0" href="Main.jsp">루키스 문서 관리</a>
          <ul class="navbar-nav col-lg-6 justify-content-lg-center">
            <li class="nav-item">
              <a class="nav-link active" aria-current="page" href="Main.jsp">홈</a>
            </li>
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" role="button" aria-expanded="false">사용자 관리</a>
              <ul class="dropdown-menu">
                <li>
                  <form method="post" action="User">
                    <input type="submit" class="dropdown-item" value="사용자 조회" />
                  </form>
                </li>
                <li><a class="dropdown-item" href="UserUpload.jsp">사용자 등록</a></li>
              </ul>
            </li>
            <li class="nav-item">
              <form method="post" action="Client">
                <input type="submit" class="nav-link" value="고객사 관리" />
              </form>
            </li>
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" role="button" aria-expanded="false">문서 관리</a>
              <ul class="dropdown-menu">
                <form method="post" action="Category">
                  <input type="submit" class="dropdown-item" value="문서 목록" />
                </form>
                <form method="post" action="Document">
                  <input type="submit" class="dropdown-item" value="문서 조회" />
                </form>
                <form method="post" action="DocumentUpload">
                  <input type="submit" class="dropdown-item" value="문서 등록" />
                </form>
              </ul>
            </li>
            <li class="nav-item">
              <form method="post" action="Log">
                <input type="submit" class="nav-link active" value="로그" />
              </form>
            </li>
          </ul>
        <div class="d-lg-flex col-lg-3 justify-content-lg-end">
          <a class="nav-link" href="LogoutAction.jsp">
            <button class="btn btn-primary">로그아웃</button>
          </a>
        </div>
      </div>
    </div>
  </nav>

  <script type="text/javascript">
    document.addEventListener('DOMContentLoaded', function() {
      //inputID
      var userIDInput = document.querySelector('#userID');
      userIDInput.addEventListener('input', checkID);

      var userNameInput = document.querySelector('#userName');
      userNameInput.addEventListener('input', checkName);
    });
  </script>
  
  <br>
  <br>

  <div class="container">
    <div class="row d-flex justify-content-between align-items-center">
      <form class="flex-grow-1" id="myForm" method="post" name="checkPassword" action="UserUpload" onsubmit="return check();">
        <div class="mb-3">
          <table class="table table-dark-line t-c custom-table">
            <thead class="table-dark">
              <tr>
                <td colspan="5">
                  <h1 class="my-2">사용자 등록</h1>
                  <p>틀린 부분 없이 잘 확인해 등록해주세요</p>
                </td>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td class="bg-gray col-1"><b>이름</b></td>
                <td class="col-2">
                  <input type="text" class="form-control" placeholder="사용자 이름" name="userName" id="userName" 
                      autocomplete="off" maxlength=20 required>
                </td>
                <td class="bg-gray col-1"><b>고객사</b></td>
                <td class="col-1">
                  <input type="radio" class="btn-check" name="isClient" id="isClientO" value=1 checked>
                  <label class="btn btn-outline-dark w-100" for="isClientO">권한 O</label>
                </td>
                <td class="col-1">
                  <input type="radio" class="btn-check" name="isClient" id="isClientX" value=0 >
                  <label class="btn btn-outline-dark w-100" for="isClientX">권한 X</label>
                </td>
              </tr>
              <tr>
                <td class="bg-gray col-1"><b>아이디</b></td>
                <td class="col-2">
                  <input type="text" class="form-control" placeholder="사용자 아이디" name="userID" id="userID" 
                      autocomplete="off" maxlength=20 required>
                </td>
                <td class="bg-gray col-1"><b>문서 목록</b></td>
                <td class="col-1">
                  <input type="radio" class="btn-check" name="isCategory" id="isCategoryO" value=1 checked >
                  <label class="btn btn-outline-dark w-100" for="isCategoryO">권한 O</label>
                </td>
                <td class="col-1">
                  <input type="radio" class="btn-check" name="isCategory" id="isCategoryX" value=0 >
                  <label class="btn btn-outline-dark w-100" for="isCategoryX">권한 X</label>
                </td>
              </tr>
              <tr>
                <td class="bg-gray col-1"><b>비밀번호</b></td>
                <td class="col-2">
                  <input type="password" class="form-control" placeholder="사용자 비밀번호" name="userPassword" id="userPassword" 
                      autocomplete="new-password" minlength=10 maxlength=20  required>
                </td>
                <td class="bg-gray col-1"><b>고객사</b></td>
                <td class="col-1">
                  <input type="radio" class="btn-check" name="isDocument" id="isDocumentO" value=1 checked >
                  <label class="btn btn-outline-dark w-100" for="isDocumentO">권한 O</label>
                </td>
                <td class="col-1">
                  <input type="radio" class="btn-check" name="isDocument" id="isDocumentX"  value=0 >
                  <label class="btn btn-outline-dark w-100" for="isDocumentX">권한 X</label>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="d-flex justify-content-end">
          <button type="submit" class="btn btn-secondary me-2">
            등록
          </button>
          <input type="button" class="btn btn-secondary" value="취소" onClick="history.back()">
        </div>
      </form>
    </div>
  </div>


  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/userlogic.js"></script>
</body>
</html>