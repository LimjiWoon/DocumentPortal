package com.client;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.ArrayList;

import com.myclass.CollectLog;



public class ClientDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private ClientDTO client;
	
	public ClientDAO() {
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
	private String filterSQL(String SQL, String startDate, String endDate, String isUse, String searchField, String searchText) {
		if (startDate == null && endDate == null && isUse == null && searchField == null && searchText == null) {
			return SQL;
		} 

		StringBuilder query = new StringBuilder(SQL);
		ArrayList<String> conditions = new ArrayList<String>();
		
		if (startDate != null && endDate != null) {
			conditions.add(" dateOfUpdate BETWEEN '"+startDate+"' AND DATEADD(day, 1, '"+endDate+"') ");
		} else if (startDate != null) {
			conditions.add(" dateOfUpdate > '"+startDate+"' ");
		} else if (endDate != null) {
			conditions.add(" dateOfUpdate < DATEADD(day, 1, '"+endDate+"') ");
		}
		
		if ("0".equals(isUse)) {
			conditions.add(" isUse = 0 ");
		} else if ("1".equals(isUse)) {
			conditions.add(" isUse = 1 ");
		}
		
		if (searchField != null && searchText != null) {
			conditions.add(" " + searchField.trim() + " LIKE '%" + searchText.trim() + "%' ");
		}
		
		if (!conditions.isEmpty()) {
		    query.append(" WHERE ").append(String.join(" AND ", conditions));
		}
		
		return query.toString(); //조합된 Where절을 반환
	}
	
	//최대 페이지 수 구하기
	public int maxPage(String startDate, String endDate, String isUse, String searchField, String searchText) { //다음 글 가지고 오기
		String SQL = "SELECT COUNT(*) AS cnt "
				+ "FROM  dbo.CLIENTS c "
				+ "LEFT JOIN dbo.USERS u ON c.userCode = u.userCode ";

		SQL = filterSQL(SQL, startDate, endDate, isUse, searchField, searchText);
		
		try {
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				return (rs.getInt(1) - 1) / 10 + 1; //최대 페이지
			}
		} catch(Exception e) {
			errorLogUpload(e);
		}
		return -1; //DB 오류
	}


	//select바의 정보를 가져오기 위한 getList() -> 코드와 이름만을 가져온다
	public ArrayList<ClientDTO> getList(){
		ArrayList<ClientDTO> list = new ArrayList<ClientDTO>();
		String SQL = "SELECT clientCode, clientName FROM  dbo.CLIENTS "
				+ "WHERE isUse=1 ORDER BY clientName ASC;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				client = new ClientDTO();
				client.setClientCode(rs.getInt(1));
				client.setClientName(rs.getString(2));
				list.add(client);
			}			
		} catch(Exception e) {
			errorLogUpload(e);
		}
		return list; //리스트 반환 -> 결과 없으면 빈 리스트
	}
	

	//페이지에 띄울 정보를 가져오기 위한 getList() -> 모든 정보를 가져온다
	public ArrayList<ClientDTO> getList(String startDate, String endDate, String isUse, String nowPage, String searchField, String searchOrder, String searchText){
		ArrayList<ClientDTO> list = new ArrayList<ClientDTO>();
		String SQL = "SELECT c.clientCode, c.clientName, u.userName, c.dateOfUpdate, c.isUse, c.clientContent "
				+ "FROM  dbo.CLIENTS c "
				+ "LEFT JOIN dbo.USERS u ON c.userCode = u.userCode ";
		
		SQL = filterSQL(SQL, startDate, endDate, isUse, searchField, searchText);

		if (searchField != null && searchOrder != null && searchText != null) {
			SQL += "ORDER BY " + searchField.trim() + " " + searchOrder +" OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
		} else {
			SQL += " ORDER BY c.clientCode DESC OFFSET ? ROWS FETCH NEXT 10 ROWS ONLY;";
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
				client = new ClientDTO();
				client.setClientCode(rs.getInt(1));
				client.setClientName(rs.getString(2));
				client.setUserName(rs.getString(3));
				client.setDateOfUpdate(rs.getString(4));
				client.setUse(rs.getInt(5));
				client.setClientContent(rs.getString(6));
				list.add(client);
			}			
		} catch(Exception e) {
			errorLogUpload(e);
		}
		
		return list; //리스트 반환 -> 결과 없으면 빈 리스트
	}

	//엑셀 시트를 다운로드 하기 위한 정보를 반환하는 메소드
	public ArrayList<ClientDTO> getExcel(int userCode, String startDate, String endDate, String isUse, String searchField, String searchOrder, String searchText){
		String SQL = "SELECT c.clientCode, c.clientName, u.userName, c.dateOfUpdate, c.isUse, c.clientContent "
				+ "FROM  dbo.CLIENTS c "
				+ "LEFT JOIN dbo.USERS u ON c.userCode = u.userCode ";
		
		SQL = filterSQL(SQL, startDate, endDate, isUse, searchField, searchText);
		
		if (searchField != null && searchText != null && searchOrder != null) {
			SQL += "ORDER BY " + searchField.trim() + " " + searchOrder +" ;";
		}
		
		ArrayList<ClientDTO> list = new ArrayList<ClientDTO>();
		
		try {
			pstmt = conn.prepareStatement(SQL);
			
			rs = pstmt.executeQuery();
			logUpload(userCode, "", "client", "download", "고객사 리스트 엑셀 다운로드");
			while (rs.next()) {
				client = new ClientDTO();
				client.setClientCode(rs.getInt(1));
				client.setClientName(rs.getString(2));
				client.setUserName(rs.getString(3));
				client.setDateOfUpdate(rs.getString(4));
				client.setUse(rs.getInt(5));
				client.setClientContent(rs.getString(6));
				list.add(client);
			}			
		} catch(Exception e) {
			logUpload(userCode, "", "client", "error", CollectLog.getLog(e));
		}
		
		return list; //리스트 반환 -> 결과 없으면 빈 리스트
	}

	//해당 고객사의 사용 유무를 변경하는 메소드
	public void updateUse(int clientCode, String status, int userCode, String clientName) {
		String SQL = "UPDATE CLIENTS SET isUSE=? FROM CLIENTS WHERE clientCode=?;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (status.equals("O")) {
				pstmt.setInt(1, 1);
			} else {
				pstmt.setInt(1, 0);
			}
			pstmt.setInt(2, clientCode);
			pstmt.executeUpdate();
			logUpload(userCode, clientName, "client", "update", "사용유무 변경");
		} catch (Exception e) {
			logUpload(userCode, "", "client", "error", CollectLog.getLog(e));
		}
	}
	
	//새로운 고객사를 등록하는 메소드
	public int clientUpload(String clientName, String clientContent, int userCode) {
		String SQL = "INSERT INTO dbo.CLIENTS (clientName, clientContent, userCode) "
				+ "VALUES (?, ?, ?);";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, clientName);
			pstmt.setString(2, clientContent);
			pstmt.setInt(3, userCode);

			pstmt.executeUpdate();
			logUpload(userCode, clientName, "client", "create", "신규 고객사 생성");
			
			return 1; //등록 성공
		} catch(Exception e) {
			logUpload(userCode, "", "client", "error", CollectLog.getLog(e));
		}
		
		return -1; //등록 실패
	} 

	//고객사의 이름이 중복되지 않는지 확인하는 메소드
	public boolean clientUniqueName(String clientName) {
		String SQL = "SELECT clientName FROM dbo.CLIENTS WHERE clientName=?;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, clientName);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				return true; // 중복 없음
			}	
		} catch(Exception e) {
			errorLogUpload(e);
		}

		
		return false; //중복 있음
	}
	
	//고객사 이름으로 고객사 코드를 가져오는 메소드
	public String getClientCode(String clientName) {
		String SQL = "SELECT clientCode FROM dbo.CLIENTS WHERE clientName=?;";
		
		if(clientName == null)
			return null; //입력값 오류
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, clientName);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getString(1); //고객사 있음
			}	
		} catch(Exception e) {
			errorLogUpload(e);
		}

		return null; //고객사 없음
	}
	

	//고객사 코드로 고객사 이름을 가져오는 메소드
	public String getClientName(String clientCode) {
		String SQL = "SELECT clientName FROM dbo.CLIENTS WHERE clientCode=?;";
		
		if (clientCode == null)
			return null; //입력값 오류
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, Integer.parseInt(clientCode));

			rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getString(1); //고객사 있음
			}	
		} catch(Exception e) {
			errorLogUpload(e);
		}
		
		return null; //고객사 없음
	}
	
	//특정 고객사의 정보를 가져오는 메소드
	public ClientDTO getClientInfo(int clientCode) {
		ClientDTO client = new ClientDTO();
		String SQL = "SELECT clientName, clientContent FROM dbo.CLIENTS WHERE clientCode=?;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, clientCode);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				client.setClientName(rs.getString(1));
				client.setClientContent(rs.getString(2));
				client.setClientCode(clientCode);
				return client; //고객사 있음
			}	
		} catch(Exception e) {
			errorLogUpload(e);
		}
		
		return null; //고객사 없음
	}
	
	//고객사 정보를 갱신하는 메소드
	public int clientUpdate(int clientCode, String clientName, String hiddenClientName, 
			String clientContent, String hiddenClientContent, int userCode) {
		String SQL = "UPDATE dbo.CLIENTS SET dateOfUpdate=GETDATE()";
		boolean isName = clientName.equals(hiddenClientName);
		boolean isContent = clientContent.equals(hiddenClientContent);
		int cnt = 1;
		String logContent = "";
		
		if (isName && isContent)
			return 0; //기존 내용과 동일함
		
		if (!isName) 
			SQL += ", clientName=?";
		if (!isContent) 
			SQL += ", clientContent=?";
		
		SQL += " WHERE clientCode=?;";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			if (!isName) {
				pstmt.setString(cnt, clientName);
				logContent += " 고객사명: " + hiddenClientName + "-&gt;" + clientName;
				cnt += 1;
			}
			if (!isContent) {
				pstmt.setString(cnt, clientContent);
				logContent += "  설명 변경";
				cnt += 1;
			}
			
			pstmt.setInt(cnt, clientCode);
			pstmt.executeUpdate();
			logUpload(userCode, hiddenClientName, "client", "update", logContent);
			return 1; //갱신 성공
		} catch(Exception e) {
			logUpload(userCode, "", "client", "error", CollectLog.getLog(e));
		}
		
		return -1; //갱신 실패
	}
	
	//해당 고객사가 사용 중인 문서 목록 리스트를 반환하는 메소드
	public ArrayList<String> getModal(String clientCode){
		ArrayList<String> list = new ArrayList<String>();
		
        String SQL = "SELECT c.categoryName "
        		+ "FROM dbo.FILES f "
        		+ "LEFT JOIN dbo.CATEGORIES c ON f.categoryCode = c.categoryCode "
        		+ "WHERE clientCode=? "
        		+ "GROUP BY f.clientCode, f.categoryCode, c.categoryName;";
        
        if (clientCode == null) {
        	return list; //입력값 오류
        }

        try {
            pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, Integer.parseInt(clientCode));

            // 쿼리 실행
            rs = pstmt.executeQuery();
            while (rs.next()) {
				list.add(rs.getString(1));
            }
        } catch (Exception e) {
			errorLogUpload(e);
        }
		
		return list; //리스트 반환 -> 결과 없을 시 빈 리스트 반환
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
				+ " VALUES (NULL, '', 'client', 'error', ?);";

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, CollectLog.getLog(error));
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//접속한 DB의 연결을 끝는 메소드
	public void clientClose() {
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
