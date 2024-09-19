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
<title>문서 등록</title>
</head>
<body>

  <c:set var="user" value="${sessionScope.user}" />

  <c:if test="${empty user or not user.isDocument}">
    <script>
      alert("비정상적인 접근");
      location.href = 'Main';
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

  <br>

  <div class="container">
    <div class="row d-flex justify-content-between align-items-center">
      <form class="flex-grow-1" id="DocumentInfo" method="post" name="DocumentInfo" action="DocumentUpload" enctype="multipart/form-data" >
        <div class="mb-3">
          <table class="table table-dark-line t-c custom-table">
            <thead class="table-dark">
              <tr>
                <td colspan="5">
                  <h1 class="my-2">문서 등록</h1>
                </td>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td class="bg-gray col-1"><b>문서 제목</b></td>
                <td class="col-2">
                  <input type="text" id="documentName" name="documentName" class="form-control" placeholder="문서 제목" 
                      aria-label="DocumentName" aria-describedby="DocumentName" maxlength="25" required>
                </td>
                <td class="bg-gray col-1"><b>고객사</b></td>
                <td class="col-2">
                  <select class="form-select" id="clientCode" name="clientCode">
                    <option selected>미선택</option>
                    <c:forEach var="list" items="${client}">
                      <option value="${list.clientCode}">${list.clientName}</option>
                    </c:forEach>
                  </select>
                </td>
              </tr>
              <tr>
                <td class="bg-gray col-1"><b>파일 등록</b></td>
                <td class="col-2">
                  <input type="file" id="fileName" name="fileName" class="form-control" accept=".jpg, .jpeg, .png, .gif, .pdf, .ppt, .pptx, .xls, .xlsx, .xml, .csv, .hwp, .hwpx, .docx, .txt, .zip" required>
                </td>
                <td class="bg-gray col-1"><b>문서 목록</b></td>
                <td class="col-2">
                  <select class="form-select" id="categoryCode" name="categoryCode" required>
                    <option disabled selected>선택</option>
                    <c:forEach var="list" items="${category}">
                      <option value="${list.categoryCode}">${list.categoryName}</option>
                    </c:forEach>
                  </select>
                </td>
              </tr>
              <tr>
                <td class="bg-gray col-1"><b>설명</b></td>
                <td class="col-5" colspan="3">
                  <textarea class="form-control" name="fileContent" aria-label="With textarea" placeholder="jpg,jpeg,png,gif,pdf,ppt,pptx,xls,xlsx,xml,csv,hwp,hwpx,docx,txt,zip 파일만 등록할 수 있습니다." maxlength="200"></textarea>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="d-flex justify-content-end">
          <input type="button" class="btn btn-secondary me-2" value="등록" onclick="checkAndUpload();">
          <input type="button" class="btn btn-secondary" value="취소" onClick="history.back()">
        </div>
      </form>
    </div>
  </div>

  <script type="text/javascript">
    document.addEventListener('DOMContentLoaded', function() {
      //inputID
      var userIDInput = document.querySelector('#documentName');
      userIDInput.addEventListener('input', checkDocumentName);
    });
  </script>


  <div class="modal fade" id="confirmationModal" tabindex="-1" aria-labelledby="confirmationModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="confirmationModalLabel">파일 덮어쓰기 확인</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <p>같은 이름의 파일이 이미 존재합니다. 기존 파일을 덮어쓰시겠습니까?</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-dark" onclick="confirmUpload(true)">예</button>
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" onclick="confirmUpload(false)">아니오</button>
        </div>
      </div>
    </div>
  </div>

  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/document.js"></script>
</body>
</html>