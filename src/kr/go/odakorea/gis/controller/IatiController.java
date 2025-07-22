package kr.go.odakorea.gis.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.futechsoft.framework.common.controller.AbstractController;

import kr.go.odakorea.gis.service.IatiService;

@Controller
public class IatiController extends AbstractController{

	private static final Logger LOGGER = LoggerFactory.getLogger(IatiController.class);


	@Resource(name = "gis.service.IatiService")
	IatiService iatiService;


}
