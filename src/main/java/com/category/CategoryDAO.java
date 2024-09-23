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
	
	
	private String filterSQL(String SQL, String startDate, String endDate, String searchField, String searchText) {
		//그 어떠한 값도 들어오지 않은 경우
		if (startDate == null && endDate == null && searchField == null && searchText == null) {
			return SQL;
		} 

		StringBuilder query = new StringBuilder(SQL);
		ArrayList<String> conditions = new ArrayList<String>();
		
		if (startDate != null && endDate != null) {
			conditions.add(" AND  dateOfCreate BETWEEN '"+startDate+"' AND DATEADD(day, 1, '"+endDate+"') ");
		} else if (startDate != null) {
			conditions.add(" AND  dateOfCreate > '"+startDate+"' ");
		} else if (endDate != null) {
			conditions.add(" AND  dateOfCreate < DATEADD(day, 1, '"+endDate+"') ");
		}
		
		if (searchField != null && searchText != null) {
			conditions.add(" AND " + searchField.trim() + " LIKE '%" + searchText.trim() + "%' ");
		}

		if (!conditions.isEmpty()) {
		    query.append(String.join("", conditions));
		}
		
		return query.toString();
	}
	
	public int maxPage(String startDate, String endDate) { //최대 페이지 수 가져오기
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.CATEGORIES  WHERE categoryLv=1 ";
		SQL = filterSQL(SQL, startDate, endDate, null, null);

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
	
	public int maxPage(String startDate, String endDate, String searchField, String searchText) { 
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.CATEGORIES cat "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = cat.userCode "
				+ "WHERE categoryLv=1 ";
		if (searchText.trim() != ""){
			SQL = filterSQL(SQL, startDate, endDate, searchField, searchText);
		} else {
			SQL = filterSQL(SQL, startDate, endDate, null, null);
		}
		
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
	
	
	public ArrayList<CategoryDTO> getList(String startDate, String endDate, String nowPage){
		String SQL = "SELECT cat.categoryCode, cat.categoryName, u.userName, cat.dateOfCreate "
				+ "FROM dbo.CATEGORIES cat "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = cat.userCode "
				+ "WHERE categoryLv=1 ";

		SQL = filterSQL(SQL, startDate, endDate, null, null);
		SQL += "ORDER BY cat.categoryCode ASC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
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

	
	
	public ArrayList<CategoryDTO> getSearch(String startDate, String endDate, String nowPage, String searchField, String searchOrder, String searchText){
		String SQL = "SELECT cat.categoryCode, cat.categoryName, u.userName, cat.dateOfCreate "
				+ "FROM dbo.CATEGORIES cat "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = cat.userCode "
				+ "WHERE categoryLv=1 ";
		
		if (searchText.trim() != ""){
			SQL = filterSQL(SQL, startDate, endDate, searchField, searchText);
		} else {
			SQL = filterSQL(SQL, startDate, endDate, null, null);
		}
		
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
	
	
	public ArrayList<CategoryDTO> getExcel(int userCode, String startDate, String endDate, String searchField, String searchOrder, String searchText){
		String SQL = "SELECT cat.categoryCode, cat.categoryName, u.userName, cat.dateOfCreate "
				+ "FROM dbo.CATEGORIES cat "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = cat.userCode "
				+ "WHERE categoryLv=1 ";
		
		if (searchField != null && searchText != null && searchOrder != null) {
			if (searchText.trim() != ""){
				SQL = filterSQL(SQL, startDate, endDate, searchField, searchText);
			} else {
				SQL = filterSQL(SQL, startDate, endDate, null, null);
			}
			SQL += "ORDER BY " + searchField.trim() + " " + searchOrder +" ;";
		} else {
			SQL = filterSQL(SQL, startDate, endDate, null, null);
		}
		
		ArrayList<CategoryDTO> list = new ArrayList<CategoryDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			
			rs = pstmt.executeQuery();
			logUpload(userCode, "", "category", "download", "문서 목록 리스트 엑셀 다운로드");
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
	
	/*
	 * 기존 모달의 기능
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
	*/
	
	public ArrayList<String> getModal(String categoryCode){
		ArrayList<String> list = new ArrayList<String>();
		
        String SQL = "SELECT c.clientName "
        		+ "FROM dbo.FILES f "
        		+ "LEFT JOIN dbo.CLIENTS c ON f.clientCode = c.clientCode "
        		+ "WHERE categoryCode=? "
        		+ "GROUP BY f.clientCode, f.categoryCode, c.clientName;";
        
        if (categoryCode == null) {
        	return list;
        }

        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, Integer.parseInt(categoryCode));

            // 쿼리 실행
            rs = pstmt.executeQuery();
            while (rs.next()) {
				list.add(rs.getString(1));
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
			
			logUpload(userCode, categoryName, "category", "create", "신규 문서 목록 생성");
			
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

	
	public String getCategoryName(int categoryCode) {
		String SQL = "SELECT categoryName FROM dbo.CATEGORIES WHERE categoryCode = ?;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, categoryCode);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getString(1);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int categoryUpdate(String categoryName, String originCategoryName,int categoryCode, int userCode) {
		String SQL = "UPDATE dbo.CATEGORIES SET categoryName=?, categoryRoot=? WHERE categoryCode=?;";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, categoryName);
			pstmt.setString(2, "/root/"+categoryName);
			pstmt.setInt(3, categoryCode);
			pstmt.executeUpdate();
			
			logUpload(userCode, categoryName, "category", "update", categoryCode + ": " + originCategoryName + "-&gt;" + categoryName);
			
			return 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return -1;
	}

	public int documentDelete(String categoryName, int categoryCode, int userCode) {
		String SQL = "DELETE dbo.FILES WHERE categoryCode=?;";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, categoryCode);
			pstmt.executeUpdate();
			
			logUpload(userCode, categoryName, "file", "delete", categoryCode + ": 문서 목록 내 파일 전부 삭제");
			
			return 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return -1;
	}
	
	public int categoryDelete(String categoryName, int categoryCode, int userCode) {
		String SQL = "DELETE CATEGORIES WHERE categoryCode=?;";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, categoryCode);
			pstmt.executeUpdate();
			
			logUpload(userCode, categoryName, "category", "delete", categoryCode + ": 문서 목록 삭제");
			
			return 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return -1;
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
