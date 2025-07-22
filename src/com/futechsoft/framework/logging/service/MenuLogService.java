package com.futechsoft.framework.logging.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.futechsoft.framework.logging.mapper.MenuLogMapper;

@Service("framework.logging.service.MenuLogService")
public class MenuLogService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MenuLogService.class);

	@Resource(name = "framework.logging.mapper.MenuLogMapper")
	private MenuLogMapper menuLogMapper;

	public void insertLog(String url) {

	}

}
