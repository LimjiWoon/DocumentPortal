package com.myservlet;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myclass.XSSEscape;
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
		
	    int startPage;
	    int endPage;
	    int totalPages;
	    String nowPage = request.getParameter("page");
	    String startDate = XSSEscape.checkDate(request.getParameter("startDate"));
	    String endDate = XSSEscape.checkDate(request.getParameter("endDate"));
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
	    
	    
	    //검색 했을 때의 로딩
	    if (searchText != null && startDate != null && endDate != null){
	    	totalPages = Math.max(logDAO.maxPage(searchText, startDate, endDate, logWhere, logHow), 1);
			
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = logDAO.getSearch(nowPage, searchText, startDate, endDate, logWhere, logHow);
	    } //날짜만 필터링 할 경우
	    else if (searchText == null && startDate != null && endDate != null){
			totalPages = Math.max(logDAO.maxPage(startDate, endDate, logWhere, logHow), 1);
			
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = logDAO.getSearch(nowPage, startDate, endDate, logWhere, logHow);
	    } //전체 리스트 반환
	    else{
	    	startDate = null;
	    	endDate = null;
	    	searchText = null;
	    	totalPages = Math.max(logDAO.maxPage(logWhere, logHow), 1);
	    	
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = logDAO.getList(nowPage, logWhere, logHow);
	    }


	    //값 반환
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("searchText", searchText);
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
