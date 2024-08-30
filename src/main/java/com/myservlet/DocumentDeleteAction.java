package com.myservlet;

import java.io.IOException;
import java.io.File;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.document.DocumentDAO;
import com.user.UserDTO;
import com.myclass.XSSEscape;

/**
 * Servlet implementation class DocumentDeleteAction
 */
@WebServlet("/DocumentDelete")
public class DocumentDeleteAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentDeleteAction() {
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
        
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		String categoryCode = XSSEscape.isNumber(request.getParameter("categoryCode"));
		String fileName = XSSEscape.changeCategoryName(request.getParameter("fileName"));
		
		if (user == null || !user.isDocument() || categoryCode == null || fileName == null) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}

        DocumentDAO documentDAO = new DocumentDAO();
		String folderPath = getServletContext().getRealPath(File.separator + documentDAO.getRoot(categoryCode) + File.separator + fileName);
        File file = new File(folderPath);
		
        if (file.exists() && documentDAO.documentDelete(fileName, categoryCode, user.getUserCode()) == 1) {
            file.delete();
            request.setAttribute("messageDocument", "문서 삭제 성공!");
            request.getRequestDispatcher("Message.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
        }

	}

}
