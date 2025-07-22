package com.futechsoft.framework.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import com.futechsoft.framework.exception.BizException;
import com.futechsoft.framework.security.auth.CustomUserDetailsService;
import com.futechsoft.framework.security.auth.JwtTokenProvider;
import com.futechsoft.framework.security.service.SecurityService;
import com.futechsoft.framework.security.vo.CustomUserDetails;
import com.futechsoft.framework.util.FtMap;

@Component
public class LoginSuccessJwtHandler implements AuthenticationSuccessHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginSuccessHandler.class);

	private RequestCache requestCache = new HttpSessionRequestCache();
	private RedirectStrategy redirectStratgy = new DefaultRedirectStrategy();

	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private  JwtTokenProvider jwtTokenProvider;
	

	
	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	 @Value("${spring.profiles.active:default}")
     private String activeProfile;

	//@Autowired
	//private LoginLogService loginLogService;

	private String loginId;
	private String defaultUrl;

	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		clearAuthenticationAttributes(request);

		String userId = request.getParameter(loginId);
		
		
		try {
			
			CustomUserDetails customUserDetails=(CustomUserDetails)userDetailsService.loadUserByUsername(userId);
			
			 // JWT 토큰 생성
	        String token = jwtTokenProvider.createToken(userId,customUserDetails.getAuthorities());

	        // JWT 토큰을 쿠키에 설정
	        Cookie cookie = new Cookie("JWT", token);
	        cookie.setHttpOnly(true);  // JavaScript에서 접근할 수 없도록 설정
	        if(activeProfile.equals("prod")) {
	        	System.out.println("Current profile: " + activeProfile);
	        	cookie.setSecure(true);    // HTTPS 프로토콜에서만 쿠키 전송 (프로덕션 환경에서만 활성화)
	        }
	        cookie.setPath("/");       // 전체 도메인에서 쿠키 사용
	        cookie.setMaxAge(3600);    // 1시간 동안 유효

	        response.addCookie(cookie);
	        
			securityService.resetFailCnt(userId);
			
			
		} catch (BizException e) {
			LOGGER.error(e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			LOGGER.error(e.toString());
		}

		/**
		 * 로그인이력저장
		 */
		FtMap params=new FtMap(request.getParameterMap());
		try {
		//	params.put("userNo", SecurityUtil.getUserNo());
		//	params.put("userIp", CommonUtil.getClientIP(request));
			//loginLogService.insertLoginLog(params);
		} catch (BizException e) {
			LOGGER.error(e.toString());
		} catch (Exception e) {
//			e.printStackTrace();
			LOGGER.error(e.toString());
		}

		resultRedirectStrategy(request, response, authentication);

	}

	protected void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null)
			return;
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}

	protected void resultRedirectStrategy(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		String[] ext= {".css", ".js", ".jpg", ".png", ".gif", ".exe", "/auth"};


		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if (savedRequest != null) {
			String targetUrl = savedRequest.getRedirectUrl();

			if(!StringUtils.endsWithAny(targetUrl, ext)) {
				redirectStratgy.sendRedirect(request, response, targetUrl);
			}else {
				redirectStratgy.sendRedirect(request, response, defaultUrl);
			}

		} else {
			redirectStratgy.sendRedirect(request, response, defaultUrl);
		}

	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getDefaultUrl() {
		return defaultUrl;
	}

	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}

}
