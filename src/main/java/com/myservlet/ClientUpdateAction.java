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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 직접 url을 타이핑하여 접근하는 것을 차단한다 -> 오로지 동작을 위한 servlet임
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
	    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
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
            request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
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
        //수정 성공 시
		if ( result == 1 ) {
            request.setAttribute("messageClient", "수정 성공");
            request.getRequestDispatcher("WEB-INF/Message.jsp").forward(request, response);
		} //바뀐 값 없을 때
		else if ( result == 0 ) {
            request.setAttribute("errorMessage", "바뀐 값이 없습니다.");
            request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
		} //수정 실패 시
		else {
            request.setAttribute("errorMessage", "수정에 실패했습니다.");
            request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
		}
		
		clientDAO.clientClose();
	}

}
