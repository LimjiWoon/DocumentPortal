package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.user.*;
import java.util.ArrayList;
import com.myclass.XSSEscape;

/**
 * Servlet implementation class UserAction
 */
@WebServlet("/User")
public class UserAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserAction() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=UTF-8");
        
    	UserDAO userDAO = new UserDAO();
    	
	    int isRetire = 0;
	    int startPage;
	    int endPage;
	    int totalPages;
	    String nowPage = request.getParameter("page");
	    String searchField = XSSEscape.changeUserField(request.getParameter("searchField"));
	    String ChangeDate = XSSEscape.changeChangeDate(request.getParameter("ChangeDate"));
	    String searchOrder = XSSEscape.changeOrder(request.getParameter("searchOrder"));
	    String searchText = XSSEscape.changeText(request.getParameter("searchText"));
	    ArrayList<UserDTO> list = new ArrayList<UserDTO>();

	    //isRetire XSS 검증 및 값 처리
	    if("1".equals(request.getParameter("isRetire"))) {
	    	isRetire = 1;
	    	if(request.getParameter("userCode") != null) 
	    		isRetire = 0;
	    } else {
	    	isRetire = 0;
	    	if(request.getParameter("userCode") != null && request.getAttribute("message") == null)
	    		isRetire = 1;
	    }
	    
		//nowPage XSS 검증 및 값 처리
		//startPage, endPage, totalPages 값 계산
		//위의 검증 된 값들로 사용자 list, DB에서 가져오기
	    if (nowPage == null){
	      startPage = 1;
	    } else {
	      try {
	        startPage = Math.max(Integer.parseInt(nowPage) - 2, 1);
	      } catch (NumberFormatException e) {
	        startPage = 1;
	        nowPage = null;
	      }
	    }
	    
	    //비밀번호 변경일 필터에 따른 출력
	    //아래는 필터 X 버전의 출력
	    if ("0".equals(ChangeDate)) {
		    //검색 기록이 있는가 없는 가에 따라 반환하는 사용자 list가 다름
		    if (searchField != null && searchText != null && searchOrder != null){
				totalPages = Math.max(userDAO.maxPage(isRetire, searchField, searchText), 1);
				
			    endPage = Math.min(startPage + 4, totalPages);

			    if (endPage - startPage < 4) {
			      startPage = Math.max(endPage - 4, 1);
			    }
			    
			    list = userDAO.getSearch(nowPage, isRetire, searchField, searchOrder, searchText);
			    
		    } else {
		    	searchField = null;
		    	searchText = null;
		    	searchOrder = null;
		    	totalPages = Math.max(userDAO.maxPage(isRetire), 1);
		    	
			    endPage = Math.min(startPage + 4, totalPages);

			    if (endPage - startPage < 4) {
			      startPage = Math.max(endPage - 4, 1);
			    }
			    
			    list = userDAO.getList(nowPage, isRetire);
		    }
	    } //비밀번호 변경일 필터에 따른 추력 
	    else {
		    //검색 기록이 있는가 없는 가에 따라 반환하는 사용자 list가 다름
		    if (searchField != null && searchText != null && searchOrder != null){
				totalPages = Math.max(userDAO.maxPage(isRetire, ChangeDate, searchField, searchText), 1);
				
			    endPage = Math.min(startPage + 4, totalPages);

			    if (endPage - startPage < 4) {
			      startPage = Math.max(endPage - 4, 1);
			    }
			    
			    list = userDAO.getSearch(nowPage, isRetire, ChangeDate, searchField, searchOrder, searchText);
			    
		    } else {
		    	searchField = null;
		    	searchText = null;
		    	searchOrder = null;
		    	totalPages = Math.max(userDAO.maxPage(isRetire, ChangeDate), 1);
		    	
			    endPage = Math.min(startPage + 4, totalPages);

			    if (endPage - startPage < 4) {
			      startPage = Math.max(endPage - 4, 1);
			    }
			    
			    list = userDAO.getList(nowPage, isRetire, ChangeDate);
		    }
	    }


	    //값 반환
        request.setAttribute("isRetire", isRetire);
        request.setAttribute("ChangeDate", ChangeDate);
        request.setAttribute("searchField", XSSEscape.restoreUserField(searchField));
        request.setAttribute("searchOrder", XSSEscape.restoreOrder(searchOrder));
        request.setAttribute("searchText", searchText);
        request.setAttribute("startPage", startPage);
        request.setAttribute("nowPage", nowPage);
        request.setAttribute("endPage", endPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("list", list);
        
	    request.getRequestDispatcher("User.jsp").forward(request, response);
        
	    
	}

}
