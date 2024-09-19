package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;
import java.util.ArrayList;
import com.category.*;
import com.client.ClientDAO;
import com.myclass.XSSEscape;
import com.user.UserDTO;

/**
 * Servlet implementation class CategoryModalAction
 */
@WebServlet("/Modal")
public class CategoryModalAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CategoryModalAction() {
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
		
		request.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=UTF-8");
		
        ArrayList<String> list;
        String selectType = XSSEscape.isNumber(request.getParameter("selectType"));
		String dataType = XSSEscape.isNumber(request.getParameter("dataType"));
        
        if ("0".equals(selectType)) {
            CategoryDAO categoryDAO = new CategoryDAO();
            list = categoryDAO.getModal(dataType);
            categoryDAO.categoryClose();
        } else if ("1".equals(selectType)) {
        	ClientDAO clientDAO = new ClientDAO();
            list = clientDAO.getModal(dataType);
            clientDAO.clientClose();
        } else {
        	list = null;
        	return;
        }

        // 데이터와 타입을 요청에 응답으로 전달
        request.setAttribute("list", list);
        request.setAttribute("selectType", selectType);
        
        
        // 모달의 HTML을 생성할 JSP 페이지를 통해 응답
        RequestDispatcher dispatcher = request.getRequestDispatcher("CategoryModal.jsp");
        dispatcher.forward(request, response);
    }

}
