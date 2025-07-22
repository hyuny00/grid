package com.futechsoft.gis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.futechsoft.framework.component.RestTemplateFactory;

@Service
public class GoogleMapsService {
/*
    private String apiKey="AIzaSyAF5clCuMo4v1twVltwuIr_140vcB8qI-E";

	@Autowired
	RestTemplateFactory restTemplateFactory;

    public byte[] getStaticMapImage(String lat, String lng) {
    	
    	
    	RestTemplate  restTemplate = restTemplateFactory.getRestTemplate();
        // Static Maps API URL 구성
        String url = String.format("https://maps.googleapis.com/maps/api/staticmap?center=%s,%s&zoom=14&markers=37.7749,-122.4194&size=600x400&key=%s",   lat, lng, apiKey);

        
        System.out.println("url................"+url);
        // RestTemplate을 사용하여 Static Map API 요청
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        return response.getBody(); // 이미지 바이트 배열 반환
    }
    */
}