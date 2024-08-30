<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="css/bootstrap.min.css" >
<link rel="stylesheet" href="css/custom.css">
<link rel="stylesheet" href="css/custom-background.css">
<title>비밀번호 변경창</title>
</head>
<body>
  <script>
    const userID = "${sessionScope.user.userID}";
  </script>
  <div class="container w-100 t-c">
    <div class="col-lg-4"></div>
    <div class="col-lg-4">
      <div class="jumbotron t-p">
        <form method="post" name="checkPassword" action="UpPwd" onsubmit="return check();">
          <h3 class="t-c">비밀번호 변경창</h3>
          <p class="t-c">비밀번호는 영어, 숫자, 특수기호 각각 1개 이상을 포함해야합니다.</p>
          <div>
            <input type="hidden" class="form-control" name="userID" id="userIDInput" readonly/>
            <script src="js/userID.js"></script>
            <div class="input-group mb-3">
              <span class="input-group-text" id="basic-addon1">기존 비밀번호</span>
              <input type="password" class="form-control" placeholder="Now Password" name="nowPassword" autocomplete="new-password" minlength="10" maxlength="20" required />
            </div>
            <div class="input-group mb-3">
              <span class="input-group-text" id="basic-addon1">신규 비밀번호</span>
              <input type="password" class="form-control" placeholder="New Password" name="newPassword" autocomplete="new-password" minlength="10"  maxlength="20" required />
            </div>
            <div class="input-group mb-3">
              <span class="input-group-text" id="basic-addon1">비밀번호 확인</span>
              <input type="password" class="form-control" placeholder="Confirm New Password" name="confirmPassword" autocomplete="new-password" minlength="10" maxlength="20" required />
            </div>
            <input type="submit" class="btn btn-primary form-control" value="비밀번호 변경" />
          </div>
          <div>
            <input type="button" class="btn btn-primary form-control" value="창닫기" onClick="self.close()">
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