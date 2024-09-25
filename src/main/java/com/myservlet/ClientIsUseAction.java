package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.client.ClientDAO;
import com.user.UserDTO;

/**
 * Servlet implementation class ClientIsUseAction
 */
@WebServlet("/ClientUse")
public class ClientIsUseAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClientIsUseAction() {
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
        
        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		ClientDAO clientDAO = new ClientDAO();

        String status = request.getParameter("status");
        String clientCode = request.getParameter("clientCode");
        String clientName = clientDAO.getClientName(clientCode);

        //입력값 검증 및 사용자 권한 확인
        if (user == null || !user.isClient() || status == null || clientCode == null 
        		|| clientName == null || !status.matches("[OX]") || !clientCode.matches("\\d+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "입력 오류");
            clientDAO.clientClose();
            return;
        }
        
        //고객사 사용유무 변경
        try {
        	clientDAO.updateUse(Integer.parseInt(clientCode), status, user.getUserCode(), clientName);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "고객사 입력 오류");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 에러");
        }
        clientDAO.clientClose();
	}

}
