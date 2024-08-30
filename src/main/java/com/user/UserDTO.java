package com.user;

public class UserDTO {
	private int userCode;
	private String userName;
	private String userID;
	private String userPassword;
	private String dateOfPassword;
	private int failOfPassword;
	private boolean isLock;
	private boolean isRetire;
	private boolean isCategory;
	private boolean isClient;
	private boolean isDocument;
	
	public int getUserCode() {
		return userCode;
	}
	public void setUserCode(int userCode) {
		this.userCode = userCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getDateOfPassword() {
		return dateOfPassword;
	}
	public void setDateOfPassword(String dateOfPassword) {
		this.dateOfPassword = dateOfPassword;
	}
	public boolean isLock() {
		return isLock;
	}
	public boolean isRetire() {
		return isRetire;
	}
	public boolean isCategory() {
		return isCategory;
	}
	public boolean isClient() {
		return isClient;
	}
	public boolean isDocument() {
		return isDocument;
	}
	public boolean getIsLock() {
		return isLock;
	}
	public boolean getIsRetire() {
		return isRetire;
	}
	public boolean getIsCategory() {
		return isCategory;
	}
	public boolean getIsClient() {
		return isClient;
	}
	public boolean getIsDocument() {
		return isDocument;
	}
	public int getFailOfPassword() {
		return failOfPassword;
	}
	public void setFailOfPassword(int failOfPassword) {
		this.failOfPassword = failOfPassword;
	}
	public void setLock(int isLock) {
		this.isLock = isLock == 1;
	}
	public void setRetire(int isRetire) {
		this.isRetire = isRetire == 1;
	}
	public void setCategory(int isCategory) {
		this.isCategory = isCategory == 1;
	}
	public void setClient(int isClient) {
		this.isClient = isClient == 1;
	}
	public void setDocument(int isDocument) {
		this.isDocument = isDocument == 1;
	}
}
