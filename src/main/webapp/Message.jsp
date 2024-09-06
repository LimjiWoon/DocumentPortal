<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>알림</title>
  <script type="text/javascript">
    window.onload = function() {
        var messageClient = "<%= request.getAttribute("messageClient") != null ? request.getAttribute("messageClient") : "" %>";
        if (messageClient) {
          alert(messageClient);

          var form = document.createElement('form');
          form.method = 'POST';
          form.action = 'Client';
          document.body.appendChild(form);
          form.submit();
        }

        var messageUser = "<%= request.getAttribute("messageUser") != null ? request.getAttribute("messageUser") : "" %>";
        if (messageUser) {
          alert(messageUser);
          
          var form = document.createElement('form');
          form.method = 'POST';
          form.action = 'User';
          document.body.appendChild(form);
          form.submit();
        }

        var messageCategory = "<%= request.getAttribute("messageCategory") != null ? request.getAttribute("messageCategory") : "" %>";
        if (messageCategory) {
          alert(messageCategory);
          
          var form = document.createElement('form');
          form.method = 'POST';
          form.action = 'Category';
          document.body.appendChild(form);
          form.submit();
        }

        var messageDocument = "<%= request.getAttribute("messageDocument") != null ? request.getAttribute("messageDocument") : "" %>";
        if (messageDocument) {
          alert(messageDocument);
          
          var form = document.createElement('form');
          form.method = 'POST';
          form.action = 'Document';
          document.body.appendChild(form);
          form.submit();
        }
        
        if (!messageClient && !messageUser && !messageCategory && !messageDocument) {
        	location.href = 'Main';
        }
    };
  </script>
</head>
<body>
</body>
</html>