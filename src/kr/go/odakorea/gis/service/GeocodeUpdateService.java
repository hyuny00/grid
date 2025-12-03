package kr.go.odakorea.gis.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
     * CSV 파일을 읽어서 위도경도가 없는 경우 geocoding API를 호출하고 새로운 CSV 파일을 생성합니다.
     * 입력 CSV 포맷: 국가번호,지역번호,국가명,지역명,행정구역명,경도,위도
     * 출력 CSV 포맷: 국가번호,지역번호,국가명,지역명,행정구역명,경도,위도
     *
     * @param inputCsvFilePath 입력 CSV 파일 경로
     * @param outputCsvFilePath 출력 CSV 파일 경로
     * @return 처리 결과 메시지
     */
    public String processCSVAndUpdateCoordinates(String inputCsvFilePath, String outputCsvFilePath) {
        int totalCount = 0;
        int updatedCount = 0;
        int existingCount = 0;
        int failCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputCsvFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsvFilePath))) {

            String line;
            boolean isFirstLine = true;

            // 출력 CSV 헤더 작성
            writer.write("국가번호,지역번호,국가명,지역명,행정구역명,경도,위도");
            writer.newLine();

            while ((line = reader.readLine()) != null) {
                // 첫 번째 줄(헤더) 건너뛰기
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // 빈 라인이거나 공백만 있는 경우 처리 종료
                if (line.trim().isEmpty()) {
                    logger.info("빈 라인 발견, CSV 처리 종료");
                    break;
                }

                String[] fields = parseCsvLine(line);
                if (fields.length >= 7) {
                    String ntnNo = fields[0].trim();
                    String rgnNo = fields[1].trim();
                    String ntnNm = fields[2].trim();
                    String rgnNm = fields[3].trim();
                    String admNm = fields[4].trim();
                    String longitude = fields[5].trim();
                    String latitude = fields[6].trim();

                    // 필수 필드가 모두 비어있으면 처리 종료
                    if (ntnNo.isEmpty() && rgnNo.isEmpty() && ntnNm.isEmpty() && rgnNm.isEmpty()) {
                        logger.info("더 이상 유효한 데이터가 없음, CSV 처리 종료");
                        break;
                    }

                    totalCount++;
                    double lng = 0.0;
                    double lat = 0.0;

                    // 경도, 위도가 비어있는 경우 geocoding API 호출
                    if (longitude.isEmpty() || latitude.isEmpty()) {
                        try {
                            // Geocoding API 호출
                            String query = ntnNm + " " + rgnNm+ " " + admNm;
                            logger.info("좌표 업데이트 중 [{}]: {} (국가번호: {}, 지역번호: {})", totalCount, query, ntnNo, rgnNo);

                            Coordinates coordinates = geocodingService.geocode(query);

                            if (coordinates != null) {
                                lng = coordinates.getLongitude();
                                lat = coordinates.getLatitude();
                                updatedCount++;
                                logger.info("성공 [{}]: {} -> 경도: {}, 위도: {}", totalCount, query, lng, lat);
                            } else {
                                failCount++;
                                logger.warn("Geocoding 실패 [{}]: {}", totalCount, query);
                            }

                            // API 호출 간격 조절 (서버 부하 방지)
                            Thread.sleep(200);

                        } catch (Exception e) {
                            failCount++;
                            logger.error("처리 실패 [{}] - 국가번호: {}, 지역번호: {} - {}", totalCount, ntnNo, rgnNo, e.getMessage());
                        }
                    } else {
                        // 기존 좌표가 있는 경우
                        try {
                            lng = Double.parseDouble(longitude);
                            lat = Double.parseDouble(latitude);
                            existingCount++;
                            logger.info("기존 좌표 사용 [{}]: {} {} (경도: {}, 위도: {})", totalCount, ntnNm, rgnNm, lng, lat);
                        } catch (NumberFormatException e) {
                            logger.warn("좌표 형식 오류 [{}]: 경도={}, 위도={}", totalCount, longitude, latitude);
                            failCount++;
                        }
                    }

                    // CSV 라인 작성 (국가번호,지역번호,국가명,지역명,행정구역명,경도,위도)
                    if (lng != 0.0 && lat != 0.0) {
                        writer.write(String.format("%s,%s,%s,%s,%s,%s,%s",
                                ntnNo, rgnNo, ntnNm, rgnNm, admNm, lng, lat));
                        writer.newLine();
                    } else {
                        // 좌표를 가져오지 못한 경우에도 빈 값으로 기록
                        writer.write(String.format("%s,%s,%s,%s,%s,,",
                                ntnNo, rgnNo, ntnNm, rgnNm, admNm));
                        writer.newLine();
                    }
                }
            }

            String result = String.format("CSV 처리 완료 - 총: %d, 업데이트: %d, 기존 사용: %d, 실패: %d",
                    totalCount, updatedCount, existingCount, failCount);
            logger.info(result);
            logger.info("출력 파일: {}", outputCsvFilePath);

            return result;

        } catch (IOException e) {
            logger.error("CSV 파일 처리 실패: {}", e.getMessage());
            throw new RuntimeException("CSV 파일 처리 중 오류 발생", e);
        }
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
            "update tcm_ntn_rgn_cd set biz_rgn_pstn_lot_vl=%s, biz_rgn_pstn_lat_vl=%s where ntn_no='%s' and rgn_no='%s';",
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
