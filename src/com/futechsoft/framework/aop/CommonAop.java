package com.futechsoft.framework.aop;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.futechsoft.framework.logging.service.MenuLogService;



@Aspect
@Component
public class CommonAop {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonAop.class);

	@Resource(name = "framework.logging.service.MenuLogService")
	private MenuLogService menuLogServic; 

	@Around("execution(* *..controller.*.*(..)) && !within(com.futechsoft.framework..*)") // joinpoint 지정
	public Object commonAspect(ProceedingJoinPoint pjp) throws Throwable {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

		LOGGER.debug("request." + request.getRequestURI());

		menuLogServic.insertLog(request.getRequestURI());

		Object result = pjp.proceed();

		return result;
	}
}
