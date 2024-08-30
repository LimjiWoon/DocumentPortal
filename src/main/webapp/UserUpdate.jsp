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
<title>사용자 수정</title>
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
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" role="button" aria-expanded="false">고객사 관리</a>
              <ul class="dropdown-menu">
                <form method="post" action="Client">
                  <input type="submit" class="dropdown-item" value="고객사 조회" />
                </form>
                <form method="post" action="ClientUpload">
                  <input type="submit" class="dropdown-item" value="고객사 등록" />
                </form>
              </ul>
            </li>
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" role="button" aria-expanded="false">문서 관리</a>
              <ul class="dropdown-menu">
                <form method="post" action="Category">
                  <input type="submit" class="dropdown-item" value="문서 목록 조회" />
                </form>
                <form method="post" action="CategoryUpload">
                  <input type="submit" class="dropdown-item" value="문서 목록 등록" />
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
    
  <div class="container w-100 t-c">
    <div class="col-lg-6"></div>
    <div class="col-lg-6 d-ib">
      <div class="jumbotron t-p t-c">
        <form id="myForm" method="post" name="checkPassword" action="UserUpdate" onsubmit="return check();">
          <h3>사용자 수정</h3>
          <p>틀린 부분 없이 잘 확인해 수정해주세요</p>
          
          <div class="input-group mb-3">
            <input type="hidden" class="form-control" name="userCode" id="userCode" 
            value="${updateUser.userCode}" autocomplete="off" readonly required>
          </div>
          
          <div class="input-group mb-3">
            <span class="input-group-text w-90p">이름</span>
            <input type="text" class="form-control" placeholder="UserName" name="userName" id="userName" 
            value="${updateUser.userName}" autocomplete="off" maxlength=20 required>
          </div>
          
          <div class="input-group mb-3">
            <span class="input-group-text w-90p">아이디</span>
            <input type="text" class="form-control" placeholder="UserID" name="userID" id="userID" 
            value="${updateUser.userID}" autocomplete="off" maxlength=20 readonly required>
          </div>
          
          <div class="input-group mb-3">
            <span class="input-group-text w-90p">비밀번호</span>
            <input type="password" class="form-control" placeholder="UserPassword" name="userPassword" id="userPassword" 
            value="${updateUser.userPassword}" autocomplete="new-password" minlength=10 maxlength=20  required>
          </div>
          
          <div class="form-check">
            <button type="button" class="btn btn-secondary w-50" disabled>고객사</button>
            <input type="radio" class="btn-check" name="isClient" id="isClientO" value=1
             ${updateUser.isClient ? 'checked="checked"' : ''}>
            <label class="btn btn-outline-success w-22" for="isClientO">권한 O</label>
            <input type="radio" class="btn-check" name="isClient" id="isClientX" value=0
             ${!updateUser.isClient ? 'checked="checked"' : ''}>
            <label class="btn btn-outline-danger w-22" for="isClientX">권한 X</label>
          </div>
          
          <div class="form-check">
            <button type="button" class="btn btn-secondary w-50" disabled>문서 목록</button>
            <input type="radio" class="btn-check" name="isCategory" id="isCategoryO" value=1
             ${updateUser.isCategory ? 'checked="checked"' : ''}>
            <label class="btn btn-outline-success w-22" for="isCategoryO">권한 O</label>
            <input type="radio" class="btn-check" name="isCategory" id="isCategoryX" value=0
             ${!updateUser.isCategory ? 'checked="checked"' : ''}>
            <label class="btn btn-outline-danger w-22" for="isCategoryX">권한 X</label>
          </div>
              
          <div class="form-check">
            <button type="button" class="btn btn-secondary w-50" disabled>문서</button>
            <input type="radio" class="btn-check" name="isDocument" id="isDocumentO" value=1
             ${updateUser.isDocument ? 'checked="checked"' : ''}>
            <label class="btn btn-outline-success w-22" for="isDocumentO">권한 O</label>
            <input type="radio" class="btn-check" name="isDocument" id="isDocumentX"  value=0
             ${!updateUser.isDocument ? 'checked="checked"' : ''}>
            <label class="btn btn-outline-danger w-22" for="isDocumentX">권한 X</label>
          </div> <br>
          <input type="submit" class="btn btn-primary form-control" value="사용자 수정">
        </form>
        <div>
          <input type="button" class="btn btn-primary form-control" value="취소" onClick="history.back()" >
        </div>
        <form method="post" name="update" id="update" action="UserRetire" onsubmit="return confirm('정말 퇴사시키겠습니까?');">
          <input type="hidden" name="userCode" id="userCode" value="${updateUser.userCode}" />
          <input type="hidden" name="isRetire" id="isRetire" value="1" />
          <button type="submit" class="btn btn-danger w-100">퇴사</button>
        </form>
      </div>
    </div>
    <div class="col-lg-6"></div>
  </div>

  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/userlogic.js"></script>
</body>
</html>