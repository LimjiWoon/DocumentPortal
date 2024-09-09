package com.user;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.myclass.PasswordLogic;

import java.sql.Connection;

public class UserDAO {

	private PasswordLogic logic; 
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private UserDTO user;
	
	
	public UserDAO(){
		logic = new PasswordLogic();
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
	
	private String filterSQL(String SQL, String startDate, String endDate, String filterCategory, 
			String filterClient, String searchField, String searchText) {
		//그 어떠한 값도 들어오지 않은 경우
		if (startDate == null && endDate == null && filterCategory == null && 
				filterClient == null && searchField == null && searchText == null) {
			return SQL;
		}
		
		StringBuilder query = new StringBuilder(SQL);
		ArrayList<String> conditions = new ArrayList<String>();
		
		if (startDate != null && endDate != null) {
			conditions.add(" f.dateOfUpdate BETWEEN '"+startDate+"' AND DATEADD(day, 1, '"+endDate+"') ");
		} else if (startDate != null) {
			conditions.add(" f.dateOfUpdate > '"+startDate+"' ");
		} else if (endDate != null) {
			conditions.add(" f.dateOfUpdate < DATEADD(day, 1, '"+endDate+"') ");
		}

		if (filterCategory != null) {
			conditions.add(" f.categoryCode ="+filterCategory+" ");
		}

		if (filterClient != null) {
			conditions.add(" f.clientCode ="+filterClient+" ");
		}
		
		if (searchField != null && searchText != null) {
			conditions.add(" " + searchField.trim() + " LIKE '%" + searchText.trim() + "%' ");
		}
		
		if (!conditions.isEmpty()) {
		    query.append(" WHERE ").append(String.join(" AND ", conditions));
		}
		
		return query.toString();
	}
	
	public int login(String userID, String userPassword) {
		String SQL = "SELECT userPassword,failOfPassword FROM USERS WHERE userID = ?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				if(rs.getString(1).equals(logic.getSHA256(userPassword))) {
					return 1; //로그인 성공
				} else {
					return 0; //비밀번호 불일치
				}
			}
			return -1; //아이디 없음
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -2; //데이터베이스 오류
	}
	
	public void loginSuccess(String userID) {
		String SQL = "UPDATE USERS SET failOfPassword=0 FROM USERS WHERE userID=?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loginFail(String userID, int failOfPassword) {
	    // 첫 번째 try-catch 블록
	    try {
	    	String SQL;
	    
	    	//실패 횟수 확인 후 일정 횟수 이상이면 계정 잠금, 아니면 1만 추가
	    	if (failOfPassword == 4){
	    		SQL = "UPDATE USERS SET failOfPassword = failOfPassword + 1, isLock = 1 WHERE userID = ?";
	    	} else {
	    		SQL = "UPDATE USERS SET failOfPassword = failOfPassword + 1 WHERE userID = ?";
	    	}

	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setString(1, userID);
	        pstmt.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        // 리소스 해제
	        try {
	            if (rs != null) rs.close();
	            if (pstmt != null) pstmt.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	public UserDTO getInfo(String userID) {
		String SQL = "SELECT * FROM USERS WHERE userID = ?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				user = new UserDTO();
				user.setUserCode(rs.getInt(1));
				user.setUserName(rs.getString(2));	
				user.setUserID(rs.getString(3));	
				user.setDateOfPassword(rs.getString(5));
				user.setFailOfPassword(rs.getInt(6));
				user.setLock(rs.getInt(7));
				user.setRetire(rs.getInt(8));
				user.setCategory(rs.getInt(9));
				user.setClient(rs.getInt(10));
				user.setDocument(rs.getInt(11));
				user.setUserID(userID);
			}
			return user; //아이디가 없으면 emtpy상태와 같음 -> 정보 없음
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; //데이터베이스 오류
	}
	
	public UserDTO getInfo(int userCode) {
		String SQL = "SELECT * FROM USERS WHERE userCode = ?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, userCode);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				user = new UserDTO();
				user.setUserCode(rs.getInt(1));
				user.setUserName(rs.getString(2));
				user.setUserID(rs.getString(3));
				user.setUserPassword(rs.getString(4));
				user.setCategory(rs.getInt(9));
				user.setClient(rs.getInt(10));
				user.setDocument(rs.getInt(11));
			}
			return user; //아이디가 없으면 emtpy상태와 같음 -> 정보 없음
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; //데이터베이스 오류
	}
	
	//DB의 비밀번호를 바꿀 때 현재 비밀번호가 맞는지 아닌지 확인하고 passwordUpdate를 실행하는 메소드
	public int passwordRenew(String userID, String nowPassword, String newPassword) {
		String SQL = "SELECT userPassword FROM USERS WHERE userID = ?";
		if(newPassword.equals(nowPassword))
				return -1;

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				if(rs.getString(1).equals(logic.getSHA256(nowPassword))) {
					passwordUpdate(userID, newPassword);
					return 1; //변경 성공
				}
			}
			return 0; //비밀번호 불일치
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -2; //데이터베이스 오류
	}
	
	//passwordRenew가 모든 검사를 마치면 비밀번호와 비밀번호 변경일을 바꾸는 메소드
	private void passwordUpdate(String userID, String newPassword) {
		String SQL = "UPDATE USERS SET userPassword=?, dateOfPassword=GETDATE() FROM USERS WHERE userID=?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, logic.getSHA256(newPassword));
			pstmt.setString(2, userID);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int maxPage(int isRetire) { //다음 글 가지고 오기
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.USERS WHERE isRetire = ? and userCode != 0;";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, isRetire);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if (isRetire == 0)
					return (rs.getInt(1)-1) / 10 + 1;
				return (rs.getInt(1)) / 10 + 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}
	
	public int maxPage(int isRetire, String ChangeDate) { //다음 글 가지고 오기
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.USERS WHERE isRetire = ? and userCode != 0 ";
		switch (ChangeDate) {
			case "1":
				SQL += "AND (dateOfPassword BETWEEN DATEADD(DAY, -90, GETDATE()) AND GETDATE()) OR dateOfPassword IS NULL;";
				break;
			case "2":
				SQL += "AND dateOfPassword BETWEEN DATEADD(DAY, -180, GETDATE()) AND DATEADD(DAY, -90, GETDATE());";
				break;
			case "3":
				SQL += "AND dateOfPassword < DATEADD(DAY, -180, GETDATE());";
				break;
			default:
				break;
		}
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, isRetire);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if (isRetire == 0)
					return (rs.getInt(1)-1) / 10 + 1;
				return (rs.getInt(1)) / 10 + 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}
	
	public int maxPage(int isRetire, String searchField, String searchText) { //다음 글 가지고 오기
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.USERS "
				+ "WHERE isRetire = ? AND userCode != 0 ";
		if (searchText.trim() != "")
				SQL += " AND " + searchField.trim() + " LIKE '%" + searchText.trim() + "%'";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, isRetire);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if (isRetire == 0)
					return (rs.getInt(1)-1) / 10 + 1;
				return (rs.getInt(1)) / 10 + 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}
	
	public int maxPage(int isRetire, String ChangeDate, String searchField, String searchText) { //다음 글 가지고 오기
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.USERS "
				+ "WHERE isRetire = ? AND userCode != 0 ";
		
		switch (ChangeDate) {
			case "1":
				SQL += "AND (dateOfPassword BETWEEN DATEADD(DAY, -90, GETDATE()) AND GETDATE()) OR dateOfPassword IS NULL ";
				break;
			case "2":
				SQL += "AND dateOfPassword BETWEEN DATEADD(DAY, -180, GETDATE()) AND DATEADD(DAY, -90, GETDATE()) ";
				break;
			case "3":
				SQL += "AND dateOfPassword < DATEADD(DAY, -180, GETDATE()) ";
				break;
			default:
				break;
		}
			
		if (searchText.trim() != "")
				SQL += " AND " + searchField.trim() + " LIKE '%" + searchText.trim() + "%'";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, isRetire);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if (isRetire == 0)
					return (rs.getInt(1)-1) / 10 + 1;
				return (rs.getInt(1)) / 10 + 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1; //DB 오류
	}
	

	public ArrayList<UserDTO> getList(String nowPage, int isRetire){
		String SQL = "SELECT userCode, userName, isCategory, isClient, isDocument, isLock, DATEDIFF(DAY, dateOfPassword, GETDATE()) "
				+ "FROM dbo.USERS WHERE isRetire = ? and userCode != 0"
				+ "ORDER BY userCode DESC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		ArrayList<UserDTO> list = new ArrayList<UserDTO>();
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);	
			pstmt.setInt(1, isRetire);
			if (nowPage == null) {
				pstmt.setInt(2, 0);
			} else {
				pstmt.setInt(2, (Integer.parseInt(nowPage) -1) * 10);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				UserDTO user = new UserDTO();
				user.setUserCode(rs.getInt(1));
				user.setUserName(rs.getString(2));
				user.setCategory(rs.getInt(3));
				user.setClient(rs.getInt(4));
				user.setDocument(rs.getInt(5));
				user.setLock(rs.getInt(6));
				user.setDateOfPassword(rs.getString(7));
				list.add(user);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public ArrayList<UserDTO> getList(String nowPage, int isRetire, String ChangeDate){
		String SQL = "SELECT userCode, userName, isCategory, isClient, isDocument, isLock, DATEDIFF(DAY, dateOfPassword, GETDATE()) "
				+ "FROM dbo.USERS WHERE isRetire = ? and userCode != 0 ";
		ArrayList<UserDTO> list = new ArrayList<UserDTO>();

		switch (ChangeDate) {
			case "1":
				SQL += "AND (dateOfPassword BETWEEN DATEADD(DAY, -90, GETDATE()) AND GETDATE()) OR dateOfPassword IS NULL ";
				break;
			case "2":
				SQL += "AND dateOfPassword BETWEEN DATEADD(DAY, -180, GETDATE()) AND DATEADD(DAY, -90, GETDATE()) ";
				break;
			case "3":
				SQL += "AND dateOfPassword < DATEADD(DAY, -180, GETDATE()) ";
				break;
			default:
				break;
		}
		SQL += " ORDER BY userCode DESC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);	
			pstmt.setInt(1, isRetire);
			if (nowPage == null) {
				pstmt.setInt(2, 0);
			} else {
				pstmt.setInt(2, (Integer.parseInt(nowPage) -1) * 10);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				UserDTO user = new UserDTO();
				user.setUserCode(rs.getInt(1));
				user.setUserName(rs.getString(2));
				user.setCategory(rs.getInt(3));
				user.setClient(rs.getInt(4));
				user.setDocument(rs.getInt(5));
				user.setLock(rs.getInt(6));
				user.setDateOfPassword(rs.getString(7));
				list.add(user);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public ArrayList<UserDTO> getSearch(String nowPage, int isRetire, String searchField, String searchOrder, String searchText){
		ArrayList<UserDTO> list = new ArrayList<UserDTO>();
		String SQL = "SELECT userCode, userName, isCategory, isClient, isDocument, isLock, DATEDIFF(DAY, dateOfPassword, GETDATE()) "
				+ "FROM dbo.USERS WHERE isRetire = ? AND userCode != 0 ";
		if (searchText.trim() != "")
				SQL += " AND " + searchField.trim() + " LIKE '%" + searchText.trim() + "%'";
		SQL += "ORDER BY " + searchField.trim() + " " + searchOrder +" OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		try {
			pstmt = conn.prepareStatement(SQL);	
			pstmt.setInt(1, isRetire);
			if (nowPage == null) {
				pstmt.setInt(2, 0);
			} else {
				pstmt.setInt(2, (Integer.parseInt(nowPage) -1) * 10);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				UserDTO user = new UserDTO();
				user.setUserCode(rs.getInt(1));
				user.setUserName(rs.getString(2));
				user.setCategory(rs.getInt(3));
				user.setClient(rs.getInt(4));
				user.setDocument(rs.getInt(5));
				user.setLock(rs.getInt(6));
				user.setDateOfPassword(rs.getString(7));
				list.add(user);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	public ArrayList<UserDTO> getSearch(String nowPage, int isRetire, String ChangeDate, String searchField, String searchOrder, String searchText){
		ArrayList<UserDTO> list = new ArrayList<UserDTO>();
		String SQL = "SELECT userCode, userName, isCategory, isClient, isDocument, isLock, DATEDIFF(DAY, dateOfPassword, GETDATE()) "
				+ "FROM dbo.USERS WHERE isRetire = ? AND userCode != 0 ";
		
		switch (ChangeDate) {
			case "1":
				SQL += "AND (dateOfPassword BETWEEN DATEADD(DAY, -90, GETDATE()) AND GETDATE()) OR dateOfPassword IS NULL ";
				break;
			case "2":
				SQL += "AND dateOfPassword BETWEEN DATEADD(DAY, -180, GETDATE()) AND DATEADD(DAY, -90, GETDATE()) ";
				break;
			case "3":
				SQL += "AND dateOfPassword < DATEADD(DAY, -180, GETDATE()) ";
				break;
			default:
				break;
		}
		
		if (searchText.trim() != "")
				SQL += " AND " + searchField.trim() + " LIKE '%" + searchText.trim() + "%'";
		SQL += "ORDER BY " + searchField.trim() + " " + searchOrder +" OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		try {
			pstmt = conn.prepareStatement(SQL);	
			pstmt.setInt(1, isRetire);
			if (nowPage == null) {
				pstmt.setInt(2, 0);
			} else {
				pstmt.setInt(2, (Integer.parseInt(nowPage) -1) * 10);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				UserDTO user = new UserDTO();
				user.setUserCode(rs.getInt(1));
				user.setUserName(rs.getString(2));
				user.setCategory(rs.getInt(3));
				user.setClient(rs.getInt(4));
				user.setDocument(rs.getInt(5));
				user.setLock(rs.getInt(6));
				user.setDateOfPassword(rs.getString(7));
				list.add(user);
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public void updateLock(int userCode, String status) {
		String SQL = "UPDATE USERS SET isLock=? FROM USERS WHERE userCode=?";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (status.equals("O")) {
				pstmt.setInt(1, 1);
			} else {
				pstmt.setInt(1, 0);
			}
			pstmt.setInt(2, userCode);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int userUpload(String userName, String userID, String userPassword, boolean isCategory, boolean isClient, boolean isDocument) {
		String SQL = "INSERT INTO USERS VALUES (?, ?, ?, null,0,0,0,?,?,?)";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userName);
			pstmt.setString(2, userID);
			pstmt.setString(3, logic.getSHA256(userPassword));
			if (isCategory) {
				pstmt.setInt(4,1);
			} else {
				pstmt.setInt(4,0);
			}
			if (isClient) {
				pstmt.setInt(5,1);
			} else {
				pstmt.setInt(5,0);
			}
			if (isDocument) {
				pstmt.setInt(6,1);
			} else {
				pstmt.setInt(6,0);
			}
			
			pstmt.executeUpdate();
			
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public int userIDCheck(String userID) {
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.USERS WHERE userID = ?;";
		
		if (userID == null)
			return 0;
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				if(rs.getInt(1) == 0)
					return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public int userUpdate(String userID, String userName, String hiddenName, String userPassword, String hiddenPassword,
			String isCategory, String hiddenCategory, String isClient, String hiddenClient, String isDocument, String hiddenDocument) {
		String SQL = "UPDATE USERS SET ";
		int cnt = 1;
		
		if (!userName.equals(hiddenName))
			SQL += "userName=?, ";
		if (!userPassword.equals(hiddenPassword))
			SQL += "userPassword=?, dateOfPassword=NULL, ";
		if (!isCategory.equals(hiddenCategory))
			SQL += "isCategory=?, ";
		if (!isClient.equals(hiddenClient))
			SQL += "isClient=?, ";
		if (!isDocument.equals(hiddenDocument))
			SQL += "isDocument=?, ";
        SQL += "isLock=0";
		SQL += " WHERE userID=?";
		try {
			pstmt = conn.prepareStatement(SQL);
			if (!userName.equals(hiddenName)){
				pstmt.setString(cnt, userName);
				cnt += 1;
			}
			if (!userPassword.equals(hiddenPassword)){
				pstmt.setString(cnt, userPassword);
				cnt += 1;
			}
			if (!isCategory.equals(hiddenCategory)){
				if (isCategory.equals("1")) {
					pstmt.setInt(cnt, 1);
				} else {
					pstmt.setInt(cnt, 0);
				}
				cnt += 1;
			}
			if (!isClient.equals(hiddenClient)){
				if (isClient.equals("1")) {
					pstmt.setInt(cnt, 1);
				} else {
					pstmt.setInt(cnt, 0);
				}
				cnt += 1;
			}
			if (!isDocument.equals(hiddenDocument)){
				if (isDocument.equals("1")) {
					pstmt.setInt(cnt, 1);
				} else {
					pstmt.setInt(cnt, 0);
				}
				cnt += 1;
			}
			pstmt.setString(cnt, userID);
			pstmt.executeUpdate();
			
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void userRetire(int userCode, String isRetire) {
		String SQL = "UPDATE USERS SET isRetire=?, isLock=? FROM USERS WHERE userCode=?";
		try {
			pstmt = conn.prepareStatement(SQL);
			if ("1".equals(isRetire)) {
				pstmt.setInt(1, 1);
				pstmt.setInt(2, 1);
			} else {
				pstmt.setInt(1, 0);
				pstmt.setInt(2, 0);
			}
			pstmt.setInt(3, userCode);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void userClose() {
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
