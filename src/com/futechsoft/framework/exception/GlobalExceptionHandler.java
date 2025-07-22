package com.futechsoft.framework.exception;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.futechsoft.framework.common.constant.ViewInfo;
import com.futechsoft.framework.security.vo.CustomUserDetails;



@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);


	@ExceptionHandler(AccessDeniedException.class)
	protected String AccessDeniedException(HttpServletRequest request, Model model, Exception e) {


		String referer =request.getHeader("referer");
		e.printStackTrace();
		LOGGER.error(e.toString());

		request.setAttribute("message", e.getMessage());
		request.setAttribute(ViewInfo.REDIRECT_URL, referer);
		return ViewInfo.ACCESS_DENIED_REDIRECT_PAGE;

		//return ACCESS_ERR_PAGE;
	}

	@ExceptionHandler(Exception.class)
	protected String handleException(Model model, Exception e) {
		e.printStackTrace();
		LOGGER.error(e.toString());
		model.addAttribute("err", e.getMessage());
		return ViewInfo.DEFAULT_ERR_PAGE;
	}

	@ExceptionHandler(DuplicateKeyException.class)
	protected String dupException(HttpServletRequest request, Model model, SQLException e) {
		e.printStackTrace();
		LOGGER.error(e.toString());

		String referer =request.getHeader("referer");

		request.setAttribute("message","입력중 중복 오류가 발생했습니다.");

		request.setAttribute(ViewInfo.REDIRECT_URL, referer);
		return ViewInfo.ACCESS_DENIED_REDIRECT_PAGE;

	}

	@ExceptionHandler(FileDownloadException.class)
	protected String FileDownloadException(HttpServletRequest request, Model model, Exception e) {

		e.printStackTrace();
		LOGGER.error(e.toString());

		model.addAttribute("err", e.getMessage());

		return ViewInfo.DEFAULT_ERR_PAGE;
	}


	@ExceptionHandler({ JsonMappingException.class, JsonProcessingException.class })
	protected String JsonException(Model model, Exception e) {

		e.printStackTrace();
		LOGGER.error(e.toString());

		model.addAttribute("err", e.getMessage());

		return ViewInfo.DEFAULT_ERR_PAGE;
	}


	@ExceptionHandler(BoardNotFoundException.class)
	protected String BoardNotFoundException(HttpServletRequest request, Model model, Exception e) {

		e.printStackTrace();
		LOGGER.error(e.toString());
		model.addAttribute("err", e.getMessage());

		return ViewInfo.DEFAULT_ERR_PAGE;
	}


	@ExceptionHandler( AsyncRequestTimeoutException.class)
	protected ModelAndView AsyncRequestTimeoutException(HttpServletRequest request, HttpServletResponse response, Model model, Exception e)  throws Exception{

		if(!response.isCommitted()) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
		return new ModelAndView();

	}


	@ExceptionHandler(FileUploadException.class)
	protected String FileUploadException(Model model, Exception e) {
		e.printStackTrace();
		LOGGER.error(e.toString());
		model.addAttribute("err", e.getMessage());
		return ViewInfo.DEFAULT_ERR_PAGE;
	}


	@ExceptionHandler(AjaxException.class)
	@ResponseBody
	protected String AjaxException(Model model, Exception e) {
		e.printStackTrace();
		LOGGER.error(e.toString());
		model.addAttribute("err", e.getMessage());
		return "FAIL";
	}


	
	@ModelAttribute
    public void addUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
            model.addAttribute("user", userDetails);  // 모든 JSP에서 ${user.userNm} 사용 가능
        }
    }

	/*
	 * @ExceptionHandler(FileUploadException.class) protected String
	 * FileUploadException(Model model,Exception e) {
	 *
	 * e.printStackTrace();
	 *
	 * model.addAttribute("err", e.getMessage());
	 *
	 * return DEFAULT_ERR_PAGE; }
	 */

}
