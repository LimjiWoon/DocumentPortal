<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Modal Content</title>
</head>
<body>


  <div class="modal fade" id="CategoryModal" data-bs-keyboard="false" 
  tabindex="-1" aria-labelledby="CategoryModallabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header">
          <c:choose>
            <c:when test="${selectType eq '0'}">
              <h5 class="modal-title" id="CategoryModallabel">고객사 목록</h5>
            </c:when>
            <c:when test="${selectType eq '1'}">
              <h5 class="modal-title" id="CategoryModallabel">문서 목록</h5>
            </c:when>
            <c:otherwise>
              <script>
                alert("비정상적인 접근");
                location.href = 'Main';
              </script>
            </c:otherwise>
          </c:choose>
          <button type="button" class="btn-close btn-outline-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        
        <div class="modal-body">
          <table class="table table-dark-line table-bordered">
            <tr>
              <c:forEach var="clientName" items="${list}" varStatus="status">
                <td>
                  <c:if test="${empty clientName}">
                    미분류
                  </c:if>
                  <c:if test="${!empty clientName}">
                    ${clientName}
                  </c:if>
                </td>
                 
                <c:if test="${status.index % 3 == 2}">
                  </tr><tr>
                </c:if>
              </c:forEach>

              <c:if test="${status.count % 3 != 0}">
                <c:forEach begin="1" end="${3 - (status.count % 3)}">
                  <td></td>
                </c:forEach>
              </c:if>
            </tr>
          </table>
        </div>
        
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
