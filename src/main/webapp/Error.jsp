<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>루키스 문서 관리 - 에러</title>
</head>
<body>
  <c:if test="${empty user}">
    <script>
      alert("${errorMessage}");
      location.href = "Login";
    </script>
  </c:if>
  <c:if test="${not empty user}">
    <script>
      alert("${errorMessage}");
      history.back();
    </script>
  </c:if>
</body>
</html>