package com.category;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.myclass.CollectLog;




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
	
	//SQL query문의 Where절 조합기
	private String filterSQL(String SQL, String startDate, String endDate, String searchField, String searchText) {
		if (startDate == null && endDate == null && searchField == null && searchText == null) {
			return SQL; //기존 query문을 반환
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
		
		return query.toString(); //조합된 Where절을 반환
	}
	
	//최대 페이지 수 구하기
	public int maxPage(String startDate, String endDate, String searchField, String searchText) { 
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.CATEGORIES cat "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = cat.userCode "
				+ "WHERE categoryLv=1 ";

		SQL = filterSQL(SQL, startDate, endDate, searchField, searchText);
		
		try {
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return (rs.getInt(1)-1) / 10 + 1; //최대 페이지 반환
			}
		} catch(Exception e) {
			errorLogUpload(e);
		}
		return -1; //DB 오류
	}

	//select 바에서 사용할 getList -> 코드와 이름만 반환
	public ArrayList<CategoryDTO> getList(){
		ArrayList<CategoryDTO> list = new ArrayList<CategoryDTO>();
		
        String SQL = "SELECT categoryCode, categoryName FROM dbo.CATEGORIES "
        		+ "WHERE categoryLv = 1 ORDER BY categoryCode ASC;";

        try {
            pstmt = conn.prepareStatement(SQL);

            rs = pstmt.executeQuery();
            while (rs.next()) {
				category = new CategoryDTO();
				category.setCategoryCode(rs.getInt(1));
				category.setCategoryName(rs.getString(2));
				list.add(category);
            }
        } catch (Exception e) {
			errorLogUpload(e);
        }
		
		return list; //리스트를 반환 -> 결과가 없으면 빈 리스트 반환
	}
	
	//홈페이지에 List 정보를 띄울 getList -> 모든 정보를 반환
	public ArrayList<CategoryDTO> getList(String startDate, String endDate, String nowPage, String searchField, String searchOrder, String searchText){
		String SQL = "SELECT cat.categoryCode, cat.categoryName, u.userName, cat.dateOfCreate "
				+ "FROM dbo.CATEGORIES cat "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = cat.userCode "
				+ "WHERE categoryLv=1 ";
		
		SQL = filterSQL(SQL, startDate, endDate, searchField, searchText);
		
		if (searchField != null && searchOrder != null && searchText != null) {
			SQL += " ORDER BY " + searchField.trim() + " " + searchOrder +" OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		} else{
			SQL += " ORDER BY cat.categoryCode ASC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		}
		
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
			errorLogUpload(e);
		}
		
		return list; //리스트를 반환 -> 결과가 없으면 빈 리스트 반환
	}
	
	//엑셀 시트를 다운로드 하기 위한 정보를 반환하는 메소드
	public ArrayList<CategoryDTO> getExcel(int userCode, String startDate, String endDate, String searchField, String searchOrder, String searchText){
		String SQL = "SELECT cat.categoryCode, cat.categoryName, u.userName, cat.dateOfCreate "
				+ "FROM dbo.CATEGORIES cat "
				+ "LEFT JOIN dbo.USERS u ON u.userCode = cat.userCode "
				+ "WHERE categoryLv=1 ";
		
		SQL = filterSQL(SQL, startDate, endDate, searchField, searchText);
		
		if (searchField != null && searchText != null && searchOrder != null) {
			SQL += "ORDER BY " + searchField.trim() + " " + searchOrder +" ;";
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
			errorLogUpload(e);
		}
		
		return list; //리스트를 반환 -> 결과가 없으면 빈 리스트 반환
	}
	
	//해당 문서 목록을 사용 중인 고객사 리스트를 반환하는 메소드
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

            rs = pstmt.executeQuery();
            while (rs.next()) {
				list.add(rs.getString(1));
            }
        } catch (Exception e) {
			errorLogUpload(e);
        }
		
		return list; //리스트를 반환 -> 결과가 없으면 빈 리스트 반환
	}
	
	//새로운 문서 목록을 등록하는 메소드
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
			
			return 1; //등록 성공
		} catch(Exception e) {
			logUpload(userCode, "", "category", "error", CollectLog.getLog(e));
		}
		
		return -1; //등록 실패
	}
	
	//특정 문서 목록의 정보를 가져오는 메소드
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
				return category; //문서 목록 정보 있음 
			}
		} catch(Exception e) {
			errorLogUpload(e);
		}
		return null; //문서 목록 정보 없음
	}

	//특정 문서 목록의 이름을 가져오는 메소드
	public String getCategoryName(int categoryCode) {
		String SQL = "SELECT categoryName FROM dbo.CATEGORIES WHERE categoryCode = ?;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, categoryCode);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getString(1); //해당 문서 목록 있음
			}
		} catch(Exception e) {
			errorLogUpload(e);
		}
		return null; //해당 문서 목록 없음
	}

	//문서 목록을 갱신하는 메소드
	public int categoryUpdate(String categoryName, String originCategoryName,int categoryCode, int userCode) {
		String SQL = "UPDATE dbo.CATEGORIES SET categoryName=?, categoryRoot=? WHERE categoryCode=?;";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, categoryName);
			pstmt.setString(2, "/root/"+categoryName);
			pstmt.setInt(3, categoryCode);
			pstmt.executeUpdate();
			
			logUpload(userCode, categoryName, "category", "update", categoryCode + ": " + originCategoryName + "-&gt;" + categoryName);
			
			return 1; //갱신 성공
		} catch(Exception e) {
			logUpload(userCode, "", "category", "error", CollectLog.getLog(e));
		}
		return -1; //갱신 실패
	}

	//문서 목록을 삭제할 때 내부에 있는 문서들을 삭제하는 메소드
	public int documentDelete(String categoryName, int categoryCode, int userCode) {
		String SQL = "DELETE dbo.FILES WHERE categoryCode=?;";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, categoryCode);
			pstmt.executeUpdate();
			
			logUpload(userCode, categoryName, "file", "delete", categoryCode + ": 문서 목록 내 파일 전부 삭제");
			
			return 1; //삭제 성공
		} catch(Exception e) {
			logUpload(userCode, "", "category", "error", CollectLog.getLog(e));
		}
		
		
		return -1; //삭제 실패
	}
	
	//문서 목록을 삭제하는 메소드, documentDelete를 호출하여 내부 문서까지 함께 삭제한다.
	public int categoryDelete(String categoryName, int categoryCode, int userCode) {
		String SQL = "DELETE CATEGORIES WHERE categoryCode=?;";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, categoryCode);
			pstmt.executeUpdate();
			
			logUpload(userCode, categoryName, "category", "delete", categoryCode + ": 문서 목록 삭제");
			
			return 1; //삭제 성공
		} catch(Exception e) {
			logUpload(userCode, "", "category", "error", CollectLog.getLog(e));
		}
		
		
		return -1; //삭제 실패
	}
	
	//로그를 기록하는 메소드
	public void logUpload(int logWho, String logWhat, String logWhere, String logHow, String logWhy) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//에러 로그를 기록하는 메소드
	public void errorLogUpload(Exception error) {
		String SQL = "INSERT INTO dbo.LOGS (logWho, logWhat, logWhere, logHow, logWhy) "
				+ " VALUES (NULL, '', 'category', 'error', ?);";

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, CollectLog.getLog(error));
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//접속한 DB의 연결을 끝는 메소드
	public void categoryClose() {
	    try {
	        if (rs != null) {
	            rs.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    try {
	        if (pstmt != null) {
	            pstmt.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    try {
	        if (conn != null) {
	            conn.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
}
