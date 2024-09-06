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
            <button type="submit" class="btn btn-outline-secondary">검색</button>
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
            <th scope="col" class="t-c w-19">
              <form method="post" action="User">
                <select class="select-dark-custom" name="ChangeDate" id="ChangeDate" aria-label="ChangeDate" onchange="this.form.submit()">
                  <option value="0" ${empty ChangeDate ? 'selected' : ''}>비밀번호 변경일</option>
                  <option value="1" ${'1'.equals(ChangeDate) ? 'selected' : ''}>비밀번호 유효</option>
                  <option value="2" ${'2'.equals(ChangeDate) ? 'selected' : ''}>비밀번호 변경 필요</option>
                  <option value="3" ${'3'.equals(ChangeDate) ? 'selected' : ''}>장기 미접속자</option>
                </select>
                <input type="hidden" name="searchField" value="${searchField}">
                <input type="hidden" name="searchText" value="${searchText}">
                <input type="hidden" name="searchOrder" value="${searchOrder}">
                <input type="hidden" name="isRetire" value="${isRetire}">
              </form>
            </th>
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
      <c:choose>
        <c:when test="${not empty searchField and searchText != null and not empty searchOrder}">
          <div class="col t-l w-25">
            <c:choose>
              <c:when test="${isRetire == 0}">
                <form method="post" action="User?page=1" class="d-i">
                  <input type="hidden" name="searchField" value="${searchField}">
                  <input type="hidden" name="searchText" value="${searchText}">
                  <input type="hidden" name="searchOrder" value="${searchOrder}">
                  <input type="hidden" name="isRetire" value="${isRetire + 1}">
                  <input type="hidden" name="ChangeDate" value="${ChangeDate}">
                  <button type="submit" class="btn btn-secondary">퇴직자만 보기</button>
                </form>
              </c:when>
              <c:otherwise>
                <form method="post" action="User?page=1" class="d-i">
                  <input type="hidden" name="searchField" value="${searchField}">
                  <input type="hidden" name="searchText" value="${searchText}">
                  <input type="hidden" name="searchOrder" value="${searchOrder}">
                  <input type="hidden" name="isRetire" value="${isRetire - 1}">
                  <input type="hidden" name="ChangeDate" value="${ChangeDate}">
                  <button type="submit" class="btn btn-secondary">재직자만 보기</button>
                </form>
              </c:otherwise>
            </c:choose>
          </div>
          
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
                        <input type="hidden" name="isRetire" value="${isRetire}">
                        <input type="hidden" name="ChangeDate" value="${ChangeDate}">
                        <button type="submit" class="page-link">«</button>
                      </form>
                    </li>
                    <li class="page-item w-55p" >
                      <form method="post" tabindex="-1" aria-disabled="true" action="User?page=${(endPage < 6) ? 1 : startPage - 1}" class="d-i">
                        <input type="hidden" name="searchField" value="${searchField}">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <input type="hidden" name="isRetire" value="${isRetire}">
                        <input type="hidden" name="ChangeDate" value="${ChangeDate}">
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
                            <input type="hidden" name="isRetire" value="${isRetire}">
                            <input type="hidden" name="ChangeDate" value="${ChangeDate}">
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
                            <input type="hidden" name="isRetire" value="${isRetire}">
                            <input type="hidden" name="ChangeDate" value="${ChangeDate}">
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
                        <input type="hidden" name="isRetire" value="${isRetire}">
                        <input type="hidden" name="ChangeDate" value="${ChangeDate}">
                        <button type="submit" class="page-link">다음</button>
                      </form>
                    </li>

                    <li class="page-item">
                      <form method="post" tabindex="-1" aria-disabled="true" action="User?page=${totalPages}" class="d-i">
                        <input type="hidden" name="searchField" value="${searchField}">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="searchOrder" value="${searchOrder}">
                        <input type="hidden" name="isRetire" value="${isRetire}">
                        <input type="hidden" name="ChangeDate" value="${ChangeDate}">
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
          <div class="col t-l w-20">
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
  
          <nav class="col t-c w-80" aria-label="Page navigation">
            <ul class="pagination justify-content-center">
              <c:if test="${totalPages > 5}">
                <li class="page-item">
                  <form method="post" action="User?page=1"  class="d-i"">
                    <input type="hidden" name="isRetire" value="${isRetire}">
                    <input type="hidden" name="ChangeDate" value="${ChangeDate}">
                    <button type="submit" class="page-link">«</button>
                  </form>
                </li>
                <li class="page-item w-55p" >
                  <form method="post" action="User?page=${(endPage < 6) ? 1 : startPage - 1}" class="d-i">
                    <input type="hidden" name="isRetire" value="${isRetire}">
                    <input type="hidden" name="ChangeDate" value="${ChangeDate}">
                    <button type="submit" class="page-link">이전</button>
                  </form>
                </li>
              </c:if>
    
              <c:forEach var="i" begin="${startPage}" end="${endPage}">
                <c:choose>
                  <c:when test="${i == nowPage or (empty nowPage and i == 1)}">
                    <li class="page-item active">
                      <form method="post" action="User?page=${i}" class="d-i">
                        <input type="hidden" name="isRetire" value="${isRetire}">
                        <input type="hidden" name="ChangeDate" value="${ChangeDate}">
                        <button type="submit" class="page-link active">${i}</button>
                      </form>
                    </li>
                  </c:when>
                  <c:otherwise>
                    <li class="page-item">
                      <form method="post" action="User?page=${i}" class="d-i">
                        <input type="hidden" name="isRetire" value="${isRetire}">
                        <input type="hidden" name="ChangeDate" value="${ChangeDate}">
                        <button type="submit" class="page-link">${i}</button>
                      </form>
                    </li>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
                
              <c:if test="${totalPages > 5}">
                <li class="page-item w-55p">
                  <form method="post" action="User?page=${(endPage == totalPages)?totalPages : endPage + 1}" class="d-i">
                    <input type="hidden" name="isRetire" value="${isRetire}">
                    <input type="hidden" name="ChangeDate" value="${ChangeDate}">
                    <button type="submit" class="page-link">다음</button>
                  </form>
                </li>
                <li class="page-item">
                  <form method="post" action="User?page=${totalPages}" class="d-i">
                    <input type="hidden" name="isRetire" value="${isRetire}">
                    <input type="hidden" name="ChangeDate" value="${ChangeDate}">
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
    


  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/userlogic.js"></script>
</body>
</html>
