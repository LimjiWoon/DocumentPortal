package com.myservlet;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.category.CategoryDAO;
import com.myclass.XSSEscape;
import com.user.UserDTO;

/**
 * Servlet implementation class CategoryUpdateAction
 */
@WebServlet("/CategoryUpdate")
public class CategoryUpdateAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CategoryUpdateAction() {
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
		

        //세션에 로그인 정보가 있는지 확인부터 한다.
		HttpSession session = request.getSession();
		UserDTO user = (UserDTO) session.getAttribute("user");
		CategoryDAO categoryDAO = new CategoryDAO();
		String categoryCode = XSSEscape.isNumber(request.getParameter("hiddenCategoryCode"));
		String categoryName = XSSEscape.changeCategoryName(request.getParameter("categoryName"));
		String originCategoryName = categoryDAO.getCategoryName(Integer.parseInt(categoryCode));
		String folderPath = getServletContext().getRealPath("/root/");
		
		//사용자 권한 확인 및 데이터 검증
		if (user == null || !user.isCategory() || categoryName == null || categoryCode == null || originCategoryName == null) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			categoryDAO.categoryClose();
			return;
		}
		
		//입력된 값이 기존의 데이터와 같을 경우
		if (originCategoryName.equals(categoryName)) {
	        request.setAttribute("errorMessage", "바뀐 값이 없습니다.");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			categoryDAO.categoryClose();
			return;
		}
		
		File folder = new File(folderPath + originCategoryName);
		File newFolder = new File(folderPath + categoryName);
		
		try {
			//값이 바꼈을 경우 폴더의 존재 확인 후 변경을 하고 DB 수정
			if(folder.exists() && folder.renameTo(newFolder) && categoryDAO.categoryUpdate(categoryName, originCategoryName, Integer.parseInt(categoryCode), user.getUserCode()) == 1) {
	            request.setAttribute("messageCategory", "문서 목록 수정 성공!");
	            request.getRequestDispatcher("WEB-INF/Message.jsp").forward(request, response);
			} else {
		        request.setAttribute("errorMessage", "문서 목록 수정 실패!");
			    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
			}
		} catch(Exception e) {
			categoryDAO.errorLogUpload(e);
	        request.setAttribute("errorMessage", "에러");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
		}
		
		
		categoryDAO.categoryClose();
		
	}

}
