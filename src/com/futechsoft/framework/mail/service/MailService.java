package com.futechsoft.framework.mail.service;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service("framework.mail.service.MailService")
public class MailService {

    @Autowired
    private JavaMailSender mailSender;


    /**
     * text 메일 전송
     *
     * @param to      받는 사람
     * @param subject 제목
     * @param html    HTML 내용
     * @throws MessagingException
     */
    public void sendSimpleMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("your_email@gmail.com");

        mailSender.send(message);
    }

    


    /**
     * HTML 메일 전송
     *
     * @param to      받는 사람
     * @param subject 제목
     * @param html    HTML 내용
     * @throws MessagingException
     */
    public void sendHtmlMail(String to, String subject, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true = HTML
        helper.setFrom("your_email@gmail.com");

        mailSender.send(message);
    }



    /**
     * 첨부파일 포함 메일 전송
     *
     * @param to        받는 사람 이메일
     * @param subject   제목
     * @param html      HTML 내용
     * @param file      첨부파일
     * @throws MessagingException
     */
    public void sendMailWithAttachment(String to, String subject, String html, File file) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        // true = multipart 메일
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);  // HTML 형식
        helper.setFrom("your_email@gmail.com");

        if (file != null && file.exists()) {
            helper.addAttachment(file.getName(), file); // 첨부파일 추가
        }

        mailSender.send(message);
    }





}