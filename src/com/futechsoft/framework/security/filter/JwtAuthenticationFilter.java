package com.futechsoft.framework.security.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.futechsoft.admin.user.vo.UserRole;
import com.futechsoft.framework.security.auth.CustomUserDetailsService;
import com.futechsoft.framework.security.auth.JwtTokenProvider;
import com.futechsoft.framework.security.vo.CustomUserDetails;


public class JwtAuthenticationFilter extends OncePerRequestFilter {


	@Autowired
    private  JwtTokenProvider jwtTokenProvider;


	@Autowired
    private  CustomUserDetailsService userDetailsService;

	 @Value("${spring.profiles.active:default}")
     private String activeProfile;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


    	 // 1. JWT 가져오기
        String authHeader = request.getHeader("Authorization");
        String jwtToken = null;

        // Authorization 헤더 확인
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        }
        // Authorization 헤더가 없으면 쿠키에서 가져오기
        else if (request.getCookies() != null) {
            for (javax.servlet.http.Cookie cookie : request.getCookies()) {
                if ("JWT".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }



    	// **1. 기존 SecurityContext 확인 (이미 인증된 상태라면 JWT 검증 생략)JWT,session같이사용
        if (SecurityContextHolder.getContext().getAuthentication() != null) {


        	if(jwtToken != null) {
        		 if (jwtTokenProvider.willExpireSoon(jwtToken)) {

        			String username =  jwtTokenProvider.getUsername(jwtToken); // JWT에서 사용자 정보 추출
	            	List<GrantedAuthority>  authorities = jwtTokenProvider.getRoles(jwtToken);

	            	jwtToken = jwtTokenProvider.createToken(username, authorities);

	            	  // JWT 토큰을 쿠키에 설정
	    	        Cookie cookie = new Cookie("JWT", jwtToken);
	    	        cookie.setHttpOnly(true);  // JavaScript에서 접근할 수 없도록 설정
	    	        if(activeProfile.equals("prod")) {
	    	        	System.out.println("Current profile: " + activeProfile);
	    	        	cookie.setSecure(true);    // HTTPS 프로토콜에서만 쿠키 전송 (프로덕션 환경에서만 활성화)
	    	        }
	    	        cookie.setPath("/");       // 전체 도메인에서 쿠키 사용
	    	        cookie.setMaxAge(3600);    // 1시간 동안 유효

	    	        response.addCookie(cookie);

	    	        System.out.println("jwtToken......"+jwtToken);

        		 }
        	}



            filterChain.doFilter(request, response);
            return;
        }





        // 2. JWT가 없으면 다음 필터로 이동
        if (jwtToken == null) { // JWT가 없을 때만 바로 다음 필터로 진행
            filterChain.doFilter(request, response);
            return;
        }


        // 3. JWT 검증 및 사용자 정보 설정
        String username =  jwtTokenProvider.getUsername(jwtToken); // JWT에서 사용자 정보 추출

        if (username != null) {

        	if(username.equals("HOMPAGE_USER") && jwtTokenProvider.isValidTokenApiUser(jwtToken)) {

        		  // JWT 토큰을 쿠키에 설정
    	        Cookie cookie = new Cookie("JWT", jwtToken);
    	        cookie.setHttpOnly(true);  // JavaScript에서 접근할 수 없도록 설정
    	        if(activeProfile.equals("prod")) {
    	        	System.out.println("Current profile: " + activeProfile);
    	        	cookie.setSecure(true);    // HTTPS 프로토콜에서만 쿠키 전송 (프로덕션 환경에서만 활성화)
    	        }
    	        cookie.setPath("/");       // 전체 도메인에서 쿠키 사용
    	        cookie.setMaxAge(3600);    // 1시간 동안 유효

    	        response.addCookie(cookie);



        		   // JWT에서 권한 정보 추출
                List<GrantedAuthority> authorities = jwtTokenProvider.getRoles(jwtToken);

                // 외부 사용자용 UserDetails 생성 (DB 조회 없음)
                CustomUserDetails externalUserDetails = createExternalUserDetails(username, authorities);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(externalUserDetails, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // 외부 사용자는 세션을 생성하지 않음 (stateless)
                System.out.println("External user authenticated without session: " + username);


        	}else {
        		CustomUserDetails userDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(username);

                if (jwtTokenProvider.isValidToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);


                    // **1. 기존 SecurityContext 확인 (이미 인증된 상태라면 JWT 검증 생략)JWT,session같이사용
                    HttpSession session = request.getSession(true); // 세션이 없으면 생성
                    session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

                }
        	}

        }

        // 4. 다음 필터 호출
        filterChain.doFilter(request, response);
    }


    /**
     * 외부 사용자용 UserDetails 생성
     */
    private CustomUserDetails createExternalUserDetails(String username, List<GrantedAuthority> authorities) {
        CustomUserDetails externalUser = new CustomUserDetails();
        externalUser.setUserNm(username);
        externalUser.setEnabled(true);
        externalUser.setAccountNonExpired(true);
        externalUser.setAccountNonLocked(true);
        externalUser.setCredentialsNonExpired(true);


        // GrantedAuthority를 UserRole 리스트로 변환
        List<UserRole> userRoleList = convertAuthoritiesToUserRoles(username, authorities);
        externalUser.setUserRoleList(userRoleList);

        return externalUser;
    }

    /**
     * GrantedAuthority 리스트를 UserRole 리스트로 변환
     */
    private List<UserRole> convertAuthoritiesToUserRoles(String username, List<GrantedAuthority> authorities) {
        List<UserRole> userRoleList = new ArrayList<>();

        for (GrantedAuthority authority : authorities) {
            UserRole userRole = new UserRole();
            userRole.setUserNo(username);

            String roleCd = authority.getAuthority();
            // ROLE_ 접두사 제거 (이미 있다면)
            if (roleCd.startsWith("ROLE_")) {
                roleCd = roleCd.substring(5);
            }

            userRole.setRoleCd(roleCd);
            userRole.setRoleNm(roleCd); // 외부 사용자는 roleCd와 roleNm을 동일하게 설정
            userRole.setRoleSeq(0); // 외부 사용자는 기본값
            userRole.setUpRoleSeq(0); // 외부 사용자는 기본값
            userRole.setRoleTypeCd("EXTERNAL"); // 외부 사용자 구분자
            userRole.setSelectYn("Y");

            userRoleList.add(userRole);
        }

        return userRoleList;
    }
}
