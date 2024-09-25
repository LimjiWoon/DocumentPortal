<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html" charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="css/bootstrap.min.css" >
  <link rel="stylesheet" href="css/custom.css">
  <title>루키스 문서 관리 - 바밀번호</title>
</head>
<body style="background: white;">
  <c:if test="${empty ID}">
    <script>
      alert("비정상적인 접근");
      location.href = 'Main';
    </script>
  </c:if>

  <div class="container w-100 t-c">
    <div class="col-lg-4"></div>
    <div class="col-lg-4">
      <div class="jumbotron t-p">
        <form method="post" name="checkPassword" action="PasswordRenew" onsubmit="return check();">
          <input type="hidden" class="form-control" name="userID" id="userIDInput" value="${ID}" readonly/>
          <table class="table table-dark-line t-c custom-table">
            <thead class="table-dark">
              <tr>
                <td colspan="2"><h3 class="t-c"><b>비밀번호 변경창</b></h3>
                  <p>비밀번호는 영어, 숫자, 특수기호 각각 1개 이상을 포함해야합니다.</p></td>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td class="t-c bg-gray"><b>기존 비밀번호</b></td>
                <td><input type="password" class="form-control" placeholder="기존 비밀번호를 입력해주세요." 
                  name="nowPassword" autocomplete="new-password" minlength="10" maxlength="20" required /></td>
              </tr>
              <tr>
                <td class="t-c bg-gray"><b>신규 비밀번호</b></td>
                <td><input type="password" class="form-control" placeholder="신규 비밀번호를 입력해주세요." 
                  name="newPassword" autocomplete="new-password" minlength="10"  maxlength="20" required /></td>
              </tr>
              <tr>
                <td class="t-c bg-gray"><b>비밀번호 확인</b></td>
                <td><input type="password" class="form-control" placeholder="신규 비밀번호를 다시 입력해주세요." 
                  name="confirmPassword" autocomplete="new-password" minlength="10" maxlength="20" required /></td>
              </tr>
            </tbody>
          </table>
          <div class="btn-group w-100" role="group">
            <button type="submit" class="btn btn-secondary w-50">비밀번호 변경</button>
            <button type="button" class="btn btn-secondary w-50" onClick="self.close()">창닫기</button>
          </div>
        </form>
      </div>
    </div>
  <div class="col-lg-4"></div>
  </div>


  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/userlogic.js"></script>
</body>
</html>