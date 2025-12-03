package com.futechsoft.gis.controller;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.futechsoft.framework.component.RestTemplateFactory;

@Controller
public class ApiGatewayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGatewayController.class);

    @Autowired
    RestTemplateFactory restTemplateFactory;

    /* ============================
       1) MapTiler Proxy
    ============================ */
    @RequestMapping(value = "/api/maptiler/**", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<byte[]> proxyMaptiler(HttpServletRequest request) {
        String path = extractPath(request, "/api/maptiler/");
        String targetUrl = buildUrl("https://api.maptiler.com/", path, request.getQueryString());

        return forwardRequest(targetUrl, request);
    }

    /* ============================
       2) Overpass Proxy (POST용)
       항상 /api/interpreter 로만 요청
    ============================ */
    @RequestMapping(value = "/api/overpass/**", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<byte[]> proxyOverpass(HttpServletRequest request) {

        // Overpass는 path를 절대 붙이지 않음!
        String targetUrl = "https://overpass-api.de/api/interpreter";

        return forwardRequest(targetUrl, request);
    }

    /* ============================
       3) OSM Proxy
    ============================ */
    @GetMapping("/api/osm/{z}/{x}/{y}.png")
    public ResponseEntity<byte[]> proxyOsm(
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,
            HttpServletRequest request) {

        String targetUrl = String.format(
                "https://tile.openstreetmap.org/%d/%d/%d.png", z, x, y);

        return forwardRequest(targetUrl, request);
    }

    /* ============================
       4) ReliefWeb Proxy
       path + query 안전 조합
    ============================ */
    @RequestMapping(value = "/api/reliefweb/**", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<byte[]> proxyReliefweb(HttpServletRequest request) {
        String originalUri = request.getRequestURI();
        LOGGER.debug("ReliefWeb Original URI: {}", originalUri);

        String path = extractPath(request, "/api/reliefweb/");
        LOGGER.debug("ReliefWeb Extracted path: '{}'", path);

        // v1/ 경로가 없으면 추가
        if (!path.startsWith("v2/")) {
            if (path.isEmpty()) {
                path = "v2/disasters";
                LOGGER.debug("Empty path, using default: {}", path);
            } else {
                path = "v2/" + path;
                LOGGER.debug("Added v1/ prefix: {}", path);
            }
        }

        String targetUrl = "https://api.reliefweb.int/" + path;

        // Query String 처리
        String queryString = request.getQueryString();

        // appname이 없으면 자동 추가
        if (queryString == null || !queryString.contains("appname=")) {
            String appname = "appname=futechsoft-gis-gateway";
            queryString = (queryString == null) ? appname : queryString + "&" + appname;
            LOGGER.debug("Added appname to query: {}", queryString);
        }

        if (queryString != null) {
            targetUrl += "?" + queryString;
        }

        LOGGER.debug("ReliefWeb Final URL → {}", targetUrl);

        return forwardRequest(targetUrl, request);
    }

    /* ============================
       5) WorldBank Proxy
    ============================ */
    @RequestMapping(value = "/api/worldbank/**", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<byte[]> proxyWorldbank(HttpServletRequest request) {
        String path = extractPath(request, "/api/worldbank/");
        String targetUrl = buildUrl("https://api.worldbank.org/", path, request.getQueryString());

        return forwardRequest(targetUrl, request);
    }

    /* ======================================================
       공통 forwardRequest
       - GET/POST 모두 지원
       - Body 포함 (중요!!)
       - gzip, host 헤더 문제 해결
    ====================================================== */
    private ResponseEntity<byte[]> forwardRequest(String targetUrl, HttpServletRequest request) {

        LOGGER.debug("Forwarding request → {}", targetUrl);

        try {
            // 헤더 복사
            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> headerNames = request.getHeaderNames();

            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();

                if ("host".equalsIgnoreCase(headerName)) continue;
                if ("accept-encoding".equalsIgnoreCase(headerName)) continue;

                headers.add(headerName, request.getHeader(headerName));
            }

            // ⭐ User-Agent 헤더가 없으면 추가 (중요!)
            if (!headers.containsKey("User-Agent")) {
                headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            }

            // 요청 Body 읽기
            byte[] body = new byte[0];
            try {
                body = IOUtils.toByteArray(request.getInputStream());
            } catch (IOException ignored) {}

            HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = restTemplateFactory.getRestTemplate();

            HttpMethod method = HttpMethod.resolve(request.getMethod());

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    targetUrl,
                    method,
                    entity,
                    byte[].class
            );

            // 응답 헤더 복사
            HttpHeaders responseHeaders = new HttpHeaders();
            response.getHeaders().forEach((key, value) -> {
                if (!"transfer-encoding".equalsIgnoreCase(key)) {
                    responseHeaders.put(key, value);
                }
            });

            // CORS 헤더 추가
           // responseHeaders.add("Access-Control-Allow-Origin", "*");
           // responseHeaders.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
           // responseHeaders.add("Access-Control-Allow-Headers", "*");

            return ResponseEntity
                    .status(response.getStatusCode())
                    .headers(responseHeaders)
                    .body(response.getBody());

        } catch (Exception e) {
            LOGGER.error("Proxy Error: {}", e.getMessage(), e);

            // 에러 응답에도 CORS 헤더 추가
            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.add("Access-Control-Allow-Origin", "*");
            errorHeaders.add("Content-Type", "text/plain; charset=UTF-8");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(errorHeaders)
                    .body(("Proxy error: " + e.getMessage()).getBytes());
        }
    }

    /* ======================================================
       Path 추출 (안정적)
    ====================================================== */
    private String extractPath(HttpServletRequest request, String prefix) {
        String uri = request.getRequestURI();
        if (!uri.startsWith(prefix)) {
            return "";
        }
        return uri.substring(prefix.length());
    }

    /* ======================================================
       URL 빌더 (path + query 조합)
    ====================================================== */
    private String buildUrl(String base, String path, String query) {
        String url = base + path;
        if (query != null) {
            url += "?" + query;
        }
        return url;
    }
}
