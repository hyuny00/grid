package com.futechsoft.framework.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.futechsoft.admin.user.vo.User;
import com.futechsoft.framework.exception.BizException;
import com.futechsoft.framework.exception.ErrorCode;
import com.futechsoft.framework.security.service.SecurityService;

public class LoginFailureHandler implements AuthenticationFailureHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginFailureHandler.class);

	@Autowired
	private SecurityService securityService;

	private String loginId;
	private String loginPwd;
	private String errorMsg;
	private String defaultFailureUrl;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

		String userId = request.getParameter(loginId);
		String password = request.getParameter(loginPwd);
		String errorMsg = "";

		if (exception instanceof BadCredentialsException) {

			errorMsg =ErrorCode.BAD_CREDENTIALS.getMessage();

			try {
				// securityService.plusFailCnt(userId);
				User user = securityService.getUserInfo(userId);

				if (user != null) {

					if (user.getLoginFailCnt() >= 3) {
						try {
							securityService.disabledUser(userId);
						} catch (BizException e) {
							LOGGER.error(e.toString());
						} catch (Exception e) {
//							e.printStackTrace();
							LOGGER.error(e.toString());
						}
					}
				}
			} catch (BizException e) {
				LOGGER.error(e.toString());
			} catch (Exception e) {
//				e.printStackTrace();
				LOGGER.error(e.toString());
			}

		}else {
			errorMsg =exception.getMessage();
		}

		request.setAttribute(loginId, userId);
		request.setAttribute(loginPwd, password);
		request.setAttribute("errorMsg", errorMsg);


		request.getRequestDispatcher(defaultFailureUrl).forward(request, response);

	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getDefaultFailureUrl() {
		return defaultFailureUrl;
	}

	public void setDefaultFailureUrl(String defaultFailureUrl) {
		this.defaultFailureUrl = defaultFailureUrl;
	}


	public void gpkiOnAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

		String errorMsg = "";

		errorMsg =exception.getMessage();


		request.setAttribute("errorMsg", errorMsg);


		request.getRequestDispatcher(defaultFailureUrl).forward(request, response);

	}

}
