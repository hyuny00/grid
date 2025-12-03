package com.futechsoft.framework.advice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @Value("${ap.inOutDiv}")
    private String inOutDiv;


    @ModelAttribute
    public void globalAttributes(Model model) {
        model.addAttribute("inOutDiv", inOutDiv);
    }
}