package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.document.*;
import com.myclass.XSSEscape;
import com.user.UserDTO;

/**
 * Servlet implementation class DocumentViewAction
 */
@WebServlet("/DocumentView")
public class DocumentViewAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentViewAction() {
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
		if (user == null) {
	        request.setAttribute("errorMessage", "로그인을 해주세요.");
		} else {
	        request.setAttribute("errorMessage", "Url을 직접 입력하여 들어올 수 없습니다.");
		}
	    request.getRequestDispatcher("Error.jsp").forward(request, response);
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
		String fileName = XSSEscape.changeCategoryName(request.getParameter("fileName"));
		String categoryCode = XSSEscape.isNumber(request.getParameter("categoryCode"));
		String clientCode = XSSEscape.isClientCode(request.getParameter("clientCode"));
		
		if (user == null || !user.isDocument() || fileName == null || categoryCode== null) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}
		
    	DocumentDAO documentDAO = new DocumentDAO();
    	DocumentDTO document = new DocumentDTO();
		
		document = documentDAO.getDocumentInfo(fileName, categoryCode, clientCode);
		
		documentDAO.documentClose();
		
        request.setAttribute("document", document);
	    request.getRequestDispatcher("DocumentView.jsp").forward(request, response);
		
	}

}
