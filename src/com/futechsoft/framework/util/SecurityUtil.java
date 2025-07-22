package com.futechsoft.framework.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.futechsoft.framework.common.constant.RoleConstant;
import com.futechsoft.framework.security.vo.CustomUserDetails;

/**
 * <PRE>
 *
 * </PRE>
 *
 * @author futech
 * @version $Revision$
 */
public class SecurityUtil {

	/**
	 * 사용자 정보를 가져온다
	 * @return
	 */
	public static CustomUserDetails getPrincipal() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = null;
		if (authentication != null) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof CustomUserDetails) {
				userDetails = ((CustomUserDetails) principal);
			}
		}

		return userDetails;
	}

	/**
	 * 권한이 있는지 체크한다
	 * @param authCd
	 * @return
	 */
	public static boolean hasAuth(String authCd) {

		if(!authCd.startsWith(RoleConstant.ROLE_PREFIX)) {
			authCd=RoleConstant.ROLE_PREFIX+authCd;
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) return false;
		return authentication.getAuthorities().contains(new SimpleGrantedAuthority(authCd));
	}


	/**
	 * 권한이 있는지 체크한다
	 * @param authCd
	 * @return
	 */
	public static boolean hasAuth(String... authCds) {

		boolean hasAuth=false;
		for(String authCd : authCds) {
			if(hasAuth(authCd)) {
				hasAuth= true;
				break;
			}
		}
		return hasAuth;
	}


	/**
	 * 로그인한 사용자 번호를 가져온다
	 * @return
	 * @throws Exception
	 */
	public static String getUserNo() throws Exception {

		CustomUserDetails userDetails = getPrincipal();

		if (userDetails == null)
			return null;

		return userDetails.getUserNo();
	}

	/**
	 * 로그인한 사용자의 id를 가져온다
	 * @return
	 * @throws Exception
	 */
	public static String getUserId() throws Exception {

		CustomUserDetails userDetails = getPrincipal();

		if (userDetails == null) return null;

		return userDetails.getUserId();
	}

	/**
	 * 익명사용자인지 체크한다
	 * @return
	 * @throws Exception
	 */
	public static boolean isAnonymousUser() throws Exception {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (!(principal instanceof CustomUserDetails)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * 로그인한 사용자의 부서를 가져온다
	 * @return
	 * @throws Exception
	 */
	public static String getDptCd() throws Exception {

		CustomUserDetails userDetails = getPrincipal();

		if (userDetails == null) return null;

		return userDetails.getDptCd();
	}

	/**
	 * 로그인한 사용자의 부처를 가져온다
	 * @return
	 * @throws Exception
	 */
	public static String getMiniCd() throws Exception {

		CustomUserDetails userDetails = getPrincipal();

		if (userDetails == null) return null;

		return userDetails.getMiniCd();
	}

	

}
