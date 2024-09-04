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

  <c:set var="user" value="${sessionScope.user}" />

  <c:if test="${user == null or not user.isDocument or list == null}">
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
        <h1 class="my-2">문서 관리 페이지</h1>
      </div>
      
      <div class="col">
        <form class="my-3" method="post" name="search" id="search" action="Document?page=1">
          <div class="input-group d-f mb-3">
            <select class="form-control f-110p" name="searchField" id="searchField" aria-label="searchField" required>
              <option value="" disabled ${empty searchField ? 'selected' : ''}>선택</option>
              <option value="1" ${'1'.equals(searchField) ? 'selected' : ''}>문서 제목</option>
              <option value="2" ${'2'.equals(searchField) ? 'selected' : ''}>고객사</option>
              <option value="3" ${'3'.equals(searchField) ? 'selected' : ''}>문서 위치</option>
              <option value="4" ${'4'.equals(searchField) ? 'selected' : ''}>만든 이</option>
              <option value="5" ${'5'.equals(searchField) ? 'selected' : ''}>최근 수정일</option>
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
    

            
    <div class="input-group mb-3">
      <span class="input-group-text">현재 문서 경로</span>
      <c:choose>
        <c:when test="${not empty category.categoryRoot}">
          <input type="text" class="form-control w-50" aria-label="categoryRoot" value="${fn:replace(category.categoryRoot, 'root/', '')}" readonly>
        </c:when>
        <c:when test="${not empty searchField}">
          <input type="text" class="form-control w-50" aria-label="categoryRoot" value="검색 중" readonly>
        </c:when>
        <c:otherwise>
          <input type="text" class="form-control w-50" aria-label="categoryRoot" value="전체 문서 탐색 중" readonly>
        </c:otherwise>
      </c:choose>
      <select class="form-select" title="ChangeSelect" id="ChangeSelect" name="ChangeSelect" required>
        <option value="" disabled selected>문서 경로 이동</option>
        <c:forEach var="list" items="${selectList}">
          <option value="${list.categoryCode}">${list.categoryName}</option>
        </c:forEach>
      </select>
    </div>

	
    <form id="documentForm" action="DocumentViewDownload" method="POST">
      <div class="row">
        <table class="table table-hover table-dark-line t-c">
          <thead class="table-dark">
            <tr>
              <th scope="col" class="t-c w-4">
                <input type='checkbox' onclick='selectAll(this)'/>
              </th>
              <th scope="col" class="t-c w-25">문서 제목</th>
              <th scope="col" class="t-c w-12">고객사</th>
              <th scope="col" class="t-c w-12">문서 위치</th>
              <th scope="col" class="t-c w-12">만든 이</th>
              <th scope="col" class="t-c w-auto">최근 수정일</th>
              <th scope="col" class="t-c w-7"></th>
              <th scope="col" class="t-c w-7"></th>
            </tr>
          </thead>
    	    <tbody>
            <c:choose>
              <c:when test="${empty list}">
                <tr><td colspan="8" rowspan="4"><h1>문서 없음</h1></td></tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="document" items="${list}">
                  <tr>
                    <td scope="row">
                      <input type="checkbox" name="checkedDocumentCode" value="${document.categoryCode}/${document.fileName}" />
                    </td>
                    <td>${document.fileTitle}</td>
                    <c:choose>
                      <c:when test="${empty document.clientName}">
                        <td>X</td>
                      </c:when>
                      <c:otherwise>
                        <td>${document.clientName}</td>
                      </c:otherwise>
                    </c:choose>
                    <td>${document.categoryName}</td>
                    <td>${document.userName}</td>
                    <td>${document.dateOfUpdate}</td>
                    <td>
                      <button type="button" class="btn btn-outline-dark btn-xs" 
                      onclick="submitForm('DocumentView', '${document.fileName}', '${document.categoryCode}')">
                        조회
                      </button>
                    </td>
                    <td>
                      <button type="button" class="btn btn-outline-dark btn-xs" 
                      onclick="submitForm('DocumentUpdate', '${document.fileName}', '${document.categoryCode}')">
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
    </form>
	
	
    <div class="row">
      <div class="col t-l w-20">
        <button class="btn btn-secondary btn-allow-left" onclick="submitCheckedDocuments();">
          다운로드
        </button>
      </div>
      <c:choose>
      
        <c:when test="${not empty category.categoryRoot}">
  
          <nav class="col t-c w-80" aria-label="Page navigation">
            <ul class="pagination justify-content-center">
              <c:if test="${totalPages > 5}">
                <li class="page-item">
                  <form method="post" action="Document?page=1"  class="d-i"">
                    <input type="hidden" name="categoryCode" value="${category.categoryCode}">
                    <button type="submit" class="page-link">«</button>
                  </form>
                </li>
                <li class="page-item w-55p" >
                  <form method="post" action="Document?page=${(endPage < 6) ? 1 : startPage - 1}" class="d-i">
                    <input type="hidden" name="categoryCode" value="${category.categoryCode}">
                    <button type="submit" class="page-link">이전</button>
                  </form>
                </li>
              </c:if>
    
              <c:forEach var="i" begin="${startPage}" end="${endPage}">
                <c:choose>
                  <c:when test="${i == nowPage or (empty nowPage and i == 1)}">
                    <li class="page-item active">
                      <form method="post" action="Document?page=${i}" class="d-i">
                        <input type="hidden" name="categoryCode" value="${category.categoryCode}">
                        <button type="submit" class="page-link active">${i}</button>
                      </form>
                    </li>
                  </c:when>
                  <c:otherwise>
                    <li class="page-item">
                      <form method="post" action="Document?page=${i}" class="d-i">
                        <input type="hidden" name="categoryCode" value="${category.categoryCode}">
                        <button type="submit" class="page-link">${i}</button>
                      </form>
                    </li>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
                
              <c:if test="${totalPages > 5}">
                <li class="page-item w-55p">
                  <form method="post" action="Document?page=${(endPage == totalPages)?totalPages : endPage + 1}" class="d-i">
                    <input type="hidden" name="categoryCode" value="${category.categoryCode}">
                    <button type="submit" class="page-link">다음</button>
                  </form>
                </li>
                <li class="page-item">
                  <form method="post" action="Document?page=${totalPages}" class="d-i">
                    <input type="hidden" name="categoryCode" value="${category.categoryCode}">
                    <button type="submit" class="page-link">»</button>
                  </form>
                </li>
              </c:if>
            </ul>
          </nav>
        </c:when>
      
      
        <c:when test="${not empty searchField and searchText != null and not empty searchOrder}">
          <c:choose>
            <c:when test="${empty list}"></c:when>
            <c:otherwise>
              <nav class="col t-c w-80" aria-label="Page navigation">
                <ul class="pagination justify-content-center">
                  <c:if test="${totalPages > 5}">
                    <li class="page-item">
                      <form method="post" tabindex="-1" aria-disabled="true" action="Document?page=1" class="d-i">
                        <input type="hidden" name="searchField" value="${searchField}">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <button type="submit" class="page-link">«</button>
                      </form>
                    </li>
                    <li class="page-item w-55p" >
                      <form method="post" tabindex="-1" aria-disabled="true" action="Document?page=${(endPage < 6) ? 1 : startPage - 1}" class="d-i">
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
                          <form method="post" tabindex="-1" aria-disabled="true" action="Document?page=${i}" class="d-i">
                            <input type="hidden" name="searchField" value="${searchField}">
                            <input type="hidden" name="searchText" value="${searchText}">
                            <input type="hidden" name="searchOrder" value="${searchOrder}">
                            <button type="submit" class="page-link active">${i}</button>
                          </form>
                        </li>
                      </c:when>
                      <c:otherwise>
                        <li class="page-item">
                          <form method="post" tabindex="-1" aria-disabled="true" action="Document?page=${i}" class="d-i">
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
                      <form method="post" tabindex="-1" aria-disabled="true" action="Document?page=${(endPage == totalPages)?totalPages : endPage + 1}" class="d-i">
                        <input type="hidden" name="searchField" value="${searchField}">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <button type="submit" class="page-link">다음</button>
                      </form>
                    </li>
                    <li class="page-item">
                      <form method="post" tabindex="-1" aria-disabled="true" action="Document?page=${totalPages}" class="d-i">
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
                  <form method="post" action="Document?page=1"  class="d-i"">
                    <button type="submit" class="page-link">«</button>
                  </form>
                </li>
                <li class="page-item w-55p" >
                  <form method="post" action="Document?page=${(endPage < 6) ? 1 : startPage - 1}" class="d-i">
                    <button type="submit" class="page-link">이전</button>
                  </form>
                </li>
              </c:if>
    
              <c:forEach var="i" begin="${startPage}" end="${endPage}">
                <c:choose>
                  <c:when test="${i == nowPage or (empty nowPage and i == 1)}">
                    <li class="page-item active">
                      <form method="post" action="Document?page=${i}" class="d-i">
                        <button type="submit" class="page-link active">${i}</button>
                      </form>
                    </li>
                  </c:when>
                  <c:otherwise>
                    <li class="page-item">
                      <form method="post" action="Document?page=${i}" class="d-i">
                        <button type="submit" class="page-link">${i}</button>
                      </form>
                    </li>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
                
              <c:if test="${totalPages > 5}">
                <li class="page-item w-55p">
                  <form method="post" action="Document?page=${(endPage == totalPages)?totalPages : endPage + 1}" class="d-i">
                    <button type="submit" class="page-link">다음</button>
                  </form>
                </li>
                <li class="page-item">
                  <form method="post" action="Document?page=${totalPages}" class="d-i">
                    <button type="submit" class="page-link">»</button>
                  </form>
                </li>
              </c:if>
            </ul>
          </nav>
        </c:otherwise>
      </c:choose>
      <div class="col t-r w-20">
        <form method="post" action="DocumentUpload">
          <input type="submit" class="btn btn-dark btn-allow-left" value="등록" />
        </form>
      </div>
    </div>
  </div>
  
  <div id="modalContainer"></div>
  


  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/document.js"></script>
  <script src="js/document.modal.js"></script>
</body>
</html>