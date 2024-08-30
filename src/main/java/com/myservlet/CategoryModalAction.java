package com.myservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import java.util.ArrayList;
import com.category.*;
import com.myclass.XSSEscape;

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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=UTF-8");
		
        CategoryDAO categoryDAO = new CategoryDAO();
        
		String dataType = XSSEscape.isNumber(request.getParameter("dataType"));
        ArrayList<CategoryDTO> list = categoryDAO.getModal(dataType);

        // 데이터와 타입을 요청에 응답으로 전달
        request.setAttribute("list", list);
        request.setAttribute("type", XSSEscape.isNumber(request.getParameter("selectType")));
        
        categoryDAO.categoryClose();
        
        // 모달의 HTML을 생성할 JSP 페이지를 통해 응답
        RequestDispatcher dispatcher = request.getRequestDispatcher("CategoryModal.jsp");
        dispatcher.forward(request, response);
    }

}
