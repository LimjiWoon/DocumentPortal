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
  <link rel="stylesheet" href="css/select2.min.css" >
  <link rel="stylesheet" href="css/select2-bootstrap-5-theme.min.css" >
  <link rel="stylesheet" href="css/custom.css">
  <title>루키스 문서 관리 - 문서</title>
</head>
<body>

  <c:set var="user" value="${sessionScope.user}" />

  <c:if test="${empty user or not user.isDocument or list == null}">
    <script>
      alert("비정상적인 접근");
      location.href = 'Main';
    </script>
  </c:if>
  
  <nav class="navbar navbar-expand-lg bg-body-tertiary rounded">
    <div class="container-fluid">
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbars" aria-controls="navbars" aria-expanded="false">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse d-lg-flex" id="navbars">
        <a class="navbar-brand col-lg-3 me-0" href="Main">루키스 문서 관리</a>
          <ul class="navbar-nav col-lg-6 justify-content-lg-center">
            <li class="nav-item">
              <a class="nav-link active" href="Main">홈</a>
            </li>
            <c:if test="${user.userCode == 0}">
              <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" role="button" aria-expanded="false">사용자 관리</a>
                <ul class="dropdown-menu">
                  <li>
                    <a class="dropdown-item" href="User">사용자 관리</a>
                  </li>
                  <li>
                    <a class="dropdown-item" href="UserUpload">사용자 등록</a>
                  </li>
                </ul>
              </li>
            </c:if>
            <c:choose>
              <c:when test="${user.isClient}">
                <li class="nav-item">
                  <a class="nav-link" href="Client">고객사 관리</a>
                </li>
              </c:when>
              <c:otherwise>
                <li class="nav-item">
                  <a class="nav-link disabled" href="#">고객사 관리</a>
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
                        <li><a class="dropdown-item" href="Category">문서 목록</a></li>
                      </c:when>
                      <c:otherwise>
                        <li><a class="dropdown-item disabled">문서 목록</a></li>
                      </c:otherwise>
                    </c:choose>
                    <c:choose>
                      <c:when test="${user.isDocument}">
                        <li><a class="dropdown-item" href="Document">문서 관리</a></li>
                        <li><a class="dropdown-item" href="DocumentUpload">문서 등록</a></li>
                      </c:when>
                      <c:otherwise>
                        <li><a class="dropdown-item disabled">문서 관리</a></li>
                        <li><a class="dropdown-item disabled"">문서 등록</a></li>
                      </c:otherwise>
                    </c:choose>
                  </ul>
                </li>
              </c:when>
              <c:otherwise>
                <li class="nav-item">
                  <a class="nav-link disabled" href="#">문서 관리</a>
                </li>
              </c:otherwise>
            </c:choose>
            <c:if test="${user.userCode == 0}">
              <li class="nav-item">
                <a class="nav-link" href="Log">로그</a>
              </li>
            </c:if>
          </ul>
        <div class="d-lg-flex col-lg-3 justify-content-lg-end">
          <div class="me-4 d-flex align-items-center justify-content-center">
            <c:if test="${not empty sessionScope.user}">
              사용자: &nbsp; <b><span style="color: gray;">${sessionScope.user.userName}</span></b>
            </c:if>
          </div>
          <a class="nav-link" href="Logout">
            <button class="btn btn-primary">로그아웃</button>
          </a>
        </div>
      </div>
    </div>
  </nav>
  
  
  <div class="container">
  
    <div class="row align-items-center">
      <div class="col">
        <h1 class="my-2">문서 관리 페이지</h1>
      </div>
      
      <div class="col">
        <form class="my-3" method="post" name="search" id="search" action="Document?page=1">
          <input type="hidden" name="startDate" value="${startDate}">
          <input type="hidden" name="endDate" value="${endDate}">
          <input type="hidden" name="filterClient" value="${filterClient}">
          <input type="hidden" name="filterCategory" value="${filterCategory}">
          <div class="input-group d-f mb-3">
            <select class="form-control f-110p" name="searchField" id="searchField" aria-label="searchField" required>
              <option value="" disabled ${empty searchField ? 'selected' : ''}>선택</option>
              <option value="1" ${'1'.equals(searchField) ? 'selected' : ''}>문서명</option>
              <option value="4" ${'4'.equals(searchField) ? 'selected' : ''}>작성자</option>
              <c:if test="${empty filterClient}">
                <option value="2" ${'2'.equals(searchField) ? 'selected' : ''}>고객사</option>
              </c:if>
              <c:if test="${empty filterCategory}">
                <option value="3" ${'3'.equals(searchField) ? 'selected' : ''}>문서 목록</option>
              </c:if>
            </select>
            <select class="form-control f-90p" name="searchOrder" id="searchOrder" aria-label="searchOrder">
              <option value="1" ${'1'.equals(searchOrder) ? 'selected' : ''}>오름차순</option>
              <option value="2" ${'2'.equals(searchOrder) ? 'selected' : ''}>내림차순</option>
            </select>
            <input type="text" class="form-control f-1" name="searchText" id="searchText" value="${searchText}">
            <button type="submit" class="btn btn-secondary">검색</button>
            <button type="button" class="btn btn-secondary" data-bs-toggle="modal" data-bs-target="#SearchFilterModal">필터</button>
          </div>
        </form>
      </div>
    </div>
    
	
    <form id="documentForm" action="#" method="POST">
      <div class="row">
        <table class="table table-hover table-dark-line t-c">
          <thead class="table-dark">
            <tr>
              <th scope="col" class="t-c w-4">
                <input type='checkbox' onclick='selectAll(this)'/>
              </th>
              <th scope="col" class="t-c w-25">문서명</th>
              <th scope="col" class="t-c w-12">작성자</th>
              <th scope="col" class="t-c w-12">고객사</th>
              <th scope="col" class="t-c w-12">문서 목록</th>
              <th scope="col" class="t-c w-auto">최근 수정일</th>
              <th scope="col" class="t-c w-7">조회</th>
              <th scope="col" class="t-c w-7">수정</th>
            </tr>
          </thead>
    	    <tbody>
            <c:choose>
              <c:when test="${empty list}">
                <tr><td colspan="8" rowspan="4"><h1 class="c-b">문서 없음</h1></td></tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="document" items="${list}">
                  <tr>
                    <td scope="row">
                      <input type="checkbox" name="checkedDocumentCode" value="${document.categoryCode}/${document.clientName}/${document.fileName}" />
                    </td>
                    <td>${document.fileTitle}</td>
                    <td>${document.userName}</td>
                    <td>${document.clientName}</td>
                    <td>${document.categoryName}</td>
                    <td>${document.dateOfUpdate}</td>
                    <td>
                      <button type="button" class="btn btn-outline-dark btn-xs" 
                      onclick="submitForm('DocumentView', '${document.fileName}', '${document.categoryCode}', '${document.clientCode}')">
                        조회
                      </button>
                    </td>
                    <td>
                      <button type="button" class="btn btn-outline-dark btn-xs" 
                      onclick="submitForm('DocumentUpdate', '${document.fileName}', '${document.categoryCode}', '${document.clientCode}')">
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
        <button type="button" class="btn btn-secondary" data-bs-toggle="modal" data-bs-target="#DownloadFilterModal">
          다운로드
        </button>
        <button class="btn btn-danger btn-allow-left" onclick="submitCheckedDocuments('DocumentDelete');">
          삭제
        </button>
        <a class="btn btn-secondary" href="Document">검색/필터 초기화</a>
      </div>
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
                    <input type="hidden" name="endDate" value="${endDate}">
                    <input type="hidden" name="isUse" value="${isUse}">
                    <input type="hidden" name="filterCategory" value="${filterCategory}">
                    <input type="hidden" name="filterClient" value="${filterClient}">
                    <button type="submit" class="page-link">«</button>
                  </form>
                </li>
                <li class="page-item w-55p" >
                  <form method="post" tabindex="-1" aria-disabled="true" action="Document?page=${(endPage < 6) ? 1 : startPage - 1}" class="d-i">
                    <input type="hidden" name="searchField" value="${searchField}">
                    <input type="hidden" name="searchText" value="${searchText}">
                    <input type="hidden" name="searchOrder" value="${searchOrder}">
                    <input type="hidden" name="endDate" value="${endDate}">
                    <input type="hidden" name="isUse" value="${isUse}">
                    <input type="hidden" name="filterCategory" value="${filterCategory}">
                    <input type="hidden" name="filterClient" value="${filterClient}">
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
                        <input type="hidden" name="endDate" value="${endDate}">
                        <input type="hidden" name="isUse" value="${isUse}">
                        <input type="hidden" name="filterCategory" value="${filterCategory}">
                        <input type="hidden" name="filterClient" value="${filterClient}">
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
                        <input type="hidden" name="endDate" value="${endDate}">
                        <input type="hidden" name="isUse" value="${isUse}">
                        <input type="hidden" name="filterCategory" value="${filterCategory}">
                        <input type="hidden" name="filterClient" value="${filterClient}">
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
                    <input type="hidden" name="endDate" value="${endDate}">
                    <input type="hidden" name="isUse" value="${isUse}">
                    <input type="hidden" name="filterCategory" value="${filterCategory}">
                    <input type="hidden" name="filterClient" value="${filterClient}">
                    <button type="submit" class="page-link">다음</button>
                  </form>
                </li>
                <li class="page-item">
                  <form method="post" tabindex="-1" aria-disabled="true" action="Document?page=${totalPages}" class="d-i">
                    <input type="hidden" name="searchField" value="${searchField}">
                    <input type="hidden" name="searchText" value="${searchText}">
                    <input type="hidden" name="searchOrder" value="${searchOrder}">
                    <input type="hidden" name="endDate" value="${endDate}">
                    <input type="hidden" name="isUse" value="${isUse}">
                    <input type="hidden" name="filterCategory" value="${filterCategory}">
                    <input type="hidden" name="filterClient" value="${filterClient}">
                    <button type="submit" class="page-link">»</button>
                  </form>
                </li>
              </c:if>
            </ul>
          </nav>
        </c:otherwise>
      </c:choose>

      <div class="col t-r w-20">
        <a class="btn btn-dark btn-allow-left" href="DocumentUpload">등록</a>
      </div>
    </div>
  </div>
  
  
  
  <div class="modal fade" id="SearchFilterModal" data-bs-keyboard="false" tabindex="-1" aria-labelledby="SearchFilterModallabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h4 class="modal-title" id="SearchFilterModallabel">검색 필터</h4>
        </div>
        <form id="SearchFilter" method="post" name="SearchFilter" action="Document">
          <div class="modal-body">
            <div class="container">
              <table class="table table-dark-line t-c custom-table">
                <tbody>
                  <tr>
                    <td class="bg-gray">
                      <select class="form-control select-gray-custom" name="searchField" id="searchField" aria-label="searchField">
                        <option value="" disabled ${empty searchField ? 'selected' : ''}>선택</option>
                        <option value="1" ${'1'.equals(searchField) ? 'selected' : ''}>문서명</option>
                        <option value="4" ${'4'.equals(searchField) ? 'selected' : ''}>작성자</option>
                        <c:if test="${empty filterClient}">
                          <option value="2" ${'2'.equals(searchField) ? 'selected' : ''}>고객사</option>
                        </c:if>
                        <c:if test="${empty filterCategory}">
                          <option value="3" ${'3'.equals(searchField) ? 'selected' : ''}>문서 목록</option>
                        </c:if>
                      </select>
                    </td>
                    <td colspan="3">
                      <input type="text" class="form-control f-1" name="searchText" id="searchText" value="${searchText}">
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray"><b>정렬</b></td>
                    <td colspan="3">
                      <select class="form-control" name="searchOrder" id="searchOrder" aria-label="searchOrder">
                        <option value="1" ${'1'.equals(searchOrder) ? 'selected' : ''}>오름차순</option>
                        <option value="2" ${'2'.equals(searchOrder) ? 'selected' : ''}>내림차순</option>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray col-4"><b>수정일</b></td>
                    <td class="col-3">
                      <input type="date" class="form-control" id="startDate" name="startDate"
                        value="${startDate}" min="2009-01-01" max="2039-12-31" />
                    </td>
                    <td class="bg-gray col-1"><b>-</b></td>
                    <td class="col-3">
                      <input type="date" class="form-control" id="endDate" name="endDate"
                        value="${endDate}" min="2009-01-01" max="2039-12-31" />
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray"><b>문서 목록</b></td>
                    <td colspan="3">
                      <select class="form-select" title="filterCategory" id="filterCategory" name="filterCategory" >
                        <option ${empty filterCategory ? 'selected' : ''} ></option>
                        <c:forEach var="list" items="${categoryList}">
                          <option value="${list.categoryCode}" ${(list.categoryCode == filterCategory) ? 'selected' : ''} >${list.categoryName}</option>
                        </c:forEach>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray"><b>고객사</b></td>
                    <td colspan="3">
                      <select class="form-select" title="filterClient" id="filterClient" name="filterClient" >
                        <option ${empty filterClient ? 'selected' : ''} ></option>
                        <c:forEach var="list" items="${clientList}">
                          <option value="${list.clientCode}" ${(list.clientCode == filterClient) ? 'selected' : ''} >${list.clientName}</option>
                        </c:forEach>
                      </select>
                    </td>
                  </tr>
                  
                </tbody>
              </table>
            </div>
          </div>
          <div class="modal-footer">
            <button type="submit" class="btn btn-secondary" onclick="searchExcel('Document')">적용</button>
            <button type="button" class="btn btn-secondary" onclick="downloadExcel('Excel', 'hidden4')">출력</button>
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
          </div>
        </form>
      </div>
    </div>
  </div>
  
    <div class="modal fade" id="DownloadFilterModal" data-bs-keyboard="false" tabindex="-1" aria-labelledby="DowonloadFilterModallabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h4 class="modal-title" id="DowonloadFilterModallabel">다운로드 옵션 선택</h4>
        </div>
        <form id="DownloadFilter" method="post" name="DownloadFilter" action="DocumentDownload">
          <div class="modal-body">
            <div class="container">
              <table class="table table-dark-line t-c custom-table">
                <tbody>
                  <tr>
                    <td class="bg-gray">
                      <select class="form-control select-gray-custom" name="searchField" id="searchField" aria-label="searchField">
                        <option value="" disabled ${empty searchField ? 'selected' : ''}>선택</option>
                        <option value="1" ${'1'.equals(searchField) ? 'selected' : ''}>문서명</option>
                        <option value="4" ${'4'.equals(searchField) ? 'selected' : ''}>작성자</option>
                        <c:if test="${empty filterClient}">
                          <option value="2" ${'2'.equals(searchField) ? 'selected' : ''}>고객사</option>
                        </c:if>
                        <c:if test="${empty filterCategory}">
                          <option value="3" ${'3'.equals(searchField) ? 'selected' : ''}>문서 목록</option>
                        </c:if>
                      </select>
                    </td>
                    <td colspan="3">
                      <input type="text" class="form-control f-1" name="searchText" id="searchText" value="${searchText}">
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray col-4"><b>수정일</b></td>
                    <td class="col-3">
                      <input type="date" class="form-control" id="startDate" name="startDate"
                        value="${startDate}" min="2009-01-01" max="2039-12-31" />
                    </td>
                    <td class="bg-gray col-1"><b>-</b></td>
                    <td class="col-3">
                      <input type="date" class="form-control" id="endDate" name="endDate"
                        value="${endDate}" min="2009-01-01" max="2039-12-31" />
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray"><b>문서 목록</b></td>
                    <td colspan="3">
                      <select class="form-select" title="filterDownloadCategory" id="filterDownloadCategory" name="filterCategory">
                        <option ${empty filterCategory ? 'selected' : ''} ></option>
                        <c:forEach var="list" items="${categoryList}">
                          <option value="${list.categoryCode}" ${(list.categoryCode == filterCategory) ? 'selected' : ''} >${list.categoryName}</option>
                        </c:forEach>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray"><b>고객사</b></td>
                    <td colspan="3">
                      <select class="form-select" title="filterDownloadClient" id="filterDownloadClient" name="filterClient">
                        <option ${empty filterClient ? 'selected' : ''} ></option>
                        <c:forEach var="list" items="${clientList}">
                          <option value="${list.clientCode}" ${(list.clientCode == filterClient) ? 'selected' : ''} >${list.clientName}</option>
                        </c:forEach>
                      </select>
                    </td>
                  </tr>
                  
                </tbody>
              </table>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary btn-allow-left" onclick="submitCheckedDocuments('DocumentViewDownload');">
              선택 다운로드
            </button>
            <button type="submit" class="btn btn-secondary">필터 다운로드</button>
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
          </div>
        </form>
      </div>
    </div>
  </div>
  


  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/select2.full.min.js"></script>
  <script src="js/document.select.js"></script>
  <script src="js/document.js"></script>
  <script src="js/log.js"></script>
</body>
</html>