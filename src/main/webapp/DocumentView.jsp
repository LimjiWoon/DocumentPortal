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
  <title>문서 목록 관리</title>
</head>
<body>

  <c:set var="user" value="${sessionScope.user}" />

  <c:if test="${user == null or not user.isDocument or empty document}">
    <script>
      alert("비정상적인 접근");
      location.href = 'Main.jsp';
    </script>
  </c:if>
  
  
  
  
  <c:choose>
    <c:when test="${empty user}">
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
                <li class="nav-item">
                  <a class="nav-link disabled" aria-current="page" href="#">고객사 관리</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link disabled" aria-current="page" href="#">문서 관리</a>
                </li>
              </ul>
            <div class="d-lg-flex col-lg-3 justify-content-lg-end">
              <a class="nav-link" href="Login.jsp">
                <button class="btn btn-primary">로그인</button>
              </a>
            </div>
          </div>
        </div>
      </nav>
    </c:when>
    <c:otherwise>
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
                <c:if test="${user.userCode == 0}">
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
                </c:if>
                <c:choose>
                  <c:when test="${user.isClient}">
                    <li class="nav-item">
                      <form method="post" action="Client">
                        <input type="submit" class="nav-link" value="고객사 관리" />
                      </form>
                    </li>
                  </c:when>
                  <c:otherwise>
                    <li class="nav-item">
                      <a class="nav-link disabled" aria-current="page" href="#">고객사 관리</a>
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
                            <form method="post" action="Category">
                              <input type="submit" class="dropdown-item" value="문서 목록" />
                            </form>
                          </c:when>
                          <c:otherwise>
                            <li><a class="dropdown-item disabled">문서 목록</a></li>
                          </c:otherwise>
                        </c:choose>
                        <c:choose>
                          <c:when test="${user.isDocument}">
                            <form method="post" action="Document">
                              <input type="submit" class="dropdown-item" value="문서 조회" />
                            </form>
                            <form method="post" action="DocumentUpload">
                              <input type="submit" class="dropdown-item" value="문서 등록" />
                            </form>
                          </c:when>
                          <c:otherwise>
                            <li><a class="dropdown-item disabled">문서 조회</a></li>
                            <li><a class="dropdown-item disabled"">문서 등록</a></li>
                          </c:otherwise>
                        </c:choose>
                      </ul>
                    </li>
                  </c:when>
                  <c:otherwise>
                    <li class="nav-item">
                      <a class="nav-link disabled" aria-current="page" href="#">문서 관리</a>
                    </li>
                  </c:otherwise>
                </c:choose>
                <c:if test="${user.userCode == 0}">
                  <li class="nav-item">
                    <form method="post" action="Log">
                      <input type="submit" class="nav-link active" value="로그" />
                    </form>
                  </li>
                </c:if>
              </ul>
            <div class="d-lg-flex col-lg-3 justify-content-lg-end">
              <a class="nav-link" href="LogoutAction.jsp">
                <button class="btn btn-primary">로그아웃</button>
              </a>
            </div>
          </div>
        </div>
      </nav>
    </c:otherwise>
  </c:choose>

  <br>
  
  <div class="container">
    <c:set var="document" value="${document}" />
    
    <div class="row">
      <table class="table table-hover table-dark-line table-bordered t-c">
        <thead class="table-dark">
          <tr>
            <th colspan="3"><h1>문서 보기</h1></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="w-25"><b>문서 제목</b></td>
            <td colspan="2">${document.fileTitle}</td>
          </tr>
          <tr>
            <td class="w-25"><b>저장된 문서명</b></td>
            <td colspan="2">
              <form method="post" class="d-flex justify-content-center" action="DocumentViewDownload">
                <input type="hidden" name="categoryCode" value="${document.categoryCode}" />
                <input type="hidden" name="clientName" value="${document.clientName}" />
                <input type="hidden" name="fileName" value="${document.fileName}" />
                <input type="submit" class="btn btn-white w-auto" value="${document.fileName}"/>
              </form>
            </td>
          </tr>
          <tr>
            <td class="w-25"><b>작성자</b></td>
            <td colspan="2">${document.userName}</td>
          </tr>
          <tr>
            <td class="w-25"><b>고객사</b></td>
            <td colspan="2">${document.clientName}</td>
          </tr>
          <tr>
            <td class="w-25"><b>문서 위치</b></td>
            <td colspan="2">${document.categoryName}</td>
          </tr>
          <tr>
            <td class="w-25"><b>최근 수정일</b></td>
            <td colspan="2">${document.dateOfUpdate}</td>
          </tr>
          <tr>
            <td class="w-25"><b>설명</b></td>
            <td colspan="2">${document.fileContent}</td>
          </tr>
        </tbody>
      </table>
      <div class="btn-group" role="group" aria-label="buttonAction">
        <button type="button" class="btn btn-primary" onclick="history.back()">목록</button>
        <button type="button" class="btn btn-success" onclick="document.getElementById('updateForm').submit()">수정</button>
        <button type="button" class="btn btn-danger" onclick="document.getElementById('deleteForm').submit()">삭제</button>
      </div>
	</div>
  </div>
  
  <form id="updateForm" action="DocumentUpdate" method="post">
    <input type="hidden" name="categoryCode" value="${document.categoryCode}" />
    <input type="hidden" name="clientCode" value="${document.clientCode}" />
    <input type="hidden" name="fileName" value="${document.fileName}" />
  </form>
  <form id="deleteForm" action="DocumentDelete" method="post">
    <input type="hidden" name="categoryCode" value="${document.categoryCode}" />
    <input type="hidden" name="clientCode" value="${document.clientCode}" />
    <input type="hidden" name="fileName" value="${document.fileName}" />
  </form>

  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>