package kr.go.odakorea.gis.service;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.futechsoft.framework.file.vo.FileInfoVo;
import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.FileUtil;
import com.futechsoft.framework.util.FtMap;

import kr.go.odakorea.gis.mapper.KoicaScrapingMapper;

@Service("gis.service.KoicaScrapingService")
public class KoicaScrapingService {
    
    private static final Logger logger = LoggerFactory.getLogger(KoicaScrapingService.class);
    private static final String BASE_URL = "https://oda.go.kr/opo/bsin/bsnsDtlsList.do";
    private static final String DETAIL_URL = "https://www.oda.go.kr/opo/bsin/bsnsSumryDocDetail.do";
    private static final int PAGE_SIZE = 100;
    
    @Resource(name = "gis.mapper.KoicaInfoMapper")
    private KoicaScrapingMapper koicaScrapingMapper;
    
    
	@Value("${oda.download.dir}")
	private String downloadDir;
    
    // 다운로드 디렉토리 초기화
    static {
        try {
            disableSslVerification();
        } catch (Exception e) {
            System.err.println("초기화 중 오류 발생: " + e.getMessage());
        }
    }
    
    @PostConstruct
	public void init() throws Exception {
    	   new File(downloadDir).mkdirs(); // 폴더 없으면 생성
	}
    
    // 사업 정보 클래스
    public static class BusinessInfo {
        private String bsnsNo;
        private String koreanBsnsNm;
        private String bsnsBeginYear;
        private String bsnsEndYear;
        private String sportRealmNm;
        
        private String bizRgnCn;
        
        private List<FileInfo> fileList = new ArrayList<>();
        
        public BusinessInfo(String bsnsNo, String koreanBsnsNm, String bsnsBeginYear, 
                           String bsnsEndYear, String sportRealmNm,String bizRgnCn) {
            this.bsnsNo = bsnsNo;
            this.koreanBsnsNm = koreanBsnsNm;
            this.bsnsBeginYear = bsnsBeginYear;
            this.bsnsEndYear = bsnsEndYear;
            this.sportRealmNm = sportRealmNm;
            this.bizRgnCn = bizRgnCn;
        }
        
        // Getters and Setters
        public String getBsnsNo() { return bsnsNo; }
        public String getKoreanBsnsNm() { return koreanBsnsNm; }
        public String getBsnsBeginYear() { return bsnsBeginYear; }
        public String getBsnsEndYear() { return bsnsEndYear; }
        public String getSportRealmNm() { return sportRealmNm; }
        public String getBizRgnCn() { return bizRgnCn; }
        public List<FileInfo> getFileList() { return fileList; }
        public void setFileList(List<FileInfo> fileList) { this.fileList = fileList; }
        
        
        public void setBizRgnCn(String bizRgnCn) { this.bizRgnCn = bizRgnCn; }
        
        @Override
        public String toString() {
            return String.format("사업번호: %s, 사업명: %s, 시작년도: %s, 종료년도: %s, 지원분야: %s, 사업대상지: %s, 파일수: %d",
                    bsnsNo, koreanBsnsNm, bsnsBeginYear, bsnsEndYear, sportRealmNm,bizRgnCn, fileList.size());
        }
    }
    
    // 파일 정보 클래스
    public static class FileInfo {
    	
    	private String fileId;
        private String fileName;
        private String downloadUrl;
        private String localPath; // 다운로드된 파일의 로컬 경로
        private boolean downloaded = false; // 다운로드 성공 여부
        
        private long fileSize;
        
        public FileInfo(String fileId,String fileName, String downloadUrl) {
        	this.fileId = fileId;
            this.fileName = fileName;
            this.downloadUrl = downloadUrl;
        }
        
        public String getFileId() { return fileId; }
        
        public String getFileName() { return fileName; }
        public String getDownloadUrl() { return downloadUrl; }
        public String getLocalPath() { return localPath; }
        public long getFileSize() { return fileSize; }
        
        public void setFileSize(long fileSize) {
        	this.fileSize=fileSize;
        }
        
        public boolean isDownloaded() { return downloaded; }
        public void setLocalPath(String localPath) { this.localPath = localPath; }
        public void setDownloaded(boolean downloaded) { this.downloaded = downloaded; }
        
        @Override
        public String toString() {
            return "FileInfo{" +
                    "fileName='" + fileName + '\'' +
                    ", downloadUrl='" + downloadUrl + '\'' +
                    ", localPath='" + localPath + '\'' +
                    ", downloaded=" + downloaded +
                    '}';
        }
    }
    
    // SSL 인증서 우회 설정
    private static void disableSslVerification() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }
        };
        
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
    
    // 매일 오전 2시에 실행되는 스케줄러
    //@Scheduled(cron = "0 0 2 * * *")
  //  @Scheduled(fixedRate = 30000) 
    public void scheduledScraping() {
        logger.info("ODA 스크래핑 스케줄 시작");
        
     
        
        try {
            List<BusinessInfo> businesses = extractAllBusinessesWithFiles();
            logger.info("총 {}개 사업 정보 추출 완료", businesses.size());
            
            // 여기서 데이터베이스 저장 또는 다른 처리 로직 수행
            //processBusinessData(businesses);
            
        } catch (Exception e) {
            logger.error("스케줄 실행 중 오류 발생", e);
        }
    }
    
    // 수동 실행용 메서드
    public List<BusinessInfo> extractAllBusinessesWithFiles() throws Exception {
    	
    	
    	String tmpBsnsNo= koicaScrapingMapper.getEnfcInstUnqBizNo();
    	
        logger.info("ODA 사업 목록 추출 시작");
        
        List<BusinessInfo> allBusinesses = new ArrayList<>();
        
        // 첫 번째 페이지를 가져와서 전체 개수 확인
        JsonNode firstPage = fetchPage(1);
        JsonNode firstItem = firstPage.get("bsnsDtlsList").get(0);
        int totalCount = firstItem.get("TOT_CNT").asInt();
        
        logger.info("전체 사업 개수: {}", totalCount);
        
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
        logger.info("총 페이지 수: {}", totalPages);
        
        boolean stopProcessing = false;
        
        // 모든 페이지 처리
        for (int pageNo = 1; pageNo <= totalPages; pageNo++) {
        	
        	if (stopProcessing) break;
            logger.info("페이지 {}/{} 처리 중...", pageNo, totalPages);
            
            try {
                JsonNode pageData = fetchPage(pageNo);
                JsonNode businessList = pageData.get("bsnsDtlsList");
                
                if (businessList != null && businessList.isArray()) {
                    for (JsonNode business : businessList) {
                    	
                    	
                    	String currentBsnsNo = business.get("BSNS_NO").asText();

                    	 if (tmpBsnsNo != null && currentBsnsNo.compareTo(tmpBsnsNo) <= 0) {
                             stopProcessing = true;
                             break; // 현재 페이지에서 멈추고 다음 페이지도 안 감
                         }
                    	
                    	
                    	String file1At = business.get("FILE1_AT").asText();
                    	String file2At = business.get("FILE2_AT").asText();
                    	
                   
                        if(CommonUtil.nvl(file1At).equals("") && CommonUtil.nvl(file2At).equals("")) {
                        	continue;
                        }
                        
                        BusinessInfo info = new BusinessInfo(
                            business.get("BSNS_NO").asText(),
                            business.get("KOREAN_BSNS_NM").asText(),
                            business.get("BSNS_BEGIN_YEAR").asText(),
                            business.get("BSNS_END_YEAR").asText(),
                            business.get("SPORT_REALM_NM").asText(),
                            ""
                        );
                        
                        // 각 사업의 파일 정보 추출 및 다운로드
                        try {
                            List<FileInfo> files = extractFilesFromBusiness(info.getBsnsNo(),info);
                            info.setFileList(files);
                            
                            // 파일 다운로드 실행
                            downloadBusinessFiles(info);
                            
                            processBusinessData(info);
                            
                            logger.debug("사업 {} 파일 {}개 추출 및 다운로드 완료", info.getBsnsNo(), files.size());
                        } catch (Exception e) {
                            logger.warn("사업 {} 파일 처리 실패: {}", info.getBsnsNo(), e.getMessage());
                        }
                        
                       // allBusinesses.add(info);
                    }
                }
                
                // 서버 부하 방지를 위한 대기 (파일 다운로드 시간 고려하여 증가)
                Thread.sleep(2000);
                
            } catch (Exception e) {
                logger.error("페이지 {} 처리 중 오류 발생", pageNo, e);
            }
        }
        
        logger.info("총 {}개 사업 정보 추출 완료", allBusinesses.size());
        return allBusinesses;
    }
    
    // 특정 사업의 파일 정보 추출
    public List<FileInfo> extractFilesFromBusiness(String bsnsNo, BusinessInfo info) throws IOException {
        String url = DETAIL_URL + "?P_BSNS_NO=" + bsnsNo;
        List<FileInfo> fileList = new ArrayList<>();
        
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();
            
            

            String bizRgnCn = "";
         
            Elements supportFieldRows = doc.select("tr:has(th:contains(사업대상지))");

            if (!supportFieldRows.isEmpty()) {
                Element row = supportFieldRows.first();
                bizRgnCn = row.select("td").text(); // td 안의 텍스트를 추출
                
                if(bizRgnCn!=null) {
                	bizRgnCn= bizRgnCn.replace("▣", "").trim();
                }
            }
            
            info.setBizRgnCn(bizRgnCn);
            
            Elements rows = doc.select("#uploaded-files-tb tr");
            for (Element row : rows) {
                String fileName = row.select(".tb_file_name").text();
                Element linkElement = row.select(".tb_down_btn[href]").first();
                
                if (fileName != null && !fileName.isEmpty() && linkElement != null) {
                    String relativeUrl = linkElement.attr("href");
                    String absoluteUrl = "https://www.oda.go.kr" + relativeUrl;
                    fileList.add(new FileInfo(FileUtil.getRandomId(), fileName, absoluteUrl));
                }
            }
            
        } catch (Exception e) {
            logger.warn("사업 {} 파일 추출 중 오류: {}", bsnsNo, e.getMessage());
        }
        
        return fileList;
    }
    
    // 페이지 데이터 가져오기
    private JsonNode fetchPage(int pageNo) throws IOException {
        String urlString = BASE_URL + "?P_PAGE_NO=" + pageNo + "&P_PAGE_SIZE=" + PAGE_SIZE;
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        
        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8");
            connection.setRequestProperty("Connection", "keep-alive");
            
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("HTTP 요청 실패: " + responseCode + " - " + connection.getResponseMessage());
            }
            
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.toString());
            
        } finally {
            connection.disconnect();
        }
    }
    
    // 비동기 처리용 메서드
    /*
    @Async
    public CompletableFuture<List<BusinessInfo>> extractAllBusinessesAsync() {
        try {
            List<BusinessInfo> result = extractAllBusinessesWithFiles();
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            logger.error("비동기 추출 중 오류 발생", e);
            
            CompletableFuture<List<BusinessInfo>> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
            
        }
    }
    */
    
    
    public boolean downloadFile(String fileURL, String saveFileName,FileInfo file) {
        long totalBytes = 0;

        try (BufferedInputStream in = new BufferedInputStream(new URL(fileURL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(saveFileName)) {
            
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            logger.info("다운로드 시작: {}", saveFileName);
            
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                totalBytes += bytesRead; // 읽은 바이트 누적
            }
            
            file.setFileSize(totalBytes);
            
            logger.info("✅ 다운로드 완료: {} (크기: {} bytes)", saveFileName, totalBytes);
            return true;
            
        } catch (IOException e) {
            logger.error("❌ 다운로드 실패: {} - {}", saveFileName, e.getMessage());
            return false;
        }
    }

    
    // 특정 사업의 모든 파일 다운로드
    public void downloadBusinessFiles(BusinessInfo business) {
        String businessDir = downloadDir + "/" + business.getBsnsBeginYear() + "/";
        new File(businessDir).mkdirs(); // 사업별 폴더 생성
        
        for (FileInfo file : business.getFileList()) {
            try {
                // 파일명에서 특수문자 제거
                String safeFileName = file.getFileName().replaceAll("[\\\\/:*?\"<>|]", "_");
                String localPath = businessDir + file.getFileId()+".FILE";
                
                // 파일 다운로드
                boolean success = downloadFile(file.getDownloadUrl(), localPath,file);
                
                // 다운로드 결과 업데이트
                file.setLocalPath(localPath);
                file.setDownloaded(success);
                
                if (success) {
                    logger.info("파일 다운로드 성공: {} -> {}", file.getFileName(), localPath);
                } else {
                    logger.warn("파일 다운로드 실패: {}", file.getFileName());
                }
                
                // 파일 간 다운로드 간격
                Thread.sleep(500);
                
            } catch (Exception e) {
                logger.error("파일 다운로드 중 오류: {} - {}", file.getFileName(), e.getMessage());
            }
        }
    }
    
    // 특정 사업의 파일만 다운로드하는 메서드
    public void downloadFilesByBsnsNo(String bsnsNo) throws IOException {
        List<FileInfo> files = extractFilesFromBusiness(bsnsNo,null);
        if (files.isEmpty()) {
            logger.info("사업 {}에 다운로드할 파일이 없습니다.", bsnsNo);
            return;
        }
        
        BusinessInfo tempBusiness = new BusinessInfo(bsnsNo, "", "", "", "","");
        tempBusiness.setFileList(files);
        downloadBusinessFiles(tempBusiness);
    }
    
    // 데이터 처리 로직 (데이터베이스 저장 등)
    /*
    private void processBusinessData(List<BusinessInfo> businesses) {
        logger.info("데이터 처리 시작 - 총 {}개 사업", businesses.size());
        
        int totalFiles = 0;
        int downloadedFiles = 0;
        
        for (BusinessInfo business : businesses) {
            try {
                // 데이터베이스 저장 로직
                //saveBusinessToDatabase(business);
                
                // 다운로드 통계 계산
                for (FileInfo file : business.getFileList()) {
                    totalFiles++;
                    if (file.isDownloaded()) {
                        downloadedFiles++;
                    }
                }
                
            } catch (Exception e) {
                logger.error("사업 {} 처리 중 오류", business.getBsnsNo(), e);
            }
        }
        
        logger.info("데이터 처리 완료 - 총 {}개 파일 중 {}개 다운로드 성공", totalFiles, downloadedFiles);
    }
    */

	private void processBusinessData(BusinessInfo businesses) {
		
		 String year = businesses.getBsnsBeginYear();
         String businessDir = downloadDir + "/" + year + "/";
         
		
		 try {
 	        // 파일별로 분리하여 데이터베이스 저장
 	        if (businesses.getFileList().isEmpty()) {
 	            // 파일이 없는 사업은 파일 정보 없이 저장
 	           // saveBusinessWithoutFile(business);
 	        } else {
 	            // 파일별로 분리하여 저장
 	            for (FileInfo file : businesses.getFileList()) {
 	            	
 	            	FtMap param = new FtMap();
 	            	
 	            	
 	            	
 	            	param.put("fileId", file.getFileId());
 	            	param.put("enfcInstUnqBizNo", businesses.getBsnsNo());
 	            	param.put("bizKornNm",  businesses.getKoreanBsnsNm());
 	            	param.put("bizPrdBgngYr", businesses.getBsnsBeginYear());
 	            	param.put("bizPrdEndYr", businesses.getBsnsEndYear());
 	            	param.put("bizFldNm",  businesses.getSportRealmNm());
 	            	param.put("bizRgnCn", businesses.getBizRgnCn());
 	            	param.put("lgcFileNm", file.getFileName());
 	            	param.put("physFileNm", file.getFileId()+".FILE");
 	            	param.put("filePathCn", businessDir);
 	            	param.put("fileExtnCn", FileUtil.getFileExt(file.getFileName()));
 	            	param.put("fileSz", file.getFileSize());
 	            	param.put("filePstnSecd", "02");
 	            	param.put("regUserId", "system");
 	            	param.put("mdfcnUserId", "system");
 	            	
 	            	
 	            	 koicaScrapingMapper.insertKoicaScrpInfo(param);

 	            	
 	            	 logger.info("사업번호: {}", businesses.getBsnsNo());
 	                 logger.info("사업명: {}", businesses.getKoreanBsnsNm());
 	                 logger.info("시작년도: {}", businesses.getBsnsBeginYear());
 	                 logger.info("종료년도: {}", businesses.getBsnsEndYear());
 	                 logger.info("지원분야: {}", businesses.getSportRealmNm());
 	                 
 	                logger.info("사업대상지: {}", businesses.getBizRgnCn());
 	            	
 	            	 logger.info("저장 파일명: {}", file.getFileName());
 	            	 logger.info("저장 디렉토리: {}", businessDir);
 	            	 logger.info("fileSz: {}", file.getFileSize());
 	            	 
 	            }
 	        }
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
  
	}
	
	
 // 더 상세한 정보를 포함한 버전
 private void processBusinessData(List<BusinessInfo> businesses) {
     logger.info("데이터 처리 시작 - 총 {}개 사업", businesses.size());
     
     int totalFiles = 0;
     int downloadedFiles = 0;
    // Map<String, Integer> yearlyStats = new HashMap<>();
     
     for (BusinessInfo business : businesses) {
    	    try {
    	        // 파일별로 분리하여 데이터베이스 저장
    	        if (business.getFileList().isEmpty()) {
    	            // 파일이 없는 사업은 파일 정보 없이 저장
    	           // saveBusinessWithoutFile(business);
    	        } else {
    	            // 파일별로 분리하여 저장
    	            for (FileInfo file : business.getFileList()) {
    	               // saveBusinessFileToDatabase(business, file);
    	            	
    	            	 logger.info("사업번호: {}", business.getBsnsNo());
    	                 logger.info("사업명: {}", business.getKoreanBsnsNm());
    	                 logger.info("시작년도: {}", business.getBsnsBeginYear());
    	                 logger.info("종료년도: {}", business.getBsnsEndYear());
    	                 logger.info("지원분야: {}", business.getSportRealmNm());
    	            	
    	            	 logger.info("저장 파일명: {}", file.getFileName());
    	            	 logger.info("저장 디렉토리: {}", file.getLocalPath());
    	            }
    	        }
     
             /*
             String year = business.getBsnsBeginYear();
             String businessDir = DOWNLOAD_DIR + year + "/";
             
             logger.info("=== 사업 정보 ===");
             logger.info("사업번호: {}", business.getBsnsNo());
             logger.info("사업명: {}", business.getKoreanBsnsNm());
             logger.info("시작년도: {}", business.getBsnsBeginYear());
             logger.info("종료년도: {}", business.getBsnsEndYear());
             logger.info("지원분야: {}", business.getSportRealmNm());
             logger.info("저장 디렉토리: {}", businessDir);
             logger.info("파일 수: {}", business.getFileList().size());
             */
    	        
             /*
             // 년도별 통계
             yearlyStats.put(year, yearlyStats.getOrDefault(year, 0) + business.getFileList().size());
             
             // 파일 정보 상세 출력
             if (!business.getFileList().isEmpty()) {
                 logger.info("--- 파일 목록 ---");
                 for (int i = 0; i < business.getFileList().size(); i++) {
                     FileInfo file = business.getFileList().get(i);
                     totalFiles++;
                     
                     if (file.isDownloaded()) {
                         downloadedFiles++;
                         logger.info("  {}. ✅ {}", i + 1, file.getFileName());
                         logger.info("     저장 위치: {}", file.getLocalPath());
                         logger.info("     다운로드 URL: {}", file.getDownloadUrl());
                     } else {
                         logger.warn("  {}. ❌ {} (다운로드 실패)", i + 1, file.getFileName());
                         logger.warn("     다운로드 URL: {}", file.getDownloadUrl());
                     }
                 }
             } else {
                 logger.info("--- 파일 없음 ---");
             }
             
             logger.info(""); // 빈 줄로 구분
             */
         } catch (Exception e) {
             logger.error("사업 {} 처리 중 오류", business.getBsnsNo(), e);
         }
     }
     
     
     // 최종 통계 출력
     logger.info("=== 최종 통계 ===");
     logger.info("총 사업 수: {}", businesses.size());
     logger.info("총 파일 수: {}", totalFiles);
     logger.info("다운로드 성공: {}", downloadedFiles);
     logger.info("다운로드 실패: {}", totalFiles - downloadedFiles);
     logger.info("성공률: {:.2f}%", totalFiles > 0 ? (double)downloadedFiles / totalFiles * 100 : 0);
     
     /*
     // 년도별 통계
     logger.info("=== 년도별 파일 통계 ===");
     for (Map.Entry<String, Integer> entry : yearlyStats.entrySet()) {
         logger.info("{}년: {}개 파일", entry.getKey(), entry.getValue());
     }
     */
 }
    
    // 파일 다운로드 통계 조회
    public void printDownloadStatistics(List<BusinessInfo> businesses) {
        int totalBusinesses = businesses.size();
        int totalFiles = 0;
        int downloadedFiles = 0;
        
        for (BusinessInfo business : businesses) {
            for (FileInfo file : business.getFileList()) {
                totalFiles++;
                if (file.isDownloaded()) {
                    downloadedFiles++;
                }
            }
        }
        
        logger.info("=== 다운로드 통계 ===");
        logger.info("총 사업 수: {}", totalBusinesses);
        logger.info("총 파일 수: {}", totalFiles);
        logger.info("다운로드 성공: {}", downloadedFiles);
        logger.info("다운로드 실패: {}", totalFiles - downloadedFiles);
        logger.info("성공률: {:.2f}%", totalFiles > 0 ? (double)downloadedFiles / totalFiles * 100 : 0);
    }
    
    
    
    public List<BusinessInfo> extractBusinessesForTest(int maxCount) throws IOException {
        logger.info("테스트용 ODA 사업 목록 추출 시작 - 최대 {}개", maxCount);
        
        List<BusinessInfo> allBusinesses = new ArrayList<>();
        
        // 첫 번째 페이지만 가져오기
        JsonNode firstPage = fetchPage(1);
        JsonNode businessList = firstPage.get("bsnsDtlsList");
        
        if (businessList != null && businessList.isArray()) {
            int processedCount = 0;
            
            for (JsonNode business : businessList) {
                if (processedCount >= maxCount) {
                    break; // 지정된 개수에 도달하면 중단
                }
                
                BusinessInfo info = new BusinessInfo(
                    business.get("BSNS_NO").asText(),
                    business.get("KOREAN_BSNS_NM").asText(),
                    business.get("BSNS_BEGIN_YEAR").asText(),
                    business.get("BSNS_END_YEAR").asText(),
                    business.get("SPORT_REALM_NM").asText(),
                    ""
                );
                
                logger.info("처리 중 ({}/{}): {}", processedCount + 1, maxCount, info.getKoreanBsnsNm());
                
                // 각 사업의 파일 정보 추출 및 다운로드
                try {
                    List<FileInfo> files = extractFilesFromBusiness(info.getBsnsNo(),null);
                    info.setFileList(files);
                    
                    // 파일 다운로드 실행
                    downloadBusinessFiles(info);
                    
                    logger.info("사업 {} 파일 {}개 추출 및 다운로드 완료", info.getBsnsNo(), files.size());
                } catch (Exception e) {
                    logger.warn("사업 {} 파일 처리 실패: {}", info.getBsnsNo(), e.getMessage());
                }
                
                allBusinesses.add(info);
                processedCount++;
                
                // 서버 부하 방지를 위한 대기
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        logger.info("테스트 완료 - 총 {}개 사업 정보 추출", allBusinesses.size());
        return allBusinesses;
    }
    
    
    public FileInfoVo getFileInfo(FtMap params) throws Exception {

		return koicaScrapingMapper.selectFileInfo(params);
	}
    
    
}