package kr.go.odakorea.gis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.futechsoft.framework.component.RestTemplateFactory;

@Service
public class TileService {

	@Autowired
	RestTemplateFactory restTemplateFactory;

	@Value("${osm.proxy.baseUrl}")
	private String osmProxyBaseUrl;

	@Value("${maptiler.proxy.baseUrl}")
	private String procyMapTilerBaseUrl;

	@Value("${maptiler.apiKey}")
	private String apiKey;

	private HttpEntity<String> createHttpEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
		return new HttpEntity<>(headers);
	}

	public byte[] getTile(int zoom, int x, int y) {
		String tileUrl = String.format(osmProxyBaseUrl + "/%d/%d/%d.png", zoom, x, y);
		return fetchTile(tileUrl);
	}

	private byte[] fetchTile(String tileUrl) {
		HttpEntity<String> entity = createHttpEntity();

		RestTemplate restTemplate = restTemplateFactory.getRestTemplate();
		ResponseEntity<byte[]> response = restTemplate.exchange(tileUrl, HttpMethod.GET, entity, byte[].class);
		return response.getBody();
	}

	
	
	public byte[] getMaptilerTile(int zoom, int x, int y, String style) {
		String tileUrl = String.format("%s/%s/%d/%d/%d.jpg?key=%s", procyMapTilerBaseUrl+"/maps", style, zoom, x, y, apiKey);
		
		System.out.println("tileUrl."+tileUrl);
		return fetchMaptilerTile(tileUrl);
	}

	
	
	private byte[] fetchMaptilerTile(String tileUrl) {
        HttpEntity<String> entity = createHttpEntity();
        RestTemplate restTemplate = restTemplateFactory.getRestTemplate();
        
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                tileUrl, HttpMethod.GET, entity, byte[].class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("MapTiler API key is invalid or expired");
            } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new RuntimeException("MapTiler API rate limit exceeded");
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // 타일이 존재하지 않는 경우 (정상적인 상황)
                return new byte[0];
            }
            throw new RuntimeException("Failed to fetch tile from MapTiler: " + e.getMessage());
        } catch (ResourceAccessException e) {
            // 타임아웃이나 연결 문제
            throw new RuntimeException("Connection timeout or network error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error fetching tile: " + e.getMessage());
        }
    }

}
