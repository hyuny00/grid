package com.futechsoft.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CommonUtil {

	public static boolean isTrue(String check) {
		return check.equalsIgnoreCase("Y");
	}

	public static String getToday() {
		return getToday("yyyyMMdd");
	}


	public static String getToday(String format) {
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(today);
	}


	public static HttpServletRequest getHttpServletRequest() throws Exception {
		return  ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
	}


	public static String getClientIP() throws Exception {

        String ip = null;
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();

        ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-RealIP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

		return ip;
	}

	public static String getClientIP(HttpServletRequest request) throws Exception {

        String ip = null;

        ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-RealIP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

		return ip;
	}


	public static void copy(InputStream in, OutputStream out) throws IOException {
		int BUFFER_SIZE = 4096;

		int byteCount = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			byteCount += bytesRead;

			if(byteCount > 1024*1024) {  //1M마다 메모리 비움
				byteCount=0;
				out.flush();
			}
		}
		out.flush();
	}

	/**
     * @param src
     * @param defaultValue
     * @return String
     */
    public static String nvl(String src, String defaultValue) {
        return src == null || "".equals(src.trim()) || "null".equals(src.trim()) ? defaultValue : src;
    }

	/**
     * @param src
     * @return String
     */
    public static String nvl(String src) {
        return nvl(src, "");
    }

    public static HashMap<String, List> getMultiInput(HttpServletRequest req) {
		HashMap<String, List> mInput = new HashMap<String, List>();
		Enumeration e = req.getParameterNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String[] values = req.getParameterValues(key);
            List list = new ArrayList();
            Collections.addAll(list, values);
            mInput.put(key, list);
        }
        return mInput;
	}

    /**
     * 날짜 더하거나 빼기
     * @param add
     * @return
     */
    public static String getAddToday(int add, String format) {

    	 Calendar cal = Calendar.getInstance();
    	 cal.add(Calendar.DATE, add);

    	 Date today =cal.getTime();
         SimpleDateFormat dateForm = new SimpleDateFormat(format);

         return dateForm.format(today);
    }


    public static String decodeBase64(String str) throws UnsupportedEncodingException {

    	Decoder decoder= Base64.getDecoder();
    	byte[] decodeByte=  decoder.decode(str);
    	String decodeString= new String(decodeByte,StandardCharsets.UTF_8.toString());

    	return decodeString;
    }

    public static String encodeBase64(String str) throws UnsupportedEncodingException {
    	return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8.toString()));
    }

    public static String getRandomInt(int length)  {
    	return RandomStringUtils.randomNumeric(length);
    }


}






