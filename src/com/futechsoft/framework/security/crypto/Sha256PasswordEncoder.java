package com.futechsoft.framework.security.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;


public class Sha256PasswordEncoder {

	private static final Logger LOGGER = LoggerFactory.getLogger(Sha256PasswordEncoder.class);

	 public  String encode(String password, String userId) throws BadCredentialsException {

			if (password == null || userId == null) {
				throw new BadCredentialsException("입력값 오류입니다");
			}

			byte[] hashValue = null;

			MessageDigest md= null;
			try {
				md = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
//				e.printStackTrace();
				LOGGER.error(e.toString());
				throw new BadCredentialsException("SHA-256 알고리즘 오류");
			}

			md.reset();
			md.update(userId.getBytes());

			hashValue = md.digest(password.getBytes());

			return new String(Base64.encodeBase64(hashValue));
    }




	 public boolean matches(String loginPwd, String userId, String password) throws BadCredentialsException {
		 return encode(loginPwd, userId).equals(password);
	 }


}
