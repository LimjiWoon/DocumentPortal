package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		if (user == null || user.getUserCode() != 0) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
		    return;
		} else {
	    	UserDAO userDAO = new UserDAO();
		    ArrayList<UserDTO> list = new ArrayList<UserDTO>();
	    	
		    int startPage = 1;
		    int endPage;
		    int totalPages;
			
	    	totalPages = Math.max(userDAO.maxPage(null, null, null), 1);
	    	
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = userDAO.getList(null, null, null, null);

	        userDAO.userClose();

	        request.setAttribute("startPage", startPage);
	        request.setAttribute("endPage", endPage);
	        request.setAttribute("totalPages", totalPages);
	        request.setAttribute("list", list);
		    request.getRequestDispatcher("User.jsp").forward(request, response);
		}
	}

  

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=UTF-8");
        
    	UserDAO userDAO = new UserDAO();
    	
	    int startPage;
	    int endPage;
	    int totalPages;
	    String nowPage = request.getParameter("page");
	    String dateOfPassword = XSSEscape.changeChangeDate(request.getParameter("dateOfPassword"));
	    String searchField = XSSEscape.changeUserField(request.getParameter("searchField"));
	    String searchOrder = XSSEscape.changeOrder(request.getParameter("searchOrder"));
	    String searchText = XSSEscape.changeText(request.getParameter("searchText"));
	    String isRetire = XSSEscape.changePermisson(request.getParameter("isRetire"));
	    String isLock = XSSEscape.changePermisson(request.getParameter("isLock"));
	    ArrayList<UserDTO> list = new ArrayList<UserDTO>();

	    
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
	    

	    //검색 기록이 있는가 없는 가에 따라 반환하는 사용자 list가 다름
	    if (searchField != null && searchText != null && searchOrder != null){
			totalPages = Math.max(userDAO.maxPage(dateOfPassword, isLock, isRetire, searchField, searchText), 1);
			
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = userDAO.getSearch(dateOfPassword, isLock, isRetire,nowPage, searchField, searchOrder, searchText);
		    
	    } else {
	    	searchField = null;
	    	searchText = null;
	    	searchOrder = null;
	    	totalPages = Math.max(userDAO.maxPage(dateOfPassword, isLock, isRetire), 1);
	    	
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = userDAO.getList(dateOfPassword, isLock, isRetire, nowPage);
	    }


        userDAO.userClose();
        
	    //값 반환
        request.setAttribute("isRetire", isRetire);
        request.setAttribute("isLock", isLock);
        request.setAttribute("dateOfPassword", dateOfPassword);
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
