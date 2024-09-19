package com.myservlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.category.*;
import com.myclass.XSSEscape;
import com.user.UserDTO;

/**
 * Servlet implementation class CategoryAction
 */
@WebServlet("/Category")
public class CategoryAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CategoryAction() {
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
		if (user == null || !user.isCategory()) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}
        
    	CategoryDAO categoryDAO = new CategoryDAO();
	    int startPage = 1;
	    int totalPages = Math.max(categoryDAO.maxPage(null, null), 1);
	    int endPage = Math.min(startPage + 4, totalPages);
	    if (endPage - startPage < 4) {
	      startPage = Math.max(endPage - 4, 1);
	    }
	    
	    ArrayList<CategoryDTO> list = categoryDAO.getList(null, null, null);
	    
	    categoryDAO.categoryClose();
	    
        request.setAttribute("startPage", startPage);
        request.setAttribute("endPage", endPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("list", list);
        
	    request.getRequestDispatcher("Category.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=UTF-8");
        
        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		if (user == null || !user.isCategory()) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}
        
    	CategoryDAO categoryDAO = new CategoryDAO();
	    int startPage;
	    int endPage;
	    int totalPages;
	    String nowPage = request.getParameter("page");
	    String searchField = XSSEscape.changeCategoryField(request.getParameter("searchField"));
	    String searchOrder = XSSEscape.changeOrder(request.getParameter("searchOrder"));
	    String searchText = XSSEscape.changeText(request.getParameter("searchText"));
	    String startDate = XSSEscape.checkDate(request.getParameter("startDate"));
	    String endDate = XSSEscape.checkDate(request.getParameter("endDate"));
	    ArrayList<CategoryDTO> list = new ArrayList<CategoryDTO>();
	    
	    
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
			totalPages = Math.max(categoryDAO.maxPage(startDate, endDate, searchField, searchText), 1);
			
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = categoryDAO.getSearch(startDate, endDate, nowPage, searchField, searchOrder, searchText);
		    
	    } else {
	    	searchField = null;
	    	searchText = null;
	    	searchOrder = null;
	    	totalPages = Math.max(categoryDAO.maxPage(startDate, endDate), 1);
	    	
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = categoryDAO.getList(startDate, endDate, nowPage);
	    }
	    
	    
	    categoryDAO.categoryClose();
	    
	    //값 반환
        request.setAttribute("searchField", XSSEscape.restoreCategoryField(searchField));
        request.setAttribute("searchOrder", XSSEscape.restoreOrder(searchOrder));
        request.setAttribute("searchText", searchText);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("startPage", startPage);
        request.setAttribute("nowPage", nowPage);
        request.setAttribute("endPage", endPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("list", list);
        
	    request.getRequestDispatcher("Category.jsp").forward(request, response);
	}

}
