package com.futechsoft.framework.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.futechsoft.framework.exception.ErrorCode;
import com.futechsoft.framework.security.vo.CustomUserDetails;

public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	//@Autowired
	//private Sha256PasswordEncoder passwordEncoderSha256;

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = (String) authentication.getPrincipal();
		String password = (String) authentication.getCredentials();

		CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(username);


	
		if (!matchPassword(password, user.getPassword())) {
			throw new BadCredentialsException(ErrorCode.BAD_CREDENTIALS.getMessage());
		}


		/*
		if (!matchPassword(password, user.getUserId(), user.getPassword())) {
			throw  new BadCredentialsException(ErrorCode.BAD_CREDENTIALS.getMessage());
		}
*/

		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());

		return upat;
	}

	public boolean supports(Class<?> authentication) {
		return true;
	}
/*
	private boolean matchPassword(String loginPwd, String password) {
		return (passwordEncoder.matches(loginPwd, password));
	}
*/
	

	private boolean matchPassword(String loginPwd, String password) {
		return (passwordEncoder.matches(loginPwd, password));
	}
}
