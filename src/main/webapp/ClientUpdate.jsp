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
<title>고객사 수정</title>
</head>
<body>
  <c:set var="user" value="${sessionScope.user}" />

  <c:if test="${user == null or not user.isClient or client == null}">
    <script>
      alert("비정상적인 접근");
      location.href = 'Main.jsp';
    </script>
  </c:if>
  
  <c:if test="${message != null}">
    <script>
      alert("${message}");
    </script>
    <c:remove var="message" scope="session"/>
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
                              <input type="submit" class="dropdown-item" value="문서 목록 조회" />
                            </form>
                            <form method="post" action="CategoryUpload">
                              <input type="submit" class="dropdown-item" value="문서 목록 등록" />
                            </form>
                          </c:when>
                          <c:otherwise>
                            <li><a class="dropdown-item disabled">문서 목록 조회</a></li>
                            <li><a class="dropdown-item disabled"">문서 목록 등록</a></li>
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

  <div class="container w-100 t-c">
    <div class="col-lg-6"></div>
    <div class="col-lg-6 d-ib">
      <div class="jumbotron t-c t-p">
        <form id="ClientInfo" method="post" name="ClientInfo" action="ClientUpdate">
          <h3>고객사 수정</h3>
          
          <div class="input-group mb-3">
            <span class="input-group-text w-90p">이름</span>
            <input type="text" id="clientName" name="clientName" class="form-control" value="${client.clientName}"
            placeholder="고객사 이름" aria-label="ClientName" aria-describedby="ClientName" maxlength="25" required>
          </div>
          
          <div class="input-group mb-3">
            <label class="input-group-text w-90p">목록 선택</label>
            <select class="form-select" title="ChangeSelect" id="ChangeSelect" name="ChangeSelect">
              <option value="" disabled selected>문서 목록 변경시 선택</option>
              <option value ="10000">(root)</option>
              <c:forEach var="list" items="${list}">
                <option value="${list.categoryCode}">${list.categoryName}</option>
              </c:forEach>
            </select>
          </div>
          
          <div class="input-group mb-3">
            <span class="input-group-text w-90p">선택한 목록</span>
            <input type="text" class="form-control" id="nowCategoryName" name="nowCategoryName" value="${category.categoryName}"
            placeholder="위 문서 목록을 선택하면 자동기입 됩니다." required readonly>
          </div>
          
          <input type="hidden" class="form-control" id="clientCode" name="clientCode" value="${client.clientCode}" required readonly>
          <input type="hidden" class="form-control" id="categoryLv" name="categoryLv" value="${category.categoryLv}" required readonly>
          <input type="hidden" class="form-control" id="categoryCode" name="categoryCode" value="${category.categoryCode}" required readonly>
          <input type="hidden" class="form-control" id="hiddenCategoryCode" name="hiddenCategoryCode" value="${category.categoryCode}" required readonly>
          <input type="hidden" class="form-control" id="hiddenClientName" name="hiddenClientName" value="${client.clientName}" required readonly>
          <input type="hidden" class="form-control" id="hiddenClientContent" name="hiddenClientContent" value="${client.clientContent}" required readonly>
          
          <div class="input-group">
            <span class="input-group-text w-90p">설명</span>
            <textarea id="clientContent" name="clientContent" class="form-control" placeholder="고객사 설명" aria-label="With textarea" maxlength="200">${client.clientContent}</textarea>
          </div> <br>
          
          <input type="submit" class="btn btn-primary form-control" value="고객사 수정" >
          <div>
            <input type="button" class="btn btn-primary form-control" value="취소" onClick="history.back()" >
          </div>
        </form>
      </div>
    </div>
    <div class="col-lg-6"></div>
  </div>
  
  
  <div id="modalContainer"></div>

  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/client.js"></script>
  <script src="js/category.modal.js"></script>
</body>
</html>