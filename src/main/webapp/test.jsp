<%@ page import="com.user.UserDTO" %>
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
  <title>문서 목록 조회</title>
</head>
<body>

  
  <div class="container w-100 t-c">
    <div class="col-lg-6"></div>
    <div class="col-lg-6 d-ib">
      <div class="jumbotron t-c t-p">
        <form id="DocumentInfo" method="post" name="DocumentInfo" action="DocumentUpdate"  enctype="multipart/form-data" >
          <h3>문서 수정</h3>
          
          <div class="input-group mb-3">
            <span class="input-group-text w-90p">문서 제목</span>
            <input type="text" id="documentName" name="documentName" class="form-control" placeholder="문서 제목" 
            aria-label="DocumentName" aria-describedby="DocumentName" maxlength="25" required>
          </div>
          
          <div class="input-group mb-3">
            <input type="file" id="fileName" name="fileName" class="form-control" required>
          </div>
          
          <div class="input-group mb-3">
            <label class="input-group-text w-90p" for="clientCode">고객사</label>
            <select class="form-select" id="clientCode" name="clientCode">
              <option selected>미선택</option>
              <c:forEach var="list" items="${client}">
                <option value="${list.clientCode}">${list.clientName}</option>
              </c:forEach>
            </select>
          </div>
          
          <div class="input-group mb-3">
            <label class="input-group-text w-90p">목록 선택</label>
            <select class="form-select" title="ChangeSelect" id="ChangeSelect" name="ChangeSelect" required>
              <option value="" disabled selected>선택하시오</option>
              <c:forEach var="list" items="${category}">
                <option value="${list.categoryCode}">${list.categoryName}</option>
              </c:forEach>
            </select>
          </div>
          
          <div class="input-group mb-3">
            <span class="input-group-text w-90p">선택된 목록</span>
            <input type="text" class="form-control" id="nowCategoryName" name="nowCategoryName" placeholder="위 문서 목록을 선택하면 자동기입 됩니다." required readonly>
          </div>
          
          <input type="hidden" class="form-control" id="categoryLv" name="categoryLv" required readonly>
          <input type="hidden" class="form-control" id="categoryCode" name="categoryCode" required readonly>
          
          <div class="input-group">
            <span class="input-group-text w-90p">설명</span>
            <textarea class="form-control" name="fileContent" aria-label="With textarea" placeholder="문서 설명을 적어주세요."></textarea>
          </div> <br>
          <input type="button" class="btn btn-danger form-control" value="삭제" onClick="history.back()" >
          <input type="submit" class="btn btn-primary form-control" value="문서 수정" onClick="return check()" >
          <div>
            <input type="button" class="btn btn-primary form-control" value="취소" onClick="history.back()">
          </div>
        </form>
      </div>
    </div>
    <div class="col-lg-4"></div>
  </div>


  <div id="modalContainer"></div>

  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/document.js"></script>
</body>
</html>