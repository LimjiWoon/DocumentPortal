<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% request.setCharacterEncoding("UTF-8"); %>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="css/bootstrap.min.css" >
<link rel="stylesheet" href="css/custom.css">
<title>사용자 관리</title>
</head>
<body>
  <c:set var="user" value="${sessionScope.user}" />

  <c:if test="${user == null or user.userCode != 0}">
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
              <a class="nav-link active" aria-current="page" href="Main">홈</a>
            </li>
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" role="button" aria-expanded="false">사용자 관리</a>
              <ul class="dropdown-menu">
                <li>
                  <form method="post" action="User">
                    <input type="submit" class="dropdown-item" value="사용자 조회" />
                  </form>
                </li>
                <li>
                  <form method="post" action="UserUpload">
                    <input type="submit" class="dropdown-item" value="사용자 등록" />
                  </form>
                </li>
              </ul>
            </li>
            <li class="nav-item">
              <form method="post" action="Client">
                <input type="submit" class="nav-link" value="고객사 관리" />
              </form>
            </li>
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown" role="button" aria-expanded="false">문서 관리</a>
              <ul class="dropdown-menu">
                <form method="post" action="Category">
                  <input type="submit" class="dropdown-item" value="문서 목록" />
                </form>
                <form method="post" action="Document">
                  <input type="submit" class="dropdown-item" value="문서 조회" />
                </form>
                <form method="post" action="DocumentUpload">
                  <input type="submit" class="dropdown-item" value="문서 등록" />
                </form>
              </ul>
            </li>
            <li class="nav-item">
              <form method="post" action="Log">
                <input type="submit" class="nav-link active" value="로그" />
              </form>
            </li>
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
        <h1 class="my-2">사용자 관리 페이지</h1>
      </div>
      <div class="col">
        <form class="my-3" method="post" name="search" id="search" action="User?page=1">
          <div class="input-group mb-3 d-f">
            <select class="form-control f-110p" name="searchField" id="searchField" aria-label="searchField" required>
              <option value="" disabled ${empty searchField ? 'selected' : ''}>선택</option>
              <option value="1" ${'1'.equals(searchField) ? 'selected' : ''}>이름</option>
              <option value="2" ${'2'.equals(searchField) ? 'selected' : ''}>사번</option>
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
  
    <div class="row">
      <table class="table table-hover table-dark-line t-c">
        <thead class="table-dark">
          <tr>
            <th scope="col" class="t-c w-5">사번</th>
            <th scope="col" class="t-c w-19">이름</th>
            <th scope="col" class="t-c w-12">고객사(권한)</th>
            <th scope="col" class="t-c w-12">문서 목록(권한)</th>
            <th scope="col" class="t-c w-12">문서(권한)</th>
            <th scope="col" class="t-c w-19">비밀번호 변경일</th>
            <th scope="col" class="t-c w-14">계정 잠금 여부</th>
            <th scope="col" class="t-c w-7"></th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty list}">
              <tr><td colspan="9" rowspan="4"><h1>결과 없음</h1></td></tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="user" items="${list}">
                <tr>
                  <td scope="row">${user.userCode}</td>
                  <td>${user.userName}</td>
                  <td>${user.client ? "O" : "X"}</td>
                  <td>${user.category ? "O" : "X"}</td>
                  <td>${user.document ? "O" : "X"}</td>
                  <td>
                    <c:choose>
                      <c:when test="${empty user.dateOfPassword}">
                        없음
                      </c:when>
                      <c:when test="${user.dateOfPassword == '0'}">
                        금일
                      </c:when>
                      <c:otherwise>
                        ${user.dateOfPassword}일 전
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${user.lock}">
                        <button class="btn btn-outline-danger btn-xs" onclick="changeLock(this, ${user.userCode})">O</button>
                      </c:when>
                      <c:otherwise>
                        <button class="btn btn-outline-success btn-xs" onclick="changeLock(this, ${user.userCode})">X</button>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${isRetire == 0}">
                        <form method="post" action="UserUpdate">
                          <input type="hidden" name="userCode" value="${user.userCode}" />
                          <button type="submit" class="btn btn-outline-dark btn-xs">수정</button>
                        </form>
                      </c:when>
                      <c:otherwise>
                        <form method="post" action="UserRetire" onsubmit="return confirm('정말 복직시키겠습니까?');">
                          <input type="hidden" name="userCode" value="${user.userCode}" />
                          <input type="hidden" name="isRetire" value="0" />
                          <button type="submit" class="btn btn-outline-dark btn-xs">복직</button>
                        </form>
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody> 
      </table>
    </div>
    <div class="row">
      <div class="col t-l w-25">
        <c:choose>
          <c:when test="${isRetire == 0}">
            <form method="post" action="User?page=1" class="d-i">
              <input type="hidden" name="isRetire" value="${isRetire + 1}">
              <input type="hidden" name="ChangeDate" value="${ChangeDate}">
              <button type="submit" class="btn btn-secondary">퇴직자만 보기</button>
            </form>
          </c:when>
          <c:otherwise>
            <form method="post" action="User?page=1" class="d-i">
              <input type="hidden" name="isRetire" value="${isRetire - 1}">
              <input type="hidden" name="ChangeDate" value="${ChangeDate}">
              <button type="submit" class="btn btn-secondary">재직자만 보기</button>
            </form>
          </c:otherwise>
        </c:choose>
      </div>
      <c:choose>
        <c:when test="${not empty searchField and searchText != null and not empty searchOrder}">
          <c:choose>
            <c:when test="${empty list}"></c:when>
            <c:otherwise>
              <nav class="col t-c w-auto" aria-label="Page navigation">
                <ul class="pagination justify-content-center">
                  <c:if test="${totalPages > 5}">
                    <li class="page-item">
                      <form method="post" tabindex="-1" aria-disabled="true" action="User?page=1" class="d-i">
                        <input type="hidden" name="searchField" value="${searchField}">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <input type="hidden" name="isClient" value="${isClient}">
                        <input type="hidden" name="isCategory" value="${isCategory}">
                        <input type="hidden" name="isDocument" value="${isDocument}">
                        <input type="hidden" name="dateOfPassword" value="${dateOfPassword}">
                        <input type="hidden" name="isLock" value="${isLock}">
                        <input type="hidden" name="isRetire" value="${isRetire}">
                        <button type="submit" class="page-link">«</button>
                      </form>
                    </li>
                    <li class="page-item w-55p" >
                      <form method="post" tabindex="-1" aria-disabled="true" action="User?page=${(endPage < 6) ? 1 : startPage - 1}" class="d-i">
                        <input type="hidden" name="searchField" value="${searchField}">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <input type="hidden" name="isClient" value="${isClient}">
                        <input type="hidden" name="isCategory" value="${isCategory}">
                        <input type="hidden" name="isDocument" value="${isDocument}">
                        <input type="hidden" name="dateOfPassword" value="${dateOfPassword}">
                        <input type="hidden" name="isLock" value="${isLock}">
                        <input type="hidden" name="isRetire" value="${isRetire}">
                        <button type="submit" class="page-link">이전</button>
                      </form>
                    </li>
                  </c:if>
                  
                  <c:forEach var="i" begin="${startPage}" end="${endPage}">
                    <c:choose>
                      <c:when test="${i == nowPage or (empty nowPage and i == 1)}">
                        <li class="page-item active">
                          <form method="post" tabindex="-1" aria-disabled="true" action="User?page=${i}" class="d-i">
                            <input type="hidden" name="searchField" value="${searchField}">
                            <input type="hidden" name="searchText" value="${searchText}">
                            <input type="hidden" name="searchOrder" value="${searchOrder}">
                            <input type="hidden" name="isClient" value="${isClient}">
                            <input type="hidden" name="isCategory" value="${isCategory}">
                            <input type="hidden" name="isDocument" value="${isDocument}">
                            <input type="hidden" name="dateOfPassword" value="${dateOfPassword}">
                            <input type="hidden" name="isLock" value="${isLock}">
                            <input type="hidden" name="isRetire" value="${isRetire}">
                            <button type="submit" class="page-link active">${i}</button>
                          </form>
                        </li>
                      </c:when>
                      <c:otherwise>
                        <li class="page-item">
                          <form method="post" tabindex="-1" aria-disabled="true" action="User?page=${i}" class="d-i">
                            <input type="hidden" name="searchField" value="${searchField}">
                            <input type="hidden" name="searchText" value="${searchText}">
                            <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <input type="hidden" name="isClient" value="${isClient}">
                        <input type="hidden" name="isCategory" value="${isCategory}">
                        <input type="hidden" name="isDocument" value="${isDocument}">
                        <input type="hidden" name="dateOfPassword" value="${dateOfPassword}">
                        <input type="hidden" name="isLock" value="${isLock}">
                        <input type="hidden" name="isRetire" value="${isRetire}">
                            <button type="submit" class="page-link">${i}</button>
                          </form>
                        </li>
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>
              
                  <c:if test="${totalPages > 5}">
                    <li class="page-item w-55p" >
                      <form method="post" tabindex="-1" aria-disabled="true" action="User?page=${(endPage == totalPages)?totalPages : endPage + 1}" class="d-i">
                        <input type="hidden" name="searchField" value="${searchField}">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <input type="hidden" name="isClient" value="${isClient}">
                        <input type="hidden" name="isCategory" value="${isCategory}">
                        <input type="hidden" name="isDocument" value="${isDocument}">
                        <input type="hidden" name="dateOfPassword" value="${dateOfPassword}">
                        <input type="hidden" name="isLock" value="${isLock}">
                        <input type="hidden" name="isRetire" value="${isRetire}">
                        <button type="submit" class="page-link">다음</button>
                      </form>
                    </li>

                    <li class="page-item">
                      <form method="post" tabindex="-1" aria-disabled="true" action="User?page=${totalPages}" class="d-i">
                        <input type="hidden" name="searchField" value="${searchField}">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <input type="hidden" name="isClient" value="${isClient}">
                        <input type="hidden" name="isCategory" value="${isCategory}">
                        <input type="hidden" name="isDocument" value="${isDocument}">
                        <input type="hidden" name="dateOfPassword" value="${dateOfPassword}">
                        <input type="hidden" name="isLock" value="${isLock}">
                        <input type="hidden" name="isRetire" value="${isRetire}">
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
                  <form method="post" action="User?page=1"  class="d-i"">
                    <input type="hidden" name="isClient" value="${isClient}">
                    <input type="hidden" name="isCategory" value="${isCategory}">
                    <input type="hidden" name="isDocument" value="${isDocument}">
                    <input type="hidden" name="dateOfPassword" value="${dateOfPassword}">
                    <input type="hidden" name="isLock" value="${isLock}">
                    <input type="hidden" name="isRetire" value="${isRetire}">
                    <button type="submit" class="page-link">«</button>
                  </form>
                </li>
                <li class="page-item w-55p" >
                  <form method="post" action="User?page=${(endPage < 6) ? 1 : startPage - 1}" class="d-i">
                    <input type="hidden" name="isClient" value="${isClient}">
                    <input type="hidden" name="isCategory" value="${isCategory}">
                    <input type="hidden" name="isDocument" value="${isDocument}">
                    <input type="hidden" name="dateOfPassword" value="${dateOfPassword}">
                    <input type="hidden" name="isLock" value="${isLock}">
                    <input type="hidden" name="isRetire" value="${isRetire}">
                    <button type="submit" class="page-link">이전</button>
                  </form>
                </li>
              </c:if>
    
              <c:forEach var="i" begin="${startPage}" end="${endPage}">
                <c:choose>
                  <c:when test="${i == nowPage or (empty nowPage and i == 1)}">
                    <li class="page-item active">
                      <form method="post" action="User?page=${i}" class="d-i">
                        <input type="hidden" name="isClient" value="${isClient}">
                        <input type="hidden" name="isCategory" value="${isCategory}">
                        <input type="hidden" name="isDocument" value="${isDocument}">
                        <input type="hidden" name="dateOfPassword" value="${dateOfPassword}">
                        <input type="hidden" name="isLock" value="${isLock}">
                        <input type="hidden" name="isRetire" value="${isRetire}">
                        <button type="submit" class="page-link active">${i}</button>
                      </form>
                    </li>
                  </c:when>
                  <c:otherwise>
                    <li class="page-item">
                      <form method="post" action="User?page=${i}" class="d-i">
                        <input type="hidden" name="isClient" value="${isClient}">
                        <input type="hidden" name="isCategory" value="${isCategory}">
                        <input type="hidden" name="isDocument" value="${isDocument}">
                        <input type="hidden" name="dateOfPassword" value="${dateOfPassword}">
                        <input type="hidden" name="isLock" value="${isLock}">
                        <input type="hidden" name="isRetire" value="${isRetire}">
                        <button type="submit" class="page-link">${i}</button>
                      </form>
                    </li>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
                
              <c:if test="${totalPages > 5}">
                <li class="page-item w-55p">
                  <form method="post" action="User?page=${(endPage == totalPages)?totalPages : endPage + 1}" class="d-i">
                    <input type="hidden" name="isClient" value="${isClient}">
                    <input type="hidden" name="isCategory" value="${isCategory}">
                    <input type="hidden" name="isDocument" value="${isDocument}">
                    <input type="hidden" name="dateOfPassword" value="${dateOfPassword}">
                    <input type="hidden" name="isLock" value="${isLock}">
                    <input type="hidden" name="isRetire" value="${isRetire}">
                    <button type="submit" class="page-link">다음</button>
                  </form>
                </li>
                <li class="page-item">
                  <form method="post" action="User?page=${totalPages}" class="d-i">
                    <input type="hidden" name="isClient" value="${isClient}">
                    <input type="hidden" name="isCategory" value="${isCategory}">
                    <input type="hidden" name="isDocument" value="${isDocument}">
                    <input type="hidden" name="dateOfPassword" value="${dateOfPassword}">
                    <input type="hidden" name="isLock" value="${isLock}">
                    <input type="hidden" name="isRetire" value="${isRetire}">
                    <button type="submit" class="page-link">»</button>
                  </form>
                </li>
              </c:if>
            </ul>
          </nav>
        </c:otherwise>
      </c:choose>
      <div class="col t-r w-25">
        <form method="post" action="UserUpload">
          <input type="submit" class="btn btn-dark btn-allow-left" value="등록" />
        </form>
      </div>
    </div>
  </div>
  
  
  
  
  <div class="modal fade" id="SearchFilterModal" data-bs-keyboard="false" tabindex="-1" aria-labelledby="SearchFilterModallabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h4 class="modal-title" id="SearchFilterModallabel">검색 필터</h4>
        </div>
        <form id="UserFilter" method="post" name="UserFilter" action="User">
          <div class="modal-body">
            <div class="container">
              <table class="table table-dark-line t-c custom-table">
                <tbody>
                  <tr>
                    <td class="bg-gray">
                      <select class="form-control select-gray-custom" name="searchField" id="searchField" aria-label="searchField">
                        <option value="" disabled ${empty searchField ? 'selected' : ''}>선택</option>
                        <option value="1" ${'1'.equals(searchField) ? 'selected' : ''}>코드</option>
                        <option value="2" ${'2'.equals(searchField) ? 'selected' : ''}>고객사명</option>
                        <option value="3" ${'3'.equals(searchField) ? 'selected' : ''}>작성자</option>
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
                    <td class="bg-gray"><b>고객사</b></td>
                    <td colspan="3">
                      <div class="btn-group w-100" role="group" >
                        <input type="radio" class="btn-check" name="isClient" id="isClientNone" ${empty isUse ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isClientNone">미선택</label>
                        <input type="radio" class="btn-check" name="isClient" id="isClientO" value="1" ${'1'.equals(isUse) ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isClientO">권한 O</label>
                        <input type="radio" class="btn-check" name="isClient" id="isClientX"  value="0" ${'0'.equals(isUse) ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isClientX">권한 X</label>
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray"><b>문서 목록</b></td>
                    <td colspan="3">
                      <div class="btn-group w-100" role="group" >
                        <input type="radio" class="btn-check" name="isCategory" id="isCategoryNone" ${empty isUse ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isCategoryNone">미선택</label>
                        <input type="radio" class="btn-check" name="isCategory" id="isCategoryO" value="1" ${'1'.equals(isUse) ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isCategoryO">권한 O</label>
                        <input type="radio" class="btn-check" name="isCategory" id="isCategoryX"  value="0" ${'0'.equals(isUse) ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isCategoryX">권한 X</label>
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray"><b>문서</b></td>
                    <td colspan="3">
                      <div class="btn-group w-100" role="group" >
                        <input type="radio" class="btn-check" name="isDocument" id="isDocumentNone" ${empty isUse ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isDocumentNone">미선택</label>
                        <input type="radio" class="btn-check" name="isDocument" id="isDocumentO" value="1" ${'1'.equals(isUse) ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isDocumentO">권한 O</label>
                        <input type="radio" class="btn-check" name="isDocument" id="isDocumentX"  value="0" ${'0'.equals(isUse) ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isDocumentX">권한 X</label>
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray"><b>비밀번호 변경일</b></td>
                    <td colspan="3">
                      <select class="form-select" name="dateOfPassword" id="dateOfPassword" aria-label="dateOfPassword">
                        <option value="0" ${empty dateOfPassword ? 'selected' : ''}>비밀번호 변경일</option>
                        <option value="1" ${'1'.equals(dateOfPassword) ? 'selected' : ''}>비밀번호 유효</option>
                        <option value="2" ${'2'.equals(dateOfPassword) ? 'selected' : ''}>비밀번호 변경 필요</option>
                        <option value="3" ${'3'.equals(dateOfPassword) ? 'selected' : ''}>장기 미접속자</option>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray"><b>계정 잠금</b></td>
                    <td colspan="3">
                      <div class="btn-group w-100" role="group" >
                        <input type="radio" class="btn-check" name="isLock" id="isLockNone" ${empty isUse ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isLockNone">미선택</label>
                        <input type="radio" class="btn-check" name="isLock" id="isLockO" value="1" ${'1'.equals(isUse) ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isLockO">권한 O</label>
                        <input type="radio" class="btn-check" name="isLock" id="isLockX"  value="0" ${'0'.equals(isUse) ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isLockX">권한 X</label>
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <td class="bg-gray"><b>퇴직 여부</b></td>
                    <td colspan="3">
                      <div class="btn-group w-100" role="group" >
                        <input type="radio" class="btn-check" name="isRetire" id="isRetireNone" ${empty isUse ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isRetireNone">미선택</label>
                        <input type="radio" class="btn-check" name="isRetire" id="isRetireO" value="1" ${'1'.equals(isUse) ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isRetireO">퇴직</label>
                        <input type="radio" class="btn-check" name="isRetire" id="isRetireX"  value="0" ${'0'.equals(isUse) ? 'checked' : ''} >
                        <label class="btn btn-outline-dark" for="isRetireX">재직</label>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
              <input type="hidden" name="searchField" value="${searchField}" />
              <input type="hidden" name="searchText" value="${searchText}" />
              <input type="hidden" name="searchOrder" value="${searchOrder}" />
            </div>
          </div>
          <div class="modal-footer">
            <button type="submit" class="btn btn-secondary">적용</button>
            <button type="button" class="btn btn-secondary" onclick=" document.getElementById('reset').submit();">검색/필터 초기화</button>
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
          </div>
        </form>
      </div>
    </div>
  </div>


  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/userlogic.js"></script>
</body>
</html>
