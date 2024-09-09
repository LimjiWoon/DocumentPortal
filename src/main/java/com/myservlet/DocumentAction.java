package com.myservlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.document.*;
import com.category.*;
import com.client.*;
import com.myclass.XSSEscape;
import com.user.UserDTO;

/**
 * Servlet implementation class DocumentAction
 */
@WebServlet("/Document")
public class DocumentAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentAction() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=UTF-8");
        
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		
		//권한을 검증하는 블럭
		if (user == null || !user.isDocument()) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}	

    	DocumentDAO documentDAO = new DocumentDAO();
    	CategoryDAO categoryDAO = new CategoryDAO();
    	ClientDAO clientDAO = new ClientDAO();

	    int startPage;
	    int endPage;
	    int totalPages;
	    String nowPage = request.getParameter("page");
	    String searchField = XSSEscape.changeDocumentField(request.getParameter("searchField"));
	    String searchOrder = XSSEscape.changeOrder(request.getParameter("searchOrder"));
	    String searchText = XSSEscape.changeText(request.getParameter("searchText"));
	    String startDate = XSSEscape.checkDate(request.getParameter("startDate"));
	    String endDate = XSSEscape.checkDate(request.getParameter("endDate"));
	    String filterCategory = XSSEscape.isNumber(request.getParameter("filterCategory"));
	    String filterClient = XSSEscape.isNumber(request.getParameter("filterClient"));
	    CategoryDTO category = new CategoryDTO();
	    ArrayList<CategoryDTO> categoryList = new ArrayList<CategoryDTO>();
	    ArrayList<ClientDTO> clientList = new ArrayList<ClientDTO>();
	    ArrayList<DocumentDTO> list = new ArrayList<DocumentDTO>();
	    

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
	    
	    if (searchField != null && searchText != null && searchOrder != null){
			totalPages = Math.max(documentDAO.maxPage(startDate, endDate, filterCategory, filterClient, searchField, searchText), 1);
			
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = documentDAO.getSearch(startDate, endDate, filterCategory, filterClient, nowPage, searchField, searchOrder, searchText);
		    
	    } //전체 문서 리스트 반환
	    else {
	    	searchField = null;
	    	searchText = null;
	    	searchOrder = null;
	    	totalPages = Math.max(documentDAO.maxPage(startDate, endDate, filterCategory, filterClient), 1);
	    	
		    endPage = Math.min(startPage + 4, totalPages);

		    if (endPage - startPage < 4) {
		      startPage = Math.max(endPage - 4, 1);
		    }
		    
		    list = documentDAO.getList(startDate, endDate, filterCategory, filterClient, nowPage);
	    }
	    
	    categoryList = categoryDAO.getList();
	    clientList = clientDAO.getList();
	    
	    categoryDAO.categoryClose();
	    clientDAO.clientClose();
	    documentDAO.documentClose();

	    //값 반환
	    request.setAttribute("categoryList", categoryList);
	    request.setAttribute("clientList", clientList);
	    request.setAttribute("filterCategory", filterCategory);
	    request.setAttribute("filterClient", filterClient);
        request.setAttribute("searchField", XSSEscape.restoreDocumentField(searchField));
        request.setAttribute("searchOrder", XSSEscape.restoreOrder(searchOrder));
        request.setAttribute("searchText", searchText);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("startPage", startPage);
        request.setAttribute("nowPage", nowPage);
        request.setAttribute("endPage", endPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("category", category);
        request.setAttribute("list", list);
        
	    request.getRequestDispatcher("Document.jsp").forward(request, response);
	}

}
