package kr.go.odakorea.gis.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futechsoft.gis.vo.Coordinates;

@Service("gis.service.GeocodeUpdateService")
public class GeocodeUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(GeocodeUpdateService.class);

    @Autowired
    private GeocodingService geocodingService;

    /**
     * CSV 파일을 읽어서 geocoding API를 호출하고 UPDATE SQL을 생성합니다.
     *
     * @param csvFilePath CSV 파일 경로
     * @return 생성된 UPDATE SQL 문 리스트
     */
    public List<String> processCSVAndGenerateSQL(String csvFilePath) {
        List<String> sqlQueries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirstLine = true;
            int processedCount = 0;
            int successCount = 0;
            int failCount = 0;

            while ((line = reader.readLine()) != null) {
                // 첫 번째 줄(헤더) 건너뛰기
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = parseCsvLine(line);
                if (fields.length >= 4) {
                    String ntnNo = fields[0].trim();
                    String rgnNo = fields[1].trim();
                    String ntnEngNm = fields[2].trim();
                    String rgnEngNm = fields[3].trim();

                    // 빈 값 체크
                    if (!ntnNo.isEmpty() && !rgnNo.isEmpty() && !ntnEngNm.isEmpty() && !rgnEngNm.isEmpty()) {
                        processedCount++;

                        try {
                            // Geocoding API 호출
                            String query = ntnEngNm + " " + rgnEngNm;
                            logger.info("처리 중 [{}]: {} (ntn_no: {}, rgn_no: {})", processedCount, query, ntnNo, rgnNo);

                            Coordinates coordinates = geocodingService.geocode(query);

                            if (coordinates != null) {
                                // UPDATE SQL 생성
                                String sql = generateUpdateSQL(ntnNo, rgnNo, coordinates.getLongitude(), coordinates.getLatitude());
                                sqlQueries.add(sql);
                                successCount++;
                                logger.info("성공 [{}]: {} -> lat: {}, lng: {}", processedCount, query, coordinates.getLatitude(), coordinates.getLongitude());
                            } else {
                                failCount++;
                                logger.warn("Geocoding 실패 [{}]: {}", processedCount, query);
                            }

                            // API 호출 간격 조절 (서버 부하 방지)
                            Thread.sleep(200);

                        } catch (Exception e) {
                            failCount++;
                            logger.error("처리 실패 [{}] - ntn_no: {}, rgn_no: {} - {}", processedCount, ntnNo, rgnNo, e.getMessage());
                        }
                    } else {
                        logger.warn("빈 값 존재로 스킵 - ntn_no: '{}', rgn_no: '{}', ntn_eng_nm: '{}', rgn_eng_nm: '{}'",
                                   ntnNo, rgnNo, ntnEngNm, rgnEngNm);
                    }
                }
            }

            logger.info("CSV 처리 완료 - 총 처리: {}, 성공: {}, 실패: {}", processedCount, successCount, failCount);

        } catch (IOException e) {
            logger.error("CSV 파일 읽기 실패: {}", e.getMessage());
            throw new RuntimeException("CSV 파일 처리 중 오류 발생", e);
        }

        return sqlQueries;
    }

    /**
     * CSV 라인을 파싱합니다.
     *
     * @param line CSV 라인
     * @return 파싱된 필드 배열
     */
    private String[] parseCsvLine(String line) {
        // 간단한 CSV 파싱 (쉼표로 구분)
        return line.split(",", -1);
    }

    /**
     * UPDATE SQL을 생성합니다.
     *
     * @param ntnNo 국가번호
     * @param rgnNo 지역번호
     * @param longitude 경도
     * @param latitude 위도
     * @return UPDATE SQL 문
     */
    private String generateUpdateSQL(String ntnNo, String rgnNo, double longitude, double latitude) {
        return String.format(
            "update tcm_ntn_rgn_dc set biz_rgn_pstn_lot_vl=%s, biz_rgn_pstn_lat_vl=%s where ntn_no='%s' and rgn_no='%s';",
            longitude, latitude, ntnNo, rgnNo
        );
    }

    /**
     * CSV를 처리하고 결과를 콘솔에 출력합니다.
     *
     * @param csvFilePath CSV 파일 경로
     */
    public void processCSVAndPrintSQL(String csvFilePath) {
        List<String> sqlQueries = processCSVAndGenerateSQL(csvFilePath);

        logger.info("========== 생성된 UPDATE SQL ==========");
        for (String sql : sqlQueries) {
            System.out.println(sql);
        }
        logger.info("총 {}개의 SQL 쿼리가 생성되었습니다.", sqlQueries.size());
    }
}
