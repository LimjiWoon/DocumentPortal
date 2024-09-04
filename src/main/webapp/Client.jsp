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
<title>고객사 관리</title>
</head>
<body>

  <c:set var="user" value="${sessionScope.user}" />

  <c:if test="${user == null or not user.isClient or list == null}">
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
  
  <div class="container">
  
    <div class="row align-items-center">
      <div class="col">
        <h1 class="my-2">고객사 관리 페이지</h1>
      </div>
      <div class="col">
        <form class="my-3" method="post" name="search" id="search" action="Client?page=1">
          <div class="input-group d-f mb-3">
            <select class="form-control f-110p" name="searchField" id="searchField" aria-label="searchField" required>
              <option value="" disabled ${empty searchField ? 'selected' : ''}>선택</option>
              <option value="1" ${'1'.equals(searchField) ? 'selected' : ''}>코드</option>
              <option value="2" ${'2'.equals(searchField) ? 'selected' : ''}>이름</option>
              <option value="4" ${'4'.equals(searchField) ? 'selected' : ''}>만든 이</option>
              <option value="5" ${'5'.equals(searchField) ? 'selected' : ''}>사용 유무</option>
              <option value="6" ${'6'.equals(searchField) ? 'selected' : ''}>최근 수정일</option>
            </select>
            <select class="form-control f-90p" name="searchOrder" id="searchOrder" aria-label="searchOrder">
              <option value="1" ${'1'.equals(searchOrder) ? 'selected' : ''}>오름차순</option>
              <option value="2" ${'2'.equals(searchOrder) ? 'selected' : ''}>내림차순</option>
            </select>
            <input type="text" class="form-control f-1" name="searchText" id="searchText" value="${searchText}">
            <button type="submit" class="btn btn-outline-secondary">검색</button>
          </div>
        </form>
      </div>
    </div>
    
    
    
    <div class="row">
      <table class="table table-hover table-dark-line t-c">
        <thead class="table-dark">
          <tr>
            <th scope="col" class="t-c w-7">코드</th>
            <th scope="col" class="t-c w-18">이름</th>
            <th scope="col" class="t-c w-18">만든 이</th>
            <th scope="col" class="t-c w-auto">최근 수정일</th>
            <th scope="col" class="t-c w-10">문서 목록</th>
            <th scope="col" class="t-c w-10">사용 유무</th>
            <th scope="col" class="t-c w-7"></th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty list}">
              <tr><td colspan="9" rowspan="4"><h1>결과 없음</h1></td></tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="client" items="${list}">
                <tr>
                  <td scope="row">${client.clientCode}</td>
                  <td>${client.clientName}</td>
                  <td>${client.userName}</td>
                  <td>${client.dateOfUpdate}</td>
                  <td>
                    <button class="btn btn-outline-dark btn-xs">조회</button>
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${client.isUse}">
                        <button class="btn btn-outline-success btn-xs" onclick="changeUse(this, ${client.clientCode})">O</button>
                      </c:when>
                      <c:otherwise>
                        <button class="btn btn-outline-danger btn-xs" onclick="changeUse(this, ${client.clientCode})">X</button>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <button type="button" class="btn btn-outline-dark btn-xs" data-bs-toggle="modal" data-bs-target="#ClientUpdateModal"
                    data-client-name="${client.clientName}" data-client-code="${client.clientCode}" data-client-content="${client.clientContent}">
                      수정
                    </button>
                  </td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody> 
      </table>
    </div>
    
    

    <div class="row">
      <div class="col t-l w-20"></div>
      <c:choose>
        <c:when test="${not empty searchField and searchText != null and not empty searchOrder}">
          <c:choose>
            <c:when test="${empty list}"></c:when>
            <c:otherwise>
              <nav class="col t-c w-80" aria-label="Page navigation">
                <ul class="pagination justify-content-center">
                  <c:if test="${totalPages > 5}">
                    <li class="page-item">
                      <form method="post" tabindex="-1" aria-disabled="true" action="Client?page=1" class="d-i">
                        <input type="hidden" name="searchField" value="${searchField}">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <button type="submit" class="page-link">«</button>
                      </form>
                    </li>
                    <li class="page-item w-55p" >
                      <form method="post" tabindex="-1" aria-disabled="true" action="Client?page=${(endPage < 6) ? 1 : startPage - 1}" class="d-i">
                        <input type="hidden" name="searchField" value="${searchField}">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <button type="submit" class="page-link">이전</button>
                      </form>
                    </li>
                  </c:if>
                  
                  <c:forEach var="i" begin="${startPage}" end="${endPage}">
                    <c:choose>
                      <c:when test="${i == nowPage or (empty nowPage and i == 1)}">
                        <li class="page-item active">
                          <form method="post" tabindex="-1" aria-disabled="true" action="Client?page=${i}" class="d-i">
                            <input type="hidden" name="searchField" value="${searchField}">
                            <input type="hidden" name="searchText" value="${searchText}">
                            <input type="hidden" name="searchOrder" value="${searchOrder}">
                            <button type="submit" class="page-link active">${i}</button>
                          </form>
                        </li>
                      </c:when>
                      <c:otherwise>
                        <li class="page-item">
                          <form method="post" tabindex="-1" aria-disabled="true" action="Client?page=${i}" class="d-i">
                            <input type="hidden" name="searchField" value="${searchField}">
                            <input type="hidden" name="searchText" value="${searchText}">
                            <input type="hidden" name="searchOrder" value="${searchOrder}">
                            <button type="submit" class="page-link">${i}</button>
                          </form>
                        </li>
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>
              
                  <c:if test="${totalPages > 5}">
                    <li class="page-item w-55p" >
                      <form method="post" tabindex="-1" aria-disabled="true" action="Client?page=${(endPage == totalPages)?totalPages : endPage + 1}" class="d-i">
                        <input type="hidden" name="searchField" value="${searchField}">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <button type="submit" class="page-link">다음</button>
                      </form>
                    </li>
                    <li class="page-item">
                      <form method="post" tabindex="-1" aria-disabled="true" action="Client?page=${totalPages}" class="d-i">
                        <input type="hidden" name="searchField" value="${searchField}">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <button type="submit" class="page-link">»</button>
                      </form>
                    </li>
                  </c:if>
                </ul>
              </nav>
            </c:otherwise>
          </c:choose>
                      
        </c:when>
        <c:otherwise>
  
          <nav class="col t-c w-80" aria-label="Page navigation">
            <ul class="pagination justify-content-center">
              <c:if test="${totalPages > 5}">
                <li class="page-item">
                  <form method="post" action="Client?page=1"  class="d-i"">
                    <button type="submit" class="page-link">«</button>
                  </form>
                </li>
                <li class="page-item w-55p" >
                  <form method="post" action="Client?page=${(endPage < 6) ? 1 : startPage - 1}" class="d-i">
                    <button type="submit" class="page-link">이전</button>
                  </form>
                </li>
              </c:if>
    
              <c:forEach var="i" begin="${startPage}" end="${endPage}">
                <c:choose>
                  <c:when test="${i == nowPage or (empty nowPage and i == 1)}">
                    <li class="page-item active">
                      <form method="post" action="Client?page=${i}" class="d-i">
                        <button type="submit" class="page-link active">${i}</button>
                      </form>
                    </li>
                  </c:when>
                  <c:otherwise>
                    <li class="page-item">
                      <form method="post" action="Client?page=${i}" class="d-i">
                        <button type="submit" class="page-link">${i}</button>
                      </form>
                    </li>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
                
              <c:if test="${totalPages > 5}">
                <li class="page-item w-55p">
                  <form method="post" action="Client?page=${(endPage == totalPages)?totalPages : endPage + 1}" class="d-i">
                    <button type="submit" class="page-link">다음</button>
                  </form>
                </li>
                <li class="page-item">
                  <form method="post" action="Client?page=${totalPages}" class="d-i">
                    <button type="submit" class="page-link">»</button>
                  </form>
                </li>
              </c:if>
            </ul>
          </nav>
        </c:otherwise>
      </c:choose>
      <div class="col t-r w-20">
        <button type="button" class="btn btn-dark btn-allow-left" data-bs-toggle="modal" data-bs-target="#ClientUploadModal">
          등록
        </button>
      </div>
    </div>
  </div>




  <div class="modal fade" id="ClientUploadModal" data-bs-backdrop="static" data-bs-keyboard="false" 
  tabindex="-1" aria-labelledby="ClientUploadModallabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header">
          <h4 class="modal-title" id="ClientUploadModallabel">신규 고객사 등록</h4>
        </div>
        <form id="ClientInfo" method="post" name="ClientInfo" action="ClientUpload">
          <div class="modal-body">
            <div class="container">
              <table class="table table-dark-line t-c custom-table">
                <tbody>
                  <tr>
                    <td class="bg-gray col-1"><b>이름</b></td>
                    <td class="col-5">
                      <input type="text" id="clientName" name="clientName" class="form-control" 
                      placeholder="고객사 이름" aria-label="ClientName" aria-describedby="ClientName" maxlength="25" required>
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray col-1"><b>설명</b></td>
                    <td class="col-5">
                      <textarea id="clientContent" name="clientContent" class="form-control" placeholder="고객사 설명" aria-label="With textarea" maxlength="200"></textarea>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
            <button type="submit" class="btn btn-secondary">등록</button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div class="modal fade" id="ClientUpdateModal" data-bs-backdrop="static" data-bs-keyboard="false" 
  tabindex="-1" aria-labelledby="ClientUpdateModallabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header">
          <h4 class="modal-title" id="ClientUpdateModallabel">고객사 수정</h4>
        </div>
        <form id="ClientInfo" method="post" name="ClientInfo" action="ClientUpdate">
          <div class="modal-body">
            <div class="container">
              <table class="table table-dark-line t-c custom-table">
                <tbody>
                  <tr>
                    <td class="bg-gray col-1"><b>이름</b></td>
                    <td class="col-5">
                      <input type="text" id="clientName" name="clientName" class="form-control" 
                      placeholder="고객사 이름" aria-label="ClientName" aria-describedby="ClientName" maxlength="25" required>
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray col-1"><b>설명</b></td>
                    <td class="col-5">
                      <textarea id="clientContent" name="clientContent" class="form-control" placeholder="고객사 설명" aria-label="With textarea" maxlength="200"></textarea>
                    </td>
                  </tr>
                </tbody>
              </table>
            <input type="hidden" class="form-control" id="clientCode" name="clientCode" required readonly>
            <input type="hidden" class="form-control" id="hiddenClientName" name="hiddenClientName" required readonly>
            <input type="hidden" class="form-control" id="hiddenClientContent" name="hiddenClientContent" required readonly>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
            <button type="submit" class="btn btn-secondary">수정</button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/client.js"></script>
</body>
</html>