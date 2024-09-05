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
<title>문서 수정</title>
</head>
<body>

  <c:set var="user" value="${sessionScope.user}" />

  <c:if test="${user == null or not user.isDocument or document == null}">
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
  
  <script type="text/javascript">
    document.addEventListener('DOMContentLoaded', function() {
      //inputID
      var userIDInput = document.querySelector('#documentName');
      userIDInput.addEventListener('input', checkDocumentName);
    });
  </script>
  
  <br>
  
  <div class="container">
    <div class="row d-flex justify-content-between align-items-center">
      <form class="flex-grow-1" id="DocumentInfo" method="post" name="DocumentInfo" action="DocumentUpdate"  enctype="multipart/form-data" >
        <div class="mb-3">
          <table class="table table-dark-line t-c custom-table">
            <thead class="table-dark">
              <tr>
                <td colspan="5">
                  <h1 class="my-2">문서 수정</h1>
                </td>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td class="bg-gray col-1"><b>문서 제목</b></td>
                <td class="col-2">
                  <input type="text" id="documentName" name="documentName" class="form-control" value="${document.fileTitle}" 
                      placeholder="문서 제목" aria-label="DocumentName" aria-describedby="DocumentName" maxlength="25" required>
                </td>
                <td class="bg-gray col-1"><b>고객사</b></td>
                <td class="col-2">
                  <select class="form-select" id="clientCode" name="clientCode">
                    <option ${empty document.clientCode ? 'selected' : ''}>미선택</option>
                    <c:forEach var="list" items="${client}">
                      <option value="${list.clientCode}" ${list.clientCode.equals(document.clientCode) ? 'selected' : ''}>${list.clientName}</option>
                    </c:forEach>
                  </select>
                </td>
              </tr>
              <tr>
                <td class="bg-gray col-1"><b>파일 등록</b></td>
                <td class="col-2">
                  <input type="file" id="fileName" name="fileName" class="form-control" required>
                </td>
                <td class="bg-gray col-1"><b>문서 목록</b></td>
                <td class="col-2">
                  <select class="form-select" id="categoryCode" name="categoryCode" >
                    <option ${empty document.categoryCode ? 'selected' : ''}>미선택</option>
                    <c:forEach var="list" items="${categoryList}">
                      <option value="${list.categoryCode}" ${list.categoryCode.equals(document.categoryCode) ? 'selected' : ''} >${list.categoryName}</option>
                    </c:forEach>
                  </select>
                </td>
              </tr>
              <tr>
                <td class="bg-gray col-1"><b>설명</b></td>
                <td class="col-5" colspan="3">
                  <textarea class="form-control" name="fileContent" aria-label="With textarea" placeholder="문서 설명을 적어주세요.">${document.fileContent}</textarea>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="d-flex justify-content-end">
          <input type="button" class="btn btn-secondary me-2" value="수정" onclick="checkAndUpdate();">
          <input type="button" class="btn btn-secondary me-2" value="취소" onClick="history.back()">
          <input type="button" class="btn btn-danger" value="삭제" onclick="document.getElementById('deleteForm').submit()" >
        </div>
        <input type="hidden" class="form-control" id="originFileTitle" name="originFileTitle" 
            value="${document.fileTitle}" required readonly>
        <input type="hidden" class="form-control" id="originFileName" name="originFileName" 
            value="${document.fileName}" required readonly>
        <input type="hidden" class="form-control" id="originClientCode" name="originClientCode" 
            value="${document.clientCode}" required readonly>
        <input type="hidden" class="form-control" id="originCategoryCode" name="originCategoryCode" 
            value="${document.categoryCode}" required readonly>
        <input type="hidden" class="form-control" id="originFileContent" name="originFileContent" 
            value="${document.fileContent}" required readonly>
      </form>
    </div>
    <div class="col-lg-4">
      <form id="deleteForm" action="DocumentDelete" method="post">
        <input type="hidden" name="clientName" value="${document.clientName}" />
        <input type="hidden" name="categoryCode" value="${document.categoryCode}" />
        <input type="hidden" name="fileName" value="${document.fileName}" />
      </form>
    </div>
  </div>
  
          
  
  
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
          <button type="button" class="btn btn-primary" onclick="confirmUpload(true)">Yes</button>
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" onclick="confirmUpload(false)">No</button>
        </div>
      </div>
    </div>
  </div>
  
  
  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/document.js"></script>
</body>
</html>