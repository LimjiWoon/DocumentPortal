package com.category;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;




public class CategoryDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private CategoryDTO category;
	
	public CategoryDAO() {
		try {
			String dbURL = "jdbc:sqlserver://localhost:1433;encrypt=false;DatabaseName=DocumentPortalData;";
			String dbID = "admin";
			String dbPassword = "qwert12345!";
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn=DriverManager.getConnection(dbURL, dbID, dbPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public int maxPage() { //최대 페이지 수 가져오기
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.CATEGORIES WHERE categoryLv=1;";
		try {
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return (rs.getInt(1)-1) / 10 + 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}
	
	public int maxPage(String searchField, String searchText) { 
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.CATEGORIES cat "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = cat.userCode ";
		if (searchText.trim() != "")
				SQL += "WHERE " + searchField.trim() + " LIKE '%" + searchText.trim() + "%'";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return (rs.getInt(1)-1) / 10 + 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}

	
	public ArrayList<CategoryDTO> getList(){
		ArrayList<CategoryDTO> list = new ArrayList<CategoryDTO>();
		
        String SQL = "SELECT categoryCode, categoryName FROM dbo.CATEGORIES "
        		+ "WHERE categoryLv = 1 ORDER BY categoryCode ASC;";

        try {
            pstmt = conn.prepareStatement(SQL);

            // 쿼리 실행
            rs = pstmt.executeQuery();
            while (rs.next()) {
				category = new CategoryDTO();
				category.setCategoryCode(rs.getInt(1));
				category.setCategoryName(rs.getString(2));
				list.add(category);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		return list;
	}
	
	
	public ArrayList<CategoryDTO> getList(String nowPage){
		String SQL = "SELECT cat.categoryCode, cat.categoryName, u.userName, cat.dateOfCreate "
				+ "FROM dbo.CATEGORIES cat "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = cat.userCode "
				+ "WHERE categoryLv=1 "
				+ "ORDER BY cat.categoryCode ASC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		ArrayList<CategoryDTO> list = new ArrayList<CategoryDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (nowPage == null) {
				pstmt.setInt(1, 0);
			} else {
				pstmt.setInt(1, (Integer.parseInt(nowPage) -1) * 10);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				category = new CategoryDTO();
				category.setCategoryCode(rs.getInt(1));
				category.setCategoryName(rs.getString(2));
				category.setUserName(rs.getString(3));
				category.setDateOfCreate(rs.getString(4));
				list.add(category);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
	public ArrayList<CategoryDTO> getSearch(String nowPage, String searchField, String searchOrder, String searchText){
		String SQL = "SELECT cat.categoryCode, cat.categoryName, u.userName, cat.dateOfCreate "
				+ "FROM dbo.CATEGORIES cat "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = cat.userCode ";
		if (searchText.trim() != "")
			SQL += " WHERE " + searchField.trim() + " LIKE '%" + searchText.trim() + "%'";
		SQL += "ORDER BY " + searchField.trim() + " " + searchOrder +" OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		ArrayList<CategoryDTO> list = new ArrayList<CategoryDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (nowPage == null) {
				pstmt.setInt(1, 0);
			} else {
				pstmt.setInt(1, (Integer.parseInt(nowPage) -1) * 10);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				category = new CategoryDTO();
				category.setCategoryCode(rs.getInt(1));
				category.setCategoryName(rs.getString(2));
				category.setUserName(rs.getString(3));
				category.setDateOfCreate(rs.getString(4));
				list.add(category);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	public ArrayList<CategoryDTO> getModal(String categoryCode){
		ArrayList<CategoryDTO> list = new ArrayList<CategoryDTO>();
		
        String SQL = "DECLARE @SelectedCategoryRoot NVARCHAR(MAX); "
        		+ "SELECT @SelectedCategoryRoot = categoryRoot FROM CATEGORIES WHERE categoryCode = ?; "
        		+ "SELECT categoryCode, categoryName, categoryCodeUp, categoryLv FROM CATEGORIES "
        		+ "WHERE categoryRoot LIKE @SelectedCategoryRoot + '%' ORDER BY categoryRoot;";
        
        if (categoryCode == null) {
        	return list;
        }

        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, Integer.parseInt(categoryCode));

            // 쿼리 실행
            rs = pstmt.executeQuery();
            while (rs.next()) {
				category = new CategoryDTO();
				category.setCategoryCode(rs.getInt(1));
				category.setCategoryName(rs.getString(2));
				category.setCategoryLv(rs.getInt(4));
				list.add(category);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
		
		return list;
	}
	
	public String getRoot(int categoryLv, int categoryCode) {
		String SQL = "SELECT categoryLv, categoryRoot "
				+ "FROM dbo.CATEGORIES "
				+ "WHERE categoryCode = ?;";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, categoryCode);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				if (categoryLv == rs.getInt(1))
					return rs.getString(2);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int categoryUpload(String categoryName, int categoryLv, int userCode, String categoryRoot) {
		String SQL = "INSERT INTO dbo.CATEGORIES VALUES (?, ?, ?, ?, GETDATE(), ?);";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, categoryLv+1);
			pstmt.setInt(2, 10000);
			pstmt.setString(3, categoryName);
			pstmt.setInt(4, userCode);
			pstmt.setString(5, categoryRoot);
			
			pstmt.executeUpdate();
			
			logUpload(userCode, categoryName, "category", "create", "신규 카테고리 생성");
			
			return 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public int categoryCodeLast() {
		String SQL = "SELECT MAX(categoryCode) as big FROM dbo.CATEGORIES;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public CategoryDTO getCategoryInfo(int categoryCode) {
		String SQL = "SELECT categoryLv, categoryName, categoryCodeUp, categoryRoot "
				+ "FROM dbo.CATEGORIES WHERE categoryCode = ?;";
		category = new CategoryDTO();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, categoryCode);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				category.setCategoryLv(rs.getInt(1));
				category.setCategoryName(rs.getString(2));
				category.setCategoryCodeUp(rs.getInt(3));
				category.setCategoryRoot(rs.getString(4));
				category.setCategoryCode(categoryCode);
				return category;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int logUpload(int logWho, String logWhat, String logWhere, String logHow, String logWhy) {
		String SQL = "INSERT INTO dbo.LOGS (logWho, logWhat, logWhere, logHow, logWhy) "
				+ " VALUES (?, ?, ?, ?, ?);";

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, logWho);
			pstmt.setString(2, logWhat);
			pstmt.setString(3, logWhere);
			pstmt.setString(4, logHow);
			if (logWhy == null){
				pstmt.setNull(5, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(5, logWhy);
			}
			pstmt.executeUpdate();
			
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void categoryClose() {
	    try {
	        if (rs != null) {
	            rs.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); // 예외 처리 로직 추가
	    }

	    try {
	        if (pstmt != null) {
	            pstmt.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); // 예외 처리 로직 추가
	    }

	    try {
	        if (conn != null) {
	            conn.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); // 예외 처리 로직 추가
	    }
	}
	
}
