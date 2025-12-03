package com.futechsoft.framework.mail.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.futechsoft.framework.mail.service.MailService;

@Controller
public class MailController {

	@Autowired
	private MailService mailService;

	@RequestMapping("/common/sendMail")
	@ResponseBody
	public String sendMail() throws Exception {


		//일반메일
		mailService.sendSimpleMail("dhpark@futechsoft.com", "테스트 제목", "스프링 MVC 메일 테스트 내용입니다.");


		//html메일
		String htmlContent = "<h1 style='color:blue;'>HTML 메일 테스트</h1>" + "<p>Spring MVC에서 보내는 HTML 메일 예제입니다.</p>"
								+ "<ul>" + "<li>리스트 아이템 1</li>" +
								"<li>리스트 아이템 2</li>" + "</ul>"
								+ "<p><a href='https://www.google.com'>구글로 이동</a></p>";

		mailService.sendHtmlMail("dhpark@futechsoft.com", "테스트 제목", htmlContent);


		//html메일 첨부파일
		String htmlContent2 = "<h2>첨부파일 메일 테스트</h2>" + "<p>Spring MVC에서 HTML + 첨부파일 메일 발송 예제입니다.</p>";

		File attachment = new File("C:/js/readMe.txt"); // 첨부파일 경로 지정

		mailService.sendMailWithAttachment("dhpark@futechsoft.com", "첨부파일 메일 테스트", htmlContent2, attachment);

		return "메일 전송 성공!";
	}

}


