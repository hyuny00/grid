package com.futechsoft.framework.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class CustomLogoutJwtSuccessHandler extends SimpleUrlLogoutSuccessHandler {
	
	private String defaultUrl;
	
	 @Value("${spring.profiles.active:default}")
     private String activeProfile;


	 @Override
	    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
	        // JWT 토큰을 저장한 쿠키 삭제 (쿠키 만료 시간을 과거로 설정)
	        deleteJwtCookie(response);

	        // 로그아웃 후 처리 (예: 기본 URL 리디렉션)
	        setDefaultTargetUrl(defaultUrl);
	        super.onLogoutSuccess(request, response, authentication);
	    }

	    private void deleteJwtCookie(HttpServletResponse response) {
	        // JWT 토큰을 저장한 쿠키 이름 (예: "JWT")
	        Cookie cookie = new Cookie("JWT", null);  // 쿠키 값 비우기
	        cookie.setHttpOnly(true);  // 보안상 HttpOnly 설정
	        
	        if(activeProfile.equals("prod")) {
	        	System.out.println("Current profile: " + activeProfile);
	        	cookie.setSecure(true);    // HTTPS 프로토콜에서만 쿠키 전송 (프로덕션 환경에서만 활성화)
	        }
	        
	        cookie.setPath("/");  // 쿠키의 경로 설정 (서버의 전체 경로에 대해 유효하도록 설정)
	        cookie.setMaxAge(0);  // 만료 시간을 0으로 설정하여 삭제
	        response.addCookie(cookie);  // 쿠키를 응답에 추가
	    }

	    public String getDefaultUrl() {
	        return defaultUrl;
	    }

	    public void setDefaultUrl(String defaultUrl) {
	        this.defaultUrl = defaultUrl;
	    }

}
