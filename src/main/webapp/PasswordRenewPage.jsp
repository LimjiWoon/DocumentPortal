<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Insert title here</title>
  <script src="js/userlogic.js"></script>
</head>
<body>
  <script>
    if ("${ID}"){
      createAndSubmitForm("${ID}")
    } else if("${LoginSuccess}" && "${errorMessage}"){
      alert("${errorMessage}");
      opener.location.href='Login';
      self.close();
    } else if("${errorMessage}"){
        alert("${errorMessage}");
        passwordChange();	
    } else{
      alert("비정상적인 접근");
      history.back();
    }
  </script>
</body>
</html>