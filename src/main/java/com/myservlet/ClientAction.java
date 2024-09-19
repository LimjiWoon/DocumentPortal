package com.myservlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.myclass.XSSEscape;
import com.user.UserDTO;
import com.client.*;

/**
 * Servlet implementation class ClientAction
 */
@WebServlet("/Client")
public class ClientAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClientAction() {
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
		if (user == null || !user.isClient()) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}
        

	    int startPage = 1;
	    int endPage;
	    int totalPages;
    	ClientDAO clientDAO = new ClientDAO();
	    ArrayList<ClientDTO> list = new ArrayList<ClientDTO>();
    	
    	totalPages = Math.max(clientDAO.maxPage(null, null, null), 1);
	    endPage = Math.min(startPage + 4, totalPages);

	    if (endPage - startPage < 4) {
	      startPage = Math.max(endPage - 4, 1);
	    }
	    
	    list = clientDAO.getList(null, null, null, null);

        request.setAttribute("startPage", startPage);
        request.setAttribute("endPage", endPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("list", list);
	    request.getRequestDispatcher("Client.jsp").forward(request, response);
    	
	}

  

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=UTF-8");
        

        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		if (user == null || !user.isClient()) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}
        
    	ClientDAO clientDAO = new ClientDAO();

	    int startPage;
	    int endPage;
	    int totalPages;
	    String nowPage = request.getParameter("page");
	    String searchField = XSSEscape.changeClientField(request.getParameter("searchField"));
	    String searchOrder = XSSEscape.changeOrder(request.getParameter("searchOrder"));
	    String searchText = XSSEscape.changeText(request.getParameter("searchText"));
	    String startDate = XSSEscape.checkDate(request.getParameter("startDate"));
	    String endDate = XSSEscape.checkDate(request.getParameter("endDate"));
	    String isUse = XSSEscape.changePermisson(request.getParameter("isUse"));
	    String message = (String) request.getSession().getAttribute("message");
	    ArrayList<ClientDTO> list = new ArrayList<ClientDTO>();
	    
		 // 메시지가 있으면 request에 저장하고 세션에서 제거
		 if (message != null) {
		     request.setAttribute("message", message);
		     request.getSession().removeAttribute("message");
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
	    
	    //검색 기록이 있는가 없는 가에 따라 반환하는 사용자 list가 다름
	    if (searchField != null && searchText != null && searchOrder != null){
			totalPages = Math.max(clientDAO.maxPage(startDate, endDate, isUse, searchField, searchText), 1);
			
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = clientDAO.getSearch(startDate, endDate, isUse, nowPage, searchField, searchOrder, searchText);
		    
	    } else {
	    	searchField = null;
	    	searchText = null;
	    	searchOrder = null;
	    	totalPages = Math.max(clientDAO.maxPage(startDate, endDate, isUse), 1);
	    	
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = clientDAO.getList(startDate, endDate, isUse, nowPage);
	    }
	    
	    clientDAO.clientClose();

	    //값 반환
        request.setAttribute("searchField", XSSEscape.restoreClientField(searchField));
        request.setAttribute("searchOrder", XSSEscape.restoreOrder(searchOrder));
        request.setAttribute("searchText", searchText);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("isUse", isUse);
        request.setAttribute("startPage", startPage);
        request.setAttribute("nowPage", nowPage);
        request.setAttribute("endPage", endPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("list", list);
        
	    request.getRequestDispatcher("Client.jsp").forward(request, response);
	}

}
