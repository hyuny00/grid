package com.futechsoft.gis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futechsoft.gis.service.GoogleMapsService;

@RestController
public class GoogleMapsController {

	/*
	@Autowired
    private  GoogleMapsService googleMapsService;

   
    @GetMapping("/maps/static")
    public ResponseEntity<byte[]> getStaticMap(@RequestParam String lat, @RequestParam String lng) {
        byte[] imageData = googleMapsService.getStaticMapImage(lat, lng);

        // 이미지의 Content-Type을 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "image/png");

        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }
    */
}