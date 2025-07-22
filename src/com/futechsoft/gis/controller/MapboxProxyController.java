package com.futechsoft.gis.controller;



import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;



@Controller
@RequestMapping("/mapbox")
public class MapboxProxyController {
/*
	
	@Value("${mapbox.key}")
	private String mapboxToken;
	

    private final RestTemplate restTemplate;

    public MapboxProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/tiles/{z}/{x}/{y}")
    public ResponseEntity<byte[]> getTile(
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,
            @RequestParam(defaultValue = "mapbox/streets-v11") String style) {

        String tileUrl = String.format(
                "https://api.mapbox.com/styles/v1/%s/tiles/256/%d/%d/%d?access_token=%s",
                style, z, x, y, mapboxToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        ResponseEntity<byte[]> response = restTemplate.exchange(
                tileUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                byte[].class);

        HttpHeaders newHeaders = new HttpHeaders();
        
        newHeaders.setContentType(MediaType.IMAGE_PNG); // 또는 IMAGE_JPEG
        
        System.out.println("Mapbox 응답 코드: " + response.getStatusCode());
        
        return response;
    }
*/
}

