<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html" charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="css/bootstrap.min.css" >
  <link rel="stylesheet" href="css/custom.css">
  <link rel="stylesheet" href="css/custom-background.css">
  <title>루키스 문서 관리 - 메인</title>
</head>
<body>
  <c:set var="user" value="${sessionScope.user}" />
  <c:choose>
    <c:when test="${empty user}">
      <nav class="navbar navbar-expand-lg bg-body-tertiary rounded fixed-top">
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
                <li class="nav-item">
                  <a class="nav-link disabled" href="#">고객사 관리</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link disabled" href="#">문서 관리</a>
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
    </c:when>
    <c:otherwise>
      <nav class="navbar navbar-expand-lg bg-body-tertiary rounded fixed-top">
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
                <c:if test="${user.userCode == 0}">
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
                </c:if>
                <c:choose>
                  <c:when test="${user.isClient}">
                    <li class="nav-item">
                      <a class="nav-link" href="Client">고객사 관리</a>
                    </li>
                  </c:when>
                  <c:otherwise>
                    <li class="nav-item">
                      <a class="nav-link disabled" href="#">고객사 관리</a>
                    </li>
                  </c:otherwise>
                </c:choose>
                <c:choose>
                  <c:when test="${user.isCategory || user.isDocument}">
                    <li class="nav-item dropdown">
                      <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" role="button" aria-expanded="false">문서 관리</a>
                      <ul class="dropdown-menu">
                        <c:choose>
                          <c:when test="${user.isCategory}">
                            <li><a class="dropdown-item" href="Category">문서 목록</a></li>
                          </c:when>
                          <c:otherwise>
                            <li><a class="dropdown-item disabled">문서 목록</a></li>
                          </c:otherwise>
                        </c:choose>
                        <c:choose>
                          <c:when test="${user.isDocument}">
                            <li><a class="dropdown-item" href="Document">문서 관리</a></li>
                            <li><a class="dropdown-item" href="DocumentUpload">문서 등록</a></li>
                          </c:when>
                          <c:otherwise>
                            <li><a class="dropdown-item disabled">문서 관리</a></li>
                            <li><a class="dropdown-item disabled"">문서 등록</a></li>
                          </c:otherwise>
                        </c:choose>
                      </ul>
                    </li>
                  </c:when>
                  <c:otherwise>
                    <li class="nav-item">
                      <a class="nav-link disabled" href="#">문서 관리</a>
                    </li>
                  </c:otherwise>
                </c:choose>
                <c:if test="${user.userCode == 0}">
                  <li class="nav-item">
                    <a class="nav-link" href="Log">로그</a>
                  </li>
                </c:if>
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
    </c:otherwise>
  </c:choose>

  <div class="container d-flex flex-column justify-content-center align-items-center vh-100">
    <div class="p-5 text-center bg-light rounded-3 w-75">
      <h1 class="text-body-emphasis">사이트 소개</h1>
      <p class="col-lg-8 mx-auto fs-5 text-muted">
        이 사이트는 루키스의 문서 관리 포탈입니다. <br> 
        <c:choose>
          <c:when test="${not empty sessionScope.user}">
            비밀번호는 3개월마다 변경해야합니다. <br>  <br>
            <b><span style="color: crimson;">최근 비밀번호 변경일</span> : ${sessionScope.user.dateOfPassword}</b> <br>
          </c:when>
          <c:otherwise>
            <br> 로그인을 해주세요. <br> <br>
          </c:otherwise>
        </c:choose>
      </p>
      <div class="d-inline-flex gap-2 mb-5">
        <c:choose>
          <c:when test="${not empty sessionScope.user}">
            <a href="#" onclick="PasswordChange()">
              <button class="d-inline-flex align-items-center btn btn-outline-primary btn-lg px-4 rounded-pill" type="button">비밀번호 변경</button>
            </a>
          </c:when>
          <c:otherwise>
            <button class="d-inline-flex align-items-center btn btn-outline-primary btn-lg px-4 rounded-pill" type="button" onclick="location.href='Login'">
              로그인 
            </button>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>

  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/userlogic.js"></script>
</body>
</html>
