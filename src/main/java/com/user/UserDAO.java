package com.user;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.myclass.CollectLog;
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
	
	//로그인을 하기 위해 비밀번호와 로그인 실패 횟수를 불러오는 메소드
	public int login(String userID, String userPassword) {
		String SQL = "SELECT userPassword FROM USERS WHERE userID = ?";
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
			errorLogUpload(e);
		}
		return -2; //데이터베이스 오류
	}
	
	//로그인 성공을 기록하고 로그인 실패 횟수를 초기화하는 메소드
	public void loginSuccess(int userCode, String userID) {
		String SQL = "UPDATE USERS SET failOfPassword=0 FROM USERS WHERE userID=?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			pstmt.executeUpdate();
			logUpload(userCode, userID, "user", "login", "로그인 성공");
		} catch (Exception e) {
			logUpload(userCode, "", "user", "error", CollectLog.getLog(e));
		}
	}
	
	//로그인 실패로 로그인 실패 횟수를 증가시키고 실패 횟수가 5회에 도달할 경우 계정을 잠금 상태로 만드는 메소드
	public void loginFail(int userCode, String userID, int failOfPassword) {
	    try {
	    	String SQL;
	    	
	    	if (failOfPassword == 4){
	    		SQL = "UPDATE USERS SET failOfPassword = failOfPassword + 1, isLock = 1 WHERE userID = ?";
	    	} else {
	    		SQL = "UPDATE USERS SET failOfPassword = failOfPassword + 1 WHERE userID = ?";
	    	}

	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setString(1, userID);
	        pstmt.executeUpdate();
	        if (failOfPassword == 4) {
				logUpload(userCode, userID, "user", "update", "로그인 5회 실패로 계정 잠금");
	        }

	    } catch (Exception e) {
			logUpload(userCode, "", "user", "error", CollectLog.getLog(e));
	    }
	}
	
	//사번으로 사용자 정보를 가져오는 메소드 -> 주로 사용자 정보를 갱신할 때 해당 정보를 불러오는 용도
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
			errorLogUpload(e);
		}
		return null; //데이터베이스 오류
	}
	
	//사용자 ID로 사용자 정보를 가져오는 메소드 -> 로그인 시 세션에 사용자 정보를 저장하는 용도
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
			errorLogUpload(e);
		}
		return null; //데이터베이스 오류
	}
	
	//사용자 아이디로 사번 가져오는 메소드
	public int getCode(String userID) {
		String SQL = "SELECT userCode FROM USERS WHERE userID = ?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getInt(1); //사번
			}
		} catch (Exception e) {
			errorLogUpload(e);
		}
		return -1; //데이터베이스 오류
	}

	//사용자 사번으로 사용자 아이디를 가져오는 메소드
	public String getID(int userCode) {
		String SQL = "SELECT userID FROM USERS WHERE userCode = ?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, userCode);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getString(1); //사용자 아이디
			}
		} catch (Exception e) {
			errorLogUpload(e);
		}
		return null; //데이터베이스 오류
	}
	
	//DB의 비밀번호를 바꿀 때 현재 비밀번호가 맞는지 아닌지 확인하고 passwordUpdate를 실행하는 메소드
	public int passwordRenew(String userID, String nowPassword, String newPassword) {
		String SQL = "SELECT userPassword FROM USERS WHERE userID = ?";
		if(newPassword.equals(nowPassword))
				return -1; //신규 비밀번호와 기존 비밀번호가 일치

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				if(rs.getString(1).equals(logic.getSHA256(nowPassword))) {
					passwordUpdate(userID, newPassword);
					logUpload(getCode(userID), userID, "user", "update", "비밀번호 변경");
					return 1; //변경 성공
				}
			}
			return 0; //기존 비밀번호 불일치
		} catch (Exception e) {
			errorLogUpload(e);
		}
		return -2; //데이터베이스 오류
	}
	
	//passwordRenew가 모든 검사를 마치면 비밀번호와 비밀번호 변경일을 바꾸는 메소드
	private void passwordUpdate(String userID, String newPassword) {
		String SQL = "UPDATE USERS SET userPassword=?, dateOfPassword=GETDATE(), failOfPassword=0 FROM USERS WHERE userID=?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, logic.getSHA256(newPassword));
			pstmt.setString(2, userID);
			pstmt.executeUpdate();
		} catch (Exception e) {
			errorLogUpload(e);
		}
	}

	//SQL query문의 Where절 조합기
	private String filterSQL(String SQL, String dateOfPassword, String isLock, String isRetire, String searchField, String searchText) {
		if (dateOfPassword == null && isLock == null && isRetire == null && searchField == null && searchText == null) {
			return SQL + " WHERE userCode != 0 "; //통상적인 Where절을 반환
		}
		
		StringBuilder query = new StringBuilder(SQL);
		ArrayList<String> conditions = new ArrayList<String>();
		
		switch (dateOfPassword) {
		case "1":
			conditions.add(" (dateOfPassword BETWEEN DATEADD(DAY, -90, GETDATE()) AND GETDATE()) OR dateOfPassword IS NULL ");
			break;
		case "2":
			conditions.add(" dateOfPassword BETWEEN DATEADD(DAY, -180, GETDATE()) AND DATEADD(DAY, -90, GETDATE()) ");
			break;
			
		case "3":
			conditions.add(" dateOfPassword < DATEADD(DAY, -180, GETDATE()) ");
			break;
		default:
			break;
		}

		if ("0".equals(isLock) || "1".equals(isLock)) {
			conditions.add(" isLock="+isLock+" ");
		}

		if ("0".equals(isRetire) || "1".equals(isRetire)) {
			conditions.add(" isRetire="+isRetire+" ");
		}

		
		if (searchField != null && searchText != null) {
			conditions.add(" " + searchField.trim() + " LIKE '%" + searchText.trim() + "%' ");
		}
		
		conditions.add(" userCode != 0 ");
		
		if (!conditions.isEmpty()) {
		    query.append(" WHERE ").append(String.join(" AND ", conditions));
		}
		
		return query.toString(); //조합된 Where절을 반환
	}

	//최대 페이지 수 구하기
	public int maxPage(String dateOfPassword, String isLock, String isRetire, String searchField, String searchText) { //다음 글 가지고 오기
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.USERS ";
			
		SQL = filterSQL(SQL, dateOfPassword, isLock, isRetire, searchField, searchText);
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return (rs.getInt(1)-1) / 10 + 1; //최대 페이지
			}
		} catch(Exception e) {
			errorLogUpload(e);
		}
		return -1; //DB 오류
	}

	//사용자들의 정보 리스트를 반환하는 메소드
	public ArrayList<UserDTO> getList(String dateOfPassword, String isLock, String isRetire, 
			String nowPage, String searchField, String searchOrder, String searchText){
		ArrayList<UserDTO> list = new ArrayList<UserDTO>();
		String SQL = "SELECT userCode, userName, isCategory, isClient, isDocument, isLock, isRetire, DATEDIFF(DAY, dateOfPassword, GETDATE()) "
				+ "FROM dbo.USERS ";

		SQL = filterSQL(SQL, dateOfPassword, isLock, isRetire, searchField, searchText);
		
		if (searchField != null && searchOrder != null && searchText != null) {
			SQL += "ORDER BY " + searchField.trim() + " " + searchOrder +" OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		} else {
			SQL += "ORDER BY userCode DESC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		}
		
		try {
			pstmt = conn.prepareStatement(SQL);	
			if (nowPage == null) {
				pstmt.setInt(1, 0);
			} else {
				pstmt.setInt(1, (Integer.parseInt(nowPage) -1) * 10);
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
				user.setRetire(rs.getInt(7));
				user.setDateOfPassword(rs.getString(8));
				list.add(user);
			}			
		} catch(Exception e) {
			errorLogUpload(e);
		}
		
		return list; //리스트 반환 -> 결과 없으면 빈 리스트
	}

	//엑셀 시트를 다운로드 하기 위한 정보를 반환하는 메소드
	public ArrayList<UserDTO> getExcel(int userCode, String dateOfPassword, String isLock, String isRetire, 
			String searchField, String searchOrder, String searchText){
		ArrayList<UserDTO> list = new ArrayList<UserDTO>();
		String SQL = "SELECT userCode, userName, isCategory, isClient, isDocument, isLock, isRetire, DATEDIFF(DAY, dateOfPassword, GETDATE()) "
				+ "FROM dbo.USERS ";
		
		SQL = filterSQL(SQL, dateOfPassword, isLock, isRetire, searchField, searchText);
		
		if (searchField != null && searchText != null && searchOrder != null) {
			SQL += "ORDER BY " + searchField.trim() + " " + searchOrder +" ;";
		} 
		
		try {
			pstmt = conn.prepareStatement(SQL);
			
			rs = pstmt.executeQuery();
			logUpload(userCode, "", "user", "download", "사용자 리스트 엑셀 다운로드");
			
			while (rs.next()) {
				UserDTO user = new UserDTO();
				user.setUserCode(rs.getInt(1));
				user.setUserName(rs.getString(2));
				user.setCategory(rs.getInt(3));
				user.setClient(rs.getInt(4));
				user.setDocument(rs.getInt(5));
				user.setLock(rs.getInt(6));
				user.setRetire(rs.getInt(7));
				user.setDateOfPassword(rs.getString(8));
				list.add(user);
			}			
		} catch(Exception e) {
			logUpload(userCode, "", "user", "error", CollectLog.getLog(e));
		}
		
		return list; //리스트 반환 -> 결과 없으면 빈 리스트
	}
	
	//사용자 계정 잠금 상태를 변경하는 메소드
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
			logUpload(userCode, getID(userCode), "user", "update", "계정 잠금 상태 변경");
		} catch (Exception e) {
			logUpload(userCode, "", "user", "error", CollectLog.getLog(e));
		}
	}
	
	//새로운 사용자를 등록하는 메소드
	public int userUpload(String userName, String userID, String userPassword, boolean isCategory, boolean isClient, boolean isDocument) {
		String SQL = "INSERT INTO USERS VALUES (?, ?, ?, null,0,0,0,?,?,?)";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userName);
			pstmt.setString(2, userID);
			pstmt.setString(3, userPassword);
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
			logUpload(getCode(userID), userID, "user", "create", "신규 계정 등록");
			
			return 1; //등록 성공
		} catch (Exception e) {
			errorLogUpload(e);
		}
		
		return 0; //등록 실패
	}
	
	//아이디 중복 여부를 확인하는 메소드
	public int userIDCheck(String userID) {
		String SQL = "SELECT COUNT(*) AS cnt FROM dbo.USERS WHERE userID = ?;";
		
		if (userID == null)
			return 0; //사용 불가능
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				if(rs.getInt(1) == 0)
					return 1; //사용 가능
			}
		} catch (Exception e) {
			errorLogUpload(e);
		}
		
		return 0; //사용 불가능
	}
	
	//사용자 정보를 갱신하는 메소드
	public int userUpdate(String userID, String userName, String hiddenName, String userPassword, String hiddenPassword,
			String isCategory, String hiddenCategory, String isClient, String hiddenClient, String isDocument, String hiddenDocument) {
		String SQL = "UPDATE USERS SET ";
		int cnt = 1;
		
		if (!userName.equals(hiddenName))
			SQL += "userName=?, ";
		if (!userPassword.equals(hiddenPassword))
			SQL += "userPassword=?, failOfPassword=0, dateOfPassword=NULL, ";
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
			logUpload(getCode(userID), userID, "user", "update", "계정 정보 변경");
			
			return 1; //사용자 정보 갱신 성공
		} catch (Exception e) {
			logUpload(getCode(userID), "", "user", "error", CollectLog.getLog(e));
		}
		return 0; //갱신 실패
	}
	
	//사용자 퇴사 여부를 변경하는 메소드
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
			logUpload(userCode, getID(userCode), "user", "update", "계정 재직 상태 변경");
		} catch (Exception e) {
			logUpload(userCode, "", "user", "error", CollectLog.getLog(e));
		}
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
				+ " VALUES (NULL, '', 'user', 'error', ?);";

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, CollectLog.getLog(error));
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//접속한 DB의 연결을 끝는 메소드
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
