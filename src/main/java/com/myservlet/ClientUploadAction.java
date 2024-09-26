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
			
			//사용자 권한 확인
			if (user == null || !user.isClient()) {
	            request.setAttribute("errorMessage", "비정상적인 접근");
	            request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
				return;
			}
			
			//입력값이 이상할 경우
			if (clientName == null) {
	            request.setAttribute("errorMessage", "실패");
	            request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			} //고객사 이름이 중복될 경우
			else if(clientDAO.clientUniqueName(clientName)){
	            request.setAttribute("errorMessage", "이름이 중복됩니다.");
	            request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			} //등록 성공
			else {
	            clientDAO.clientUpload(clientName, clientContent, user.getUserCode());
	            request.setAttribute("messageClient", "고객사를 등록했습니다.");
	            request.getRequestDispatcher("WEB-INF/Message.jsp").forward(request, response);
			}
        } catch(Exception e) {
        	clientDAO.errorLogUpload(e);
            request.setAttribute("errorMessage", "비정상적인 접근");
            request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
        }

		clientDAO.clientClose();
		
	}

}
