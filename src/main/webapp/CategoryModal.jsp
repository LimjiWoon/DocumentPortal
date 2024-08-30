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


  <div class="modal fade" id="CategoryModal" data-bs-backdrop="static" data-bs-keyboard="false" 
  tabindex="-1" aria-labelledby="CategoryModallabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable">
      <div class="modal-content">
        <c:choose>
          <c:when test="${empty list or empty type}">
          
        <div class="modal-header">
          <h5 class="modal-title" id="CategoryModallabel">잘못된 접근</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
        </div>
        
          </c:when>
          <c:otherwise>
          
        
        <div class="modal-header">
          <c:set var="firstItem" value="${list.get(0)}" />
          <h5 class="modal-title" id="CategoryModallabel">${firstItem.categoryName}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
        
          <c:if test="${list.size() == 1}">
            <h1>하위 목록 없음</h1>
          </c:if>
        
          <c:choose>
            <c:when test="${type eq '0'}">
            
              <ul>
                <c:set var="Lv" value="${firstItem.categoryLv + 1}" />
                <c:forEach var="item" items="${list}" begin="1">
                  <c:choose>
                    <c:when test="${item.categoryLv == Lv}"> </c:when>
                    <c:when test="${item.categoryLv > Lv}">
                      <c:set var="Lv" value="${Lv + 1}" />
                      <ul>
                    </c:when>
                    <c:otherwise>
                      <c:forEach begin="${item.categoryLv}" end="${Lv - 1}">
                        </ul>
                        <c:set var="Lv" value="${Lv - 1}" /> 
                      </c:forEach> 
                    </c:otherwise>
                  </c:choose>
                  <li>
                    <form method="post" action="Document">
                      <input type="hidden" name="categoryCode" value="${item.categoryCode}" />
                      <button type="submit" class="btn btn-sm">${item.categoryName}</button>
                    </form>
                  </li>
                </c:forEach>
                <c:forEach begin="${firstItem.categoryLv}" end="${Lv - 1}">
                  </ul>
                </c:forEach>
              </ul>
            
            </c:when>
            <c:when test="${type eq '1'}">
            
              <ul>
                <c:set var="Lv" value="${firstItem.categoryLv + 1}" />
                <c:forEach var="item" items="${list}" begin="1">
                  <c:choose>
                    <c:when test="${item.categoryLv == Lv}"> </c:when>
                    <c:when test="${item.categoryLv > Lv}">
                      <c:set var="Lv" value="${Lv + 1}" />
                      <ul>
                    </c:when>
                    <c:otherwise>
                      <c:forEach begin="${item.categoryLv}" end="${Lv - 1}">
                        </ul>
                        <c:set var="Lv" value="${Lv - 1}" /> 
                      </c:forEach> 
                    </c:otherwise>
                  </c:choose>
                  <li>
                    <button type="button" class="btn btn-sm" id="modalValue"
                    data-name="${item.categoryName}" data-Lv="${item.categoryLv}" data-value="${item.categoryCode}">${item.categoryName}</button>
                  </li>
                </c:forEach>
                <c:forEach begin="${firstItem.categoryLv}" end="${Lv - 1}">
                  </ul>
                </c:forEach>
              </ul>
            </c:when>
            <c:when test="${type eq '2'}">
            
              <ul>
                <c:set var="Lv" value="${firstItem.categoryLv + 1}" />
                <c:forEach var="item" items="${list}" begin="1">
                  <c:choose>
                    <c:when test="${item.categoryLv == Lv}"> </c:when>
                    <c:when test="${item.categoryLv > Lv}">
                      <c:set var="Lv" value="${Lv + 1}" />
                      <ul>
                    </c:when>
                    <c:otherwise>
                      <c:forEach begin="${item.categoryLv}" end="${Lv - 1}">
                        </ul>
                        <c:set var="Lv" value="${Lv - 1}" /> 
                      </c:forEach> 
                    </c:otherwise>
                  </c:choose>
                  <li>
                    <form method="post" action="Document">
                      <input type="hidden" name="categoryCode" value="${item.categoryCode}" />
                      <button type="submit" class="btn btn-sm">${item.categoryName}</button>
                    </form>
                  </li>
                </c:forEach>
                <c:forEach begin="${firstItem.categoryLv}" end="${Lv - 1}">
                  </ul>
                </c:forEach>
              </ul>
            </c:when>
            <c:otherwise>
              <script>
                alert("비정상적인 접근");
                history.back();
              </script>
            </c:otherwise>
          </c:choose>
        
        </div>
        
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
          <c:choose>
            <c:when test="${type eq '0'}">
              <form method="post" action="Document">
                <input type="hidden" name="categoryCode" value="${firstItem.categoryCode}" />
                <button type="submit" class="btn btn-dark">열기</button>
              </form>
            </c:when>
            <c:when test="${type eq '1'}">
              <button type="button" class="btn btn-dark" id="modalValue"
                    data-name="${firstItem.categoryName}" data-Lv="${firstItem.categoryLv}" data-value="${firstItem.categoryCode}">선택</button>
            </c:when>
            <c:when test="${type eq '2'}">
              <form method="post" action="Document">
                <input type="hidden" name="categoryCode" value="${firstItem.categoryCode}" />
                <button type="submit" class="btn btn-dark">선택</button>
              </form>
            </c:when>
          </c:choose>
        </div>
        
          </c:otherwise>
        </c:choose>

      </div>
    </div>
  </div>
</body>
</html>
