package com.myservlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.client.*;
import com.myclass.XSSEscape;
import com.user.UserDTO;

/**
 * Servlet implementation class ClientUploadAction
 */
@WebServlet("/ClientUpload")
public class ClientUploadAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClientUploadAction() {
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
        

        ClientDAO clientDAO = new ClientDAO();
        
		try {
			String clientName = XSSEscape.changeClientName(request.getParameter("clientName"));
			String clientContent = XSSEscape.changeText(request.getParameter("clientContent"));
			HttpSession session = request.getSession();
			UserDTO user = (UserDTO) session.getAttribute("user");
			
			if (clientName == null) {
	            request.setAttribute("errorMessage", "실패");
	            request.getRequestDispatcher("Error.jsp").forward(request, response);
			} else if(clientDAO.clientUniqueName(clientName)){
	            request.setAttribute("errorMessage", "이름이 중복됩니다.");
	            request.getRequestDispatcher("Error.jsp").forward(request, response);
			} else {
	            clientDAO.clientUpload(clientName, clientContent, user.getUserCode());
	            request.setAttribute("messageClient", "고객사를 등록했습니다.");
	            request.getRequestDispatcher("Message.jsp").forward(request, response);
			}
        } catch(Exception e) {
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
        }

		clientDAO.clientClose();
		
	}

}
