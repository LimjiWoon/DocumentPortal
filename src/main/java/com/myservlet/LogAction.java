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
import com.log.*;

/**
 * Servlet implementation class LogAction
 */
@WebServlet("/Log")
public class LogAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogAction() {
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
		}
		

	    ArrayList<LogDTO> list = new ArrayList<LogDTO>();
	    LogDAO logDAO = new LogDAO();
	    int startPage = 1;
	    int totalPages = Math.max(logDAO.maxPage(null, null, null, null), 1);
	    int endPage = Math.min(startPage + 4, totalPages);

	    if (endPage - startPage < 4) {
	      startPage = Math.max(endPage - 4, 1);
	    }
	    
	    list = logDAO.getList(null, null, null, null, null);
	    
	    logDAO.logClose();

	    //값 반환
        request.setAttribute("startPage", startPage);
        request.setAttribute("endPage", endPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("list", list);
	    request.getRequestDispatcher("Log.jsp").forward(request, response);
	    
	}

  
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
		

        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		if (user == null || user.getUserCode() != 0) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}
        
	    int startPage;
	    int endPage;
	    int totalPages;
	    String nowPage = request.getParameter("page");
	    String startDate = XSSEscape.checkDate(request.getParameter("startDate"));
	    String endDate = XSSEscape.checkDate(request.getParameter("endDate"));
	    String searchField = XSSEscape.changeLogField(request.getParameter("searchField"));
	    String searchText = XSSEscape.changeUserName(request.getParameter("searchText"));
	    String logWhere = XSSEscape.changeLogWhere(request.getParameter("logWhere"));
	    String logHow = XSSEscape.changeLogHow(request.getParameter("logHow"));
	    ArrayList<LogDTO> list = new ArrayList<LogDTO>();
	    LogDAO logDAO = new LogDAO();
        

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
	    
	    
	    
	    if (searchText != null && searchField != null){
	    	totalPages = Math.max(logDAO.maxPage(startDate, endDate, logWhere, logHow, searchField, searchText), 1);
			
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = logDAO.getSearch(startDate, endDate, logWhere, logHow, nowPage, searchField, searchText);
	    } else{
	    	startDate = null;
	    	endDate = null;
	    	searchText = null;
	    	searchField = null;
	    	totalPages = Math.max(logDAO.maxPage(startDate, endDate, logWhere, logHow), 1);
	    	
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = logDAO.getList(startDate, endDate, logWhere, logHow, nowPage);
	    }
	    
	    logDAO.logClose();

	    //값 반환
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("searchText", searchText);
        request.setAttribute("searchField", XSSEscape.restoreLogField(searchField));
        request.setAttribute("startPage", startPage);
        request.setAttribute("nowPage", nowPage);
        request.setAttribute("endPage", endPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("logWhere", XSSEscape.restoreLogWhere(logWhere));
        request.setAttribute("logHow", XSSEscape.restoreLogHow(logHow));
        request.setAttribute("list", list);
	    request.getRequestDispatcher("Log.jsp").forward(request, response);
		
	}

}
