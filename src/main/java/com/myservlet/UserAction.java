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
	 * 페이지 로딩을 위한 servlet, 페이지에 보여줄 정보 리스트를 가져와 화면에 띄워준다.
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
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
		    return;
		}
		
    	
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
    	UserDAO userDAO = new UserDAO();
	    ArrayList<UserDTO> list = new ArrayList<UserDTO>();
		
		//실제 페이지 계산
		if (nowPage == null){
		  startPage = 1;
		} else {
		  try {
		    startPage = Math.max(Integer.parseInt(nowPage) - 2, 1);
		  } catch (NumberFormatException e) {
			userDAO.errorLogUpload(e);
		    startPage = 1;
		    nowPage = null;
		  }
		}
		
		
		//검색 입력값 검증
		if (searchField == null || searchText == null || searchOrder == null){
			searchField = null;
			searchText = null;
			searchOrder = null;
			
		}
		
		totalPages = Math.max(userDAO.maxPage(dateOfPassword, isLock, isRetire, searchField, searchText), 1);
		endPage = Math.min(startPage + 4, totalPages);
		
		if (endPage - startPage < 4) {
		  startPage = Math.max(endPage - 4, 1);
		}
		
		//화면에 출력할 리스트 획득
		list = userDAO.getList(dateOfPassword, isLock, isRetire,nowPage, searchField, searchOrder, searchText);
		
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
		  
		request.getRequestDispatcher("WEB-INF/User.jsp").forward(request, response);
	}

  

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
