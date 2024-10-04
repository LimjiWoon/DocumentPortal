package com.myservlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
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
	 * 페이지 로딩을 위한 servlet, 페이지에 보여줄 정보 리스트를 가져와 화면에 띄워준다.
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
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
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
	    ArrayList<ClientDTO> list = new ArrayList<ClientDTO>();
		 
		//현재 페이지, 최대 페이지 수, 이동할 수 있는 페이지 범위를 계산한다.
	    if (nowPage == null){
	      startPage = 1;
	    } else {
	      try {
	        startPage = Math.max(Integer.parseInt(nowPage) - 2, 1);
	      } catch (NumberFormatException e) {
	        clientDAO.errorLogUpload(e);
	        startPage = 1;
	        nowPage = null;
	      }
	    }
	    
	    //검색이 올바른지 아닌지를 확인한다.
	    if (searchField == null || searchText == null || searchOrder == null){
	    	searchField = null;
	    	searchText = null;
	    	searchOrder = null;
	    }
	    
	    totalPages = Math.max(clientDAO.maxPage(startDate, endDate, isUse, searchField, searchText), 1);
	    endPage = Math.min(startPage + 4, totalPages);

	    if (endPage - startPage < 4) {
	      startPage = Math.max(endPage - 4, 1);
	    }
	    
	    //위 정보들로 화면에 출력할 리스트 정보를 가져온다.
	    list = clientDAO.getList(startDate, endDate, isUse, nowPage, searchField, searchOrder, searchText);
	    
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
        
	    request.getRequestDispatcher("WEB-INF/Client.jsp").forward(request, response);
    	
	}

  
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
