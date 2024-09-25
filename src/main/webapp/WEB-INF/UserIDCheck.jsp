<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta http-equiv="Content-Type" content="text/html" charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="css/bootstrap.min.css" >
  <link rel="stylesheet" href="css/custom.css">
  <title>아이디 중복 체크</title>
</head>
<body onload="getID()">
  <div class="container w-100 t-c">
    <div class="col-lg-4"></div>
    <div class="col-lg-4">
      <div class="jumbotron t-p">
        <table class="table table-dark-line t-c custom-table">
          <thead class="table-dark">
            <tr>
              <td colspan="3"><h3 class="t-c"><b>아이디 중복체크</b></h3>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="t-c bg-gray w-25" rowspan="2"><b>아이디</b></td>
              <td class="t-c" colspan="2">
                <form id="checkForm">
                  <div class="input-group">
                    <input type="text" class="form-control" name="userID" id="userID">
                    <button type="button" class="btn btn-secondary" onclick="IDCheck()">중복확인</button>
                  </div>
                </form>
              </td>
            </tr>
            <tr>
              <td class="t-c" colspan="3"><b> <div id="msg">아이디 중복확인을 해주세요.</div> </b></td>
            </tr>
          </tbody>
        </table>
        <div class="btn-group w-100" role="group">
          <button id="useBtn" type="button" class="btn btn-secondary w-50" onclick="sendCheckValue()" disabled>사용하기</button>
          <button id="cancelBtn" type="button" class="btn btn-secondary w-50" onclick="window.close()">취소</button>
        </div>
      </div>
    </div>
    <div class="col-lg-4"></div>
  </div>

  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/userlogic.js"></script>
  <script src="js/user.id.js"></script>
</body>
</html>