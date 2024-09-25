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
 * Servlet implementation class CategoryDeleteAction
 */
@WebServlet("/CategoryDelete")
public class CategoryDeleteAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CategoryDeleteAction() {
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
		String categoryCode = XSSEscape.isNumber(request.getParameter("categoryCode"));
		String categoryName = categoryDAO.getCategoryName(Integer.parseInt(categoryCode));
		String folderPath = getServletContext().getRealPath("/root/");
		File folder = new File(folderPath + categoryName);
		
		//입력된 값 검증 및 사용자 권한 확인
		if (user == null || !user.isCategory() || categoryCode == null || categoryName == null) {
	        request.setAttribute("errorMessage", "비정상적인 접근");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
		    categoryDAO.categoryClose();
			return;
		}
		
		
		//문서 권한에 따른 문서 목록 삭제 처리
		if (user.isDocument() && folder.exists() &&
				categoryDAO.documentDelete(categoryName, Integer.parseInt(categoryCode), user.getUserCode()) == 1 &&
				categoryDAO.categoryDelete(categoryName, Integer.parseInt(categoryCode), user.getUserCode()) == 1 &&
				deleteDirectory(folder)) {
            request.setAttribute("messageCategory", "문서 목록 삭제 성공!");
            request.getRequestDispatcher("WEB-INF/Message.jsp").forward(request, response);
		} //문서 권한이 없으면 폴더가 비어 있을 때만 삭제시킴
		else if(folder.exists() && isDirectoryEmpty(folder) && 
				categoryDAO.categoryDelete(categoryName, Integer.parseInt(categoryCode), user.getUserCode()) == 1 && folder.delete()) {
            request.setAttribute("messageCategory", "문서 목록 삭제 성공!");
            request.getRequestDispatcher("WEB-INF/Message.jsp").forward(request, response);
		} //만약 문서 목록 내부에 문서들이 있고 문서에 대한 권한이 없으면 삭제 실패
		else if (!user.isDocument() && folder.exists()){
	        request.setAttribute("errorMessage", "문서 권한이 없어 삭제에 실패했습니다.");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
		} //실패
		else {
	        request.setAttribute("errorMessage", "삭제 실패!");
		    request.getRequestDispatcher("WEB-INF/Error.jsp").forward(request, response);
		}
		
		categoryDAO.categoryClose();

	}
	
	//해당 폴더(문서 목록)가 비어있는지 확인하는 메소드
	private static boolean isDirectoryEmpty(File directory) {
    	String[] files = directory.list();
        return files == null || files.length == 0;
    }
   
	//해당 폴더(문서 목록) 안의 모든 파일(문서)를 삭제하는 메소드
	private static boolean deleteDirectory(File folder) {
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			if (files != null) {
				for (File file : files) {
					deleteDirectory(file);
				}
			}
		}
		return folder.delete();
	}

}
