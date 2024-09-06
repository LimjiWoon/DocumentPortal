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
  <link rel="stylesheet" href="css/bootstrap.min.css">
  <link rel="stylesheet" href="css/custom.css">
<title>로그 페이지</title>
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
        <h1 class="my-2">로그 페이지</h1>
      </div>
      <div class="col">
        <form class="my-3" method="post" name="search" id="search" action="Log?page=1">
          <div class="input-group mb-3 d-f">
            <input type="text" class="form-control f-1" name="searchText" id="searchText" value="${searchText}" placeholder="사번 및 이름" >
            <input type="date" class="form-control f-140p" id="startDate" name="startDate"
            value="${startDate}" min="2009-01-01" max="2039-12-31" />
            <span class="input-group-text">~</span>
            <input type="date" class="form-control f-140p" id="endDate" name="endDate"
            value="${endDate}" min="2009-01-01" max="2039-12-31" />
            <button type="submit" class="form-control f-90p btn btn-outline-secondary">조회</button>
          </div>
        </form>
      </div>
    </div>
    
    <div class="row">
      <table class="table table-hover table-dark-line t-c">
        <thead class="table-dark">
          <tr>
            <th scope="col" class="t-c w-5">사번</th>
            <th scope="col" class="t-c w-7">사용자</th>
            <th scope="col" class="t-c w-7">
              <form method="post" action="Log">
                <select class="select-dark-custom" name="logWhere" id="logWhere" aria-label="logWhere" onchange="this.form.submit()">
                  <option ${empty logWhere ? 'selected' : ''}>페이지</option>
                  <option value="1" ${'1'.equals(logWhere) ? 'selected' : ''}>고객사</option>
                  <option value="2" ${'2'.equals(logWhere) ? 'selected' : ''}>문서 목록</option>
                  <option value="3" ${'3'.equals(logWhere) ? 'selected' : ''}>문서</option>
                </select>
                <input type="hidden" name="searchText" value="${searchText}">
                <input type="hidden" name="startDate" value="${startDate}">
                <input type="hidden" name="endDate" value="${endDate}">
                <input type="hidden" name="logHow" value="${logHow}">
              </form>
            </th>
            <th scope="col" class="t-c w-25">시간</th>
            <th scope="col" class="t-c w-12">대상</th>
            <th scope="col" class="t-c w-7">
              <form method="post" action="Log">
                <select class="select-dark-custom" name="logHow" id="logHow" aria-label="logHow" onchange="this.form.submit()">
                  <option ${empty logHow ? 'selected' : ''}>행동</option>
                  <option value="1" ${'1'.equals(logHow) ? 'selected' : ''}>등록</option>
                  <option value="2" ${'2'.equals(logHow) ? 'selected' : ''}>갱신</option>
                  <option value="3" ${'3'.equals(logHow) ? 'selected' : ''}>삭제</option>
                </select>
                <input type="hidden" name="searchText" value="${searchText}">
                <input type="hidden" name="startDate" value="${startDate}">
                <input type="hidden" name="endDate" value="${endDate}">
                <input type="hidden" name="logWhere" value="${logWhere}">
              </form>
            </th>
            <th scope="col" class="t-c w-auto">설명</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty list}">
              <tr><td colspan="9" rowspan="4"><h1>결과 없음</h1></td></tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="log" items="${list}">
                <tr>
                  <td scope="row">${log.logWho}</td>
                  <td>${log.logWhoName}</td>
                  <td>${log.logWhere}</td>
                  <td>${log.logWhen}</td>
                  <td>${log.logWhat}</td>
                  <td>${log.logHow}</td>
                  <td>${log.logWhy}</td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody> 
      </table>
    </div>
	

    <div class="row">
      <c:choose>
        <c:when test="${not empty startDate and not empty endDate}"> 
          <c:choose>
            <c:when test="${empty list}"></c:when>
            <c:otherwise>
              <div class="col t-l w-20"></div>
              <nav class="col t-c w-80" aria-label="Page navigation">
                <ul class="pagination justify-content-center">
                  <c:if test="${totalPages > 5}">
                    <li class="page-item">
                      <form method="post" tabindex="-1" aria-disabled="true" action="Log?page=1" class="d-i">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="startDate" value="${startDate}">
                        <input type="hidden" name="endDate" value="${endDate}">
                        <input type="hidden" name="logWhere" value="${logWhere}">
                        <input type="hidden" name="logHow" value="${logHow}">
                        <button type="submit" class="page-link">«</button>
                      </form>
                    </li>
                    <li class="page-item w-55p" >
                      <form method="post" tabindex="-1" aria-disabled="true" action="Log?page=${(endPage < 6) ? 1 : startPage - 1}" class="d-i">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="startDate" value="${startDate}">
                        <input type="hidden" name="endDate" value="${endDate}">
                        <input type="hidden" name="logWhere" value="${logWhere}">
                        <input type="hidden" name="logHow" value="${logHow}">
                        <button type="submit" class="page-link">이전</button>
                      </form>
                    </li>
                  </c:if>
                  
                  <c:forEach var="i" begin="${startPage}" end="${endPage}">
                    <c:choose>
                      <c:when test="${i == nowPage or (empty nowPage and i == 1)}">
                        <li class="page-item active">
                          <form method="post" tabindex="-1" aria-disabled="true" action="Log?page=${i}" class="d-i">
                            <input type="hidden" name="searchText" value="${searchText}">
                            <input type="hidden" name="startDate" value="${startDate}">
                            <input type="hidden" name="endDate" value="${endDate}">
                            <input type="hidden" name="logWhere" value="${logWhere}">
                            <input type="hidden" name="logHow" value="${logHow}">
                            <button type="submit" class="page-link active">${i}</button>
                          </form>
                        </li>
                      </c:when>
                      <c:otherwise>
                        <li class="page-item">
                          <form method="post" tabindex="-1" aria-disabled="true" action="Log?page=${i}" class="d-i">
                            <input type="hidden" name="searchText" value="${searchText}">
                            <input type="hidden" name="startDate" value="${startDate}">
                            <input type="hidden" name="endDate" value="${endDate}">
                            <input type="hidden" name="logWhere" value="${logWhere}">
                            <input type="hidden" name="logHow" value="${logHow}">
                            <button type="submit" class="page-link">${i}</button>
                          </form>
                        </li>
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>
              
                  <c:if test="${totalPages > 5}">
                    <li class="page-item w-55p" >
                      <form method="post" tabindex="-1" aria-disabled="true" action="Log?page=${(endPage == totalPages)?totalPages : endPage + 1}" class="d-i">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="startDate" value="${startDate}">
                        <input type="hidden" name="endDate" value="${endDate}">
                        <input type="hidden" name="logWhere" value="${logWhere}">
                        <input type="hidden" name="logHow" value="${logHow}">
                        <button type="submit" class="page-link">다음</button>
                      </form>
                    </li>

                    <li class="page-item">
                      <form method="post" tabindex="-1" aria-disabled="true" action="Log?page=${totalPages}" class="d-i">
                        <input type="hidden" name="searchText" value="${searchText}">
                        <input type="hidden" name="startDate" value="${startDate}">
                        <input type="hidden" name="endDate" value="${endDate}">
                        <input type="hidden" name="logWhere" value="${logWhere}">
                        <input type="hidden" name="logHow" value="${logHow}">
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
          <div class="col t-l w-20"></div>
  
          <nav class="col t-c w-80" aria-label="Page navigation">
            <ul class="pagination justify-content-center">
              <c:if test="${totalPages > 5}">
                <li class="page-item">
                  <form method="post" action="Log?page=1"  class="d-i"">
                    <input type="hidden" name="logWhere" value="${logWhere}">
                    <input type="hidden" name="logHow" value="${logHow}">
                    <button type="submit" class="page-link">«</button>
                  </form>
                </li>
                <li class="page-item w-55p" >
                  <form method="post" action="Log?page=${(endPage < 6) ? 1 : startPage - 1}" class="d-i">
                    <input type="hidden" name="logWhere" value="${logWhere}">
                    <input type="hidden" name="logHow" value="${logHow}">
                    <button type="submit" class="page-link">이전</button>
                  </form>
                </li>
              </c:if>
    
              <c:forEach var="i" begin="${startPage}" end="${endPage}">
                <c:choose>
                  <c:when test="${i == nowPage or (empty nowPage and i == 1)}">
                    <li class="page-item active">
                      <form method="post" action="Log?page=${i}" class="d-i">
                        <input type="hidden" name="logWhere" value="${logWhere}">
                        <input type="hidden" name="logHow" value="${logHow}">
                        <button type="submit" class="page-link active">${i}</button>
                      </form>
                    </li>
                  </c:when>
                  <c:otherwise>
                    <li class="page-item">
                      <form method="post" action="Log?page=${i}" class="d-i">
                        <input type="hidden" name="logWhere" value="${logWhere}">
                        <input type="hidden" name="logHow" value="${logHow}">
                        <button type="submit" class="page-link">${i}</button>
                      </form>
                    </li>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
                
              <c:if test="${totalPages > 5}">
                <li class="page-item w-55p">
                  <form method="post" action="Log?page=${(endPage == totalPages)?totalPages : endPage + 1}" class="d-i">
                    <input type="hidden" name="logWhere" value="${logWhere}">
                    <input type="hidden" name="logHow" value="${logHow}">
                    <button type="submit" class="page-link">다음</button>
                  </form>
                </li>
                <li class="page-item">
                  <form method="post" action="Log?page=${totalPages}" class="d-i">
                    <input type="hidden" name="logWhere" value="${logWhere}">
                    <input type="hidden" name="logHow" value="${logHow}">
                    <button type="submit" class="page-link">»</button>
                  </form>
                </li>
              </c:if>
            </ul>
          </nav>
        </c:otherwise>
      </c:choose>
      <div class="col t-r w-20"></div>
    </div>
  </div>



  <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
  <script src="js/bootstrap.bundle.min.js"></script>
  <script src="js/log.js"></script>
</body>
</html>