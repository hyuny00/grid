package com;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeoCodeSqlGenerator {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("사용법: java GeoCodeSqlGenerator <csv파일경로>");
            return;
        }

        String csvPath = args[0];
        File file = new File(csvPath);

        if (!file.exists()) {
            System.out.println("CSV 파일이 존재하지 않습니다: " + csvPath);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) { // 헤더 건너뛰기
                    firstLine = false;
                    continue;
                }

                String[] cols = line.split(",", -1);
                if (cols.length < 6) continue;

                String ntnNo = cols[0].trim();
                String rgnNo = cols[1].trim();
                String ntnEngNm = cols[2].trim();
                String rgnEngNm = cols[3].trim();

                String query = (ntnEngNm + " " + rgnEngNm).trim().replace(" ", "%20");
                String apiUrl = "http://localhost:8080/geocode?query=" + query;

                try {
                    // API 호출
                    HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    if (conn.getResponseCode() == 200) {
                        JsonNode jsonNode = mapper.readTree(conn.getInputStream());
                        double latitude = jsonNode.get("latitude").asDouble();
                        double longitude = jsonNode.get("longitude").asDouble();

                        // SQL 생성
                        String sql = String.format(
                            "update tcm_ntn_rgn_dc set biz_rgn_pstn_lot_vl=%s, biz_rgn_pstn_lat_vl=%s where ntn_no='%s' and rgn_no='%s';",
                            longitude, latitude, ntnNo, rgnNo
                        );
                        System.out.println(sql);
                    } else {
                        System.err.println("API 호출 실패: " + apiUrl);
                    }
                } catch (Exception e) {
                    System.err.println("에러: " + query + " => " + e.getMessage());
                }
            }
        }
    }
}
