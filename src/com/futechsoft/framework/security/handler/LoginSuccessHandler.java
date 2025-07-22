package com.futechsoft.framework.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.futechsoft.framework.security.service.SecurityService;
import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginSuccessHandler.class);

	private RequestCache requestCache = new HttpSessionRequestCache();
	private RedirectStrategy redirectStratgy = new DefaultRedirectStrategy();

	@Autowired
	private SecurityService securityService;

	//@Autowired
	//private LoginLogService loginLogService;

	private String loginId;
	private String defaultUrl;

	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		clearAuthenticationAttributes(request);

		String userId = request.getParameter(loginId);
		try {
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
			params.put("userNo", SecurityUtil.getUserNo());
			params.put("userIp", CommonUtil.getClientIP(request));
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
