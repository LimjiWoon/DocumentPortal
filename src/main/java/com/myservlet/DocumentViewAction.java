package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.document.*;
import com.myclass.XSSEscape;

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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=UTF-8");

    	DocumentDAO documentDAO = new DocumentDAO();
    	DocumentDTO document = new DocumentDTO();
        
		String fileName = XSSEscape.changeCategoryName(request.getParameter("fileName"));
		String categoryCode = XSSEscape.isNumber(request.getParameter("categoryCode"));
		String clientCode = XSSEscape.isClientCode(request.getParameter("clientCode"));
		
		
		if (fileName == null || categoryCode== null) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}
		
		document = documentDAO.getDocumentInfo(fileName, categoryCode, clientCode);
		
		documentDAO.documentClose();
		
        request.setAttribute("document", document);
	    request.getRequestDispatcher("DocumentView.jsp").forward(request, response);
		
	}

}
