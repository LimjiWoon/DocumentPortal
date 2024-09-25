<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html" charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="css/bootstrap.min.css" >
  <link rel="stylesheet" href="css/custom.css">
  <title>루키스 문서 관리 - 사용자</title>
</head>
<body>
  <c:set var="user" value="${sessionScope.user}" />

  <c:if test="${empty user or user.userCode != 0}">
    <script>
      alert("비정상적인 접근");
      location.href = 'Main';
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
        <a class="navbar-brand col-lg-3 me-0" href="Main">루키스 문서 관리</a>
          <ul class="navbar-nav col-lg-6 justify-content-lg-center">
            <li class="nav-item">
              <a class="nav-link active" href="Main">홈</a>
            </li>
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" role="button" aria-expanded="false">사용자 관리</a>
              <ul class="dropdown-menu">
                <li>
                  <a class="dropdown-item" href="User">사용자 관리</a>
                </li>
                <li>
                  <a class="dropdown-item" href="UserUpload">사용자 등록</a>
                </li>
              </ul>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="Client">고객사 관리</a>
            </li>
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" role="button" aria-expanded="false">문서 관리</a>
              <ul class="dropdown-menu">
                <a class="dropdown-item" href="Category">문서 목록</a>
                <a class="dropdown-item" href="Document">문서 관리</a>
                <a class="dropdown-item" href="DocumentUpload">문서 등록</a>
              </ul>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="Log">로그</a>
            </li>
          </ul>
        <div class="d-lg-flex col-lg-3 justify-content-lg-end">
          <div class="me-4 d-flex align-items-center justify-content-center">
            <c:if test="${not empty sessionScope.user}">
              사용자: &nbsp; <b><span style="color: gray;">${sessionScope.user.userName}</span></b>
            </c:if>
          </div>
          <a class="nav-link" href="Logout">
            <button class="btn btn-primary">로그아웃</button>
          </a>
        </div>
      </div>
    </div>
  </nav>
    
    <br>

  <div class="container">
    <div class="row d-flex justify-content-between align-items-center">
      <form class="flex-grow-1" method="post" id="UserUpdate" name="checkPassword" action="UserUpdate" onsubmit="return check();">
        <div class="mb-3">
          <input type="hidden" class="form-control" name="userCode" id="userCode" 
              value="${updateUser.userCode}" autocomplete="off" readonly required>
          <table class="table table-dark-line t-c custom-table">
            <thead class="table-dark">
              <tr>
                <td colspan="5">
                  <h1 class="my-2">사용자 수정</h1>
                  <p>틀린 부분 없이 잘 확인해 수정해주세요</p>
                </td>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td class="bg-gray col-1"><b>이름</b></td>
                <td class="col-2">
                  <input type="text" class="form-control" placeholder="UserName" name="userName" id="userName" 
                      value="${updateUser.userName}" autocomplete="off" maxlength=20 required>
                </td>
                <td class="bg-gray col-1"><b>고객사</b></td>
                <td class="col-1">
                  <input type="radio" class="btn-check" name="isClient" id="isClientO" value=1
                      ${updateUser.isClient ? 'checked="checked"' : ''} hidden>
                  <label class="btn btn-outline-dark w-100" for="isClientO">권한 O</label>
                </td>
                <td class="col-1">
                  <input type="radio" class="btn-check" name="isClient" id="isClientX" value=0
                    ${!updateUser.isClient ? 'checked="checked"' : ''} hidden>
                  <label class="btn btn-outline-dark w-100" for="isClientX">권한 X</label>
                </td>
              </tr>
              <tr>
                <td class="bg-gray col-1"><b>아이디</b></td>
                <td class="col-2">
                  <input type="text" class="form-control" placeholder="UserID" name="userID" id="userID" 
                      value="${updateUser.userID}" autocomplete="off" maxlength=20 readonly required>
                </td>
                <td class="bg-gray col-1"><b>문서 목록</b></td>
                <td class="col-1">
                  <input type="radio" class="btn-check" name="isCategory" id="isCategoryO" value=1
                      ${updateUser.isCategory ? 'checked="checked"' : ''} hidden>
                  <label class="btn btn-outline-dark w-100" for="isCategoryO">권한 O</label>
                </td>
                <td class="col-1">
                  <input type="radio" class="btn-check" name="isCategory" id="isCategoryX" value=0
                      ${!updateUser.isCategory ? 'checked="checked"' : ''} hidden>
                  <label class="btn btn-outline-dark w-100" for="isCategoryX">권한 X</label>
                </td>
              </tr>
              <tr>
                <td class="bg-gray col-1"><b>비밀번호</b></td>
                <td class="col-2">
                  <input type="password" class="form-control" placeholder="UserPassword" name="userPassword" id="userPassword" 
                      value="${updateUser.userPassword}" autocomplete="new-password" minlength=10 maxlength=20  required>
                </td>
                <td class="bg-gray col-1"><b>문서</b></td>
                <td class="col-1">
                  <input type="radio" class="btn-check" name="isDocument" id="isDocumentO" value=1
                      ${updateUser.isDocument ? 'checked="checked"' : ''} hidden>
                  <label class="btn btn-outline-dark w-100" for="isDocumentO">권한 O</label>
                </td>
                <td class="col-1">
                  <input type="radio" class="btn-check" name="isDocument" id="isDocumentX"  value=0
                      ${!updateUser.isDocument ? 'checked="checked"' : ''} hidden>
                  <label class="btn btn-outline-dark w-100" for="isDocumentX">권한 X</label>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
      </form>
        <div class="d-flex justify-content-end">
          <button type="button" class="btn btn-secondary me-2" onclick="document.getElementById('UserUpdate').submit();">
            수정
          </button>
          <input type="button" class="btn btn-secondary" value="취소" onClick="history.back()">
          <button type="button" class="btn btn-danger ms-2" onclick="if (confirm('정말 퇴사시키겠습니까?')) { document.getElementById('UserRetire').submit(); }">
            퇴사
          </button>
        </div>
      <form method="post" class="ms-2" name="UserRetire" id="UserRetire" action="UserRetire" >
        <input type="hidden" name="userCode" id="userCode" value="${updateUser.userCode}" />
        <input type="hidden" name="isRetire" id="isRetire" value="1" />
      </form>
    </div>
  </div>


  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/userlogic.js"></script>
</body>
</html>