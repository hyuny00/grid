package com.futechsoft.framework.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ConvertUtil {

	public static void mapToBean(Object target, Map<String, Object> source) throws IllegalAccessException, InvocationTargetException {
		org.apache.commons.beanutils.BeanUtils.populate(target, source);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> beanToMap(Object src) throws Exception {
		return org.apache.commons.beanutils.BeanUtils.describe(src);
	}

	public static void copyProperties(Object source, Object target, String[] ignoreProperties) {
		org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
	}

	public static void copyProperties(Object source, Object target) {
		org.springframework.beans.BeanUtils.copyProperties(source, target);
	}

}
