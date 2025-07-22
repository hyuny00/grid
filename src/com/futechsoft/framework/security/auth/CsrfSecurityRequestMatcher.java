package com.futechsoft.framework.security.auth;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;


@Component("framework.security.auth.CsrfSecurityRequestMatcher")
public class CsrfSecurityRequestMatcher implements RequestMatcher {

	private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

	private RequestMatcher wsMatcher = new RegexRequestMatcher("/ws/.*", null, true);
	private RequestMatcher ckUploadMatcher = new RegexRequestMatcher("/file/uploadImage.*", null, true);
	private RequestMatcher registCertMatcher = new RegexRequestMatcher("/admin/user/registCert", null, true);
	private RequestMatcher certLoginMatcher = new RegexRequestMatcher("/login/certLogin", null, true);
	private RequestMatcher LoginMatcher = new RegexRequestMatcher("/login/loginPage", null, true);

	//비로그인 사용자등록
	private RequestMatcher idDupChk = new RegexRequestMatcher("/admin/user/popup/idDupChk", null, true);
	private RequestMatcher userForm = new RegexRequestMatcher("/admin/user/userAppl/insertUser", null, true);

	private RequestMatcher authMatcher = new RegexRequestMatcher("/auth", null, true);
	private RequestMatcher jwtAuthMatcher = new RegexRequestMatcher("/loginSessAndJwt", null, true);


	private RequestMatcher orMatcher = new OrRequestMatcher(wsMatcher, ckUploadMatcher, registCertMatcher, certLoginMatcher, idDupChk, userForm, authMatcher,LoginMatcher,jwtAuthMatcher);

	@Override
	public boolean matches(HttpServletRequest request) {

		if (allowedMethods.matcher(request.getMethod()).matches()) {

			return false;

		}
		return !orMatcher.matches(request);
	}

}
