package com.myclass;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CollectLog {
    // 스택 트레이스를 문자열로 변환하는 메서드
    private static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);  // 스택 트레이스를 문자열로 변환
        return stringWriter.toString();
    }
	
    public static String getLog(Exception e) {
        // 현재 날짜와 시간 가져오기
    	String errorMessage = getStackTraceAsString(e);
        return errorMessage;
    }
}
