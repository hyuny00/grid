package kr.go.odakorea.gis.service;

import java.util.List;

import kr.go.odakorea.gis.service.KoicaScrapingService.BusinessInfo;

//또는 별도의 테스트 클래스로 만들 경우
public class OdaScrapingTest {
 public static void main(String[] args) {
     System.out.println("=== ODA 스크래핑 테스트 시작 ===");
     
     KoicaScrapingService service = new KoicaScrapingService();
     
     try {
         // 테스트 실행
         //runBasicTest(service);
         
         // 특정 사업 번호로 테스트 (옵션)
         // runSpecificBusinessTest(service, "특정사업번호");
         
    	 service.scheduledScraping();
         
     } catch (Exception e) {
         System.err.println("테스트 실행 중 오류 발생: " + e.getMessage());
         e.printStackTrace();
     }
 }
 
 // 기본 테스트
 private static void runBasicTest(KoicaScrapingService service) throws Exception {
     System.out.println("\n--- 기본 테스트: 2개 사업 처리 ---");
     
     long startTime = System.currentTimeMillis();
     List<KoicaScrapingService.BusinessInfo> testResult = service.extractBusinessesForTest(2);
     long endTime = System.currentTimeMillis();
     
     System.out.println("실행 시간: " + (endTime - startTime) + "ms");
     System.out.println("총 처리된 사업 수: " + testResult.size());
     
     // 결과 상세 출력
     printDetailedResults(testResult);
     
     // 통계 출력
     service.printDownloadStatistics(testResult);
 }
 
 // 특정 사업 번호로 테스트
 private static void runSpecificBusinessTest(KoicaScrapingService service, String bsnsNo) {
     System.out.println("\n--- 특정 사업 테스트: " + bsnsNo + " ---");
     
     try {
         service.downloadFilesByBsnsNo(bsnsNo);
         System.out.println("특정 사업 파일 다운로드 완료");
     } catch (Exception e) {
         System.err.println("특정 사업 테스트 실패: " + e.getMessage());
     }
 }
 
 // 상세 결과 출력
 private static void printDetailedResults(List<KoicaScrapingService.BusinessInfo> results) {
     for (int i = 0; i < results.size(); i++) {
         KoicaScrapingService.BusinessInfo business = results.get(i);
         System.out.println("\n=== 사업 " + (i + 1) + " ===");
         System.out.println("사업번호: " + business.getBsnsNo());
         System.out.println("사업명: " + business.getKoreanBsnsNm());
         System.out.println("시작년도: " + business.getBsnsBeginYear());
         System.out.println("종료년도: " + business.getBsnsEndYear());
         System.out.println("지원분야: " + business.getSportRealmNm());
         System.out.println("파일 수: " + business.getFileList().size());
         
         // 파일 정보 출력
         if (!business.getFileList().isEmpty()) {
             System.out.println("파일 목록:");
             for (KoicaScrapingService.FileInfo file : business.getFileList()) {
                 String status = file.isDownloaded() ? "✅" : "❌";
                 System.out.println("  " + status + " " + file.getFileName());
                 if (file.isDownloaded()) {
                     System.out.println("    저장 위치: " + file.getLocalPath());
                 }
             }
         } else {
             System.out.println("파일이 없습니다.");
         }
         
     }
 }
}
