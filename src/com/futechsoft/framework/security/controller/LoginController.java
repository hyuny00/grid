package com.futechsoft.framework.security.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.futechsoft.framework.exception.ErrorCode;
import com.futechsoft.framework.security.auth.CustomUserDetailsService;
import com.futechsoft.framework.security.handler.LoginFailureHandler;
import com.futechsoft.framework.security.handler.LoginSuccessHandler;
import com.futechsoft.framework.security.handler.LoginSuccessJwtHandler;
import com.futechsoft.framework.security.vo.CustomUserDetails;

/**
 * <PRE>
 *
 * </PRE>
 *
 * @author futech
 * @version $Revision$
 */
@Controller
public class LoginController {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	//@Autowired
	//private SessionRegistry sessionRegistry;

	@RequestMapping("/login/loginPage")
	public String loginPage() throws Exception {
		return "framework/login/login";
	}

	@Autowired
	private LoginFailureHandler  loginFailureHandler;

	@Autowired
	private LoginSuccessHandler loginSuccessHandler;
	
	
	@Autowired
	private LoginSuccessJwtHandler loginSuccessJwtHandler;
	
	

	@RequestMapping("/main")
	public String main() throws Exception {

		return "tiles:main";
		
	}

	@RequestMapping(value = "/loginWithoutSecurity")
	public void loginWithoutSecurity(String userId, String password, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(userId);


			if (!matchPassword(password, user.getPassword())) {
				throw new BadCredentialsException(ErrorCode.BAD_CREDENTIALS.getMessage());
			}

			UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
			SecurityContext securityContext =SecurityContextHolder.getContext();
			securityContext.setAuthentication(upat);

			HttpSession session=request.getSession(true);
			session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

			//sessionRegistry.registerNewSession(session.getId(), user);

			loginSuccessHandler.setDefaultUrl("/main");
			loginSuccessHandler.setLoginId("userId");
			loginSuccessHandler.onAuthenticationSuccess(request, response, securityContext.getAuthentication());

		}catch(Exception e) {
			loginFailureHandler.setDefaultFailureUrl("/login/loginPage?error");
			loginFailureHandler.setLoginId("userId");
			loginFailureHandler.onAuthenticationFailure(request, response,  new UsernameNotFoundException(e.getMessage()));
		}

	}

	@RequestMapping("/autoLogout")
	public String autoLogout() throws Exception {

		return "framework/login/autoLogout";
	}



	private boolean matchPassword(String loginPwd, String password) {
		return (passwordEncoder.matches(loginPwd, password));
	}
	
	
	@RequestMapping(value = "/loginSessAndJwt")
	public void loginWithoutSecurityJwt(String userId, String password, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		System.out.println("userIduserId...."+userId);
		
		try {
			CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(userId);


			if (!matchPassword(password, user.getPassword())) {
				throw new BadCredentialsException(ErrorCode.BAD_CREDENTIALS.getMessage());
			}
			
			
			// Authentication 객체 생성
	        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

	        // SecurityContext에 Authentication 설정
	        SecurityContext securityContext = SecurityContextHolder.getContext();
	        securityContext.setAuthentication(authentication);

	        loginSuccessJwtHandler.setDefaultUrl("/main");
	        loginSuccessJwtHandler.setLoginId("userId");
	        loginSuccessJwtHandler.onAuthenticationSuccess(request, response, securityContext.getAuthentication());

		}catch(Exception e) {
			loginFailureHandler.setDefaultFailureUrl("/login/loginPage?error");
			loginFailureHandler.setLoginId("userId");
			loginFailureHandler.onAuthenticationFailure(request, response,  new UsernameNotFoundException(e.getMessage()));
		}

	}
}
