package com.myclass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public class PasswordLogic {

	public String getSHA256(String userPassword) {
		
		String sha = "";
		
		try {
			MessageDigest sh = MessageDigest.getInstance("SHA-256");
			sh.update(userPassword.getBytes());
			byte[] byteData = sh.digest();
			StringBuilder sb = new StringBuilder();
			for (byte byteDatum : byteData) {
				sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
			}
			
			sha=sb.toString();
			
		} catch (NoSuchAlgorithmException e) {
			System.out.println("암호화 에러 - NoSuchAlgorithmException");
			
		}
		
		return sha;
	}
	
	public int getPasswordChangeDay(String dateOfPassword) {
		
		if(dateOfPassword == null)
			return 91;
		
		try {
			LocalDate now = LocalDate.now();
			LocalDate update = LocalDate.parse(dateOfPassword);
			
			return now.getDayOfYear() - update.getDayOfYear() + (now.getYear() - update.getYear())*365;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 91;
	}
}