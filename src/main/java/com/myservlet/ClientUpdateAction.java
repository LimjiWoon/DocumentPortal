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
 * Servlet implementation class ClientUpdateAction
 */
@WebServlet("/ClientUpdate")
public class ClientUpdateAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClientUpdateAction() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        //사용자 정보가 옳바른지 확인 및 정보가 정상인지 확인
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		String clientCode = XSSEscape.isNumber(request.getParameter("clientCode"));
		if (user == null || clientCode == null || !user.isClient()) {
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
			return;
		}

        ClientDAO clientDAO = new ClientDAO();

		//수정 시 실행
		String clientName = XSSEscape.changeClientName(request.getParameter("clientName"));
		String hiddenClientName = XSSEscape.changeClientName(request.getParameter("hiddenClientName"));
		String clientContent = XSSEscape.changeText(request.getParameter("clientContent"));
		String hiddenClientContent = XSSEscape.changeText(request.getParameter("hiddenClientContent"));
        int result = clientDAO.clientUpdate(Integer.parseInt(clientCode), clientName, hiddenClientName, 
        		clientContent, hiddenClientContent, user.getUserCode());
		if ( result == 1 ) {
            request.setAttribute("messageClient", "수정 성공");
            request.getRequestDispatcher("Message.jsp").forward(request, response);
		} else if ( result == 0 ) {
            request.setAttribute("errorMessage", "바뀐 값이 없습니다.");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
			
		} else {
            request.setAttribute("errorMessage", "수정에 실패했습니다.");
            request.getRequestDispatcher("Error.jsp").forward(request, response);
		}
		
		clientDAO.clientClose();
	}

}
