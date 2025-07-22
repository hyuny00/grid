package com.futechsoft.gis.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.futechsoft.framework.component.RestTemplateFactory;

@Service
public class TileService {
/*
	@Autowired
	RestTemplateFactory restTemplateFactory;

	@Value("${alidade.key}")
	private String alidadeKey;
	
	@Value("${osm.proxy.baseUrl}")
	private String osmProxyBaseUrl;

	public byte[] getMap(String mapType, int zoom, int x, int y) {

		switch (mapType) {
			case "a":
				return getTile(zoom, x, y);
			case "b":
				return getTileCartodb(zoom, x, y);
			case "c":
				return getTileAlidade(zoom, x, y);
			default:
			return null;
		}
	}

	private HttpEntity<String> createHttpEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
		return new HttpEntity<>(headers);
	}

	private byte[] getTile(int zoom, int x, int y) {
		String tileUrl = String.format(osmProxyBaseUrl+"/%d/%d/%d.png", zoom, x, y);
		return fetchTile(tileUrl);
	}

	private byte[] getTileCartodb(int zoom, int x, int y) {
		String[] servers = { "a", "b", "c", "d" };
		String server = servers[new Random().nextInt(servers.length)];
		String tileUrl = String.format("https://%s.basemaps.cartocdn.com/light_all/%d/%d/%d.png", server, zoom, x, y);
		return fetchTile(tileUrl);
	}

	private byte[] getTileAlidade(int zoom, int x, int y) {
		String tileUrl = String.format(
				"https://tiles.stadiamaps.com/tiles/alidade_smooth/%d/%d/%d.png?api_key="+alidadeKey,
				zoom, x, y);
		return fetchTile(tileUrl);
	}/
	private byte[] fetchTile(String tileUrl) {
		HttpEntity<String> entity = createHttpEntity();

		RestTemplate restTemplate = restTemplateFactory.getRestTemplate();
		ResponseEntity<byte[]> response = restTemplate.exchange(tileUrl, HttpMethod.GET, entity, byte[].class);
		return response.getBody();
	}
	*/
	
}
