package com.futechsoft.framework.security.controller;

import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccessErrorController {


	@RequestMapping(value = "/accessdenied")
	public String accessdenied(@RequestParam HashMap<String, Object> map) throws Exception {
		return "framework/error/accessError";
	}

	@RequestMapping(value = "/invalidCsrf")
	public String csrfAccessdenied(@RequestParam HashMap<String, Object> map) throws Exception {
		return "framework/error/csrfError";
	}
	
	

}
