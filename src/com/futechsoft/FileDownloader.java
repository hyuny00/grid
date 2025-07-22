package com.futechsoft;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FileDownloader {

    public static class FileInfo {
        public String fileName;
        public String downloadUrl;

        public FileInfo(String fileName, String downloadUrl) {
            this.fileName = fileName;
            this.downloadUrl = downloadUrl;
        }

        @Override
        public String toString() {
            return "FileInfo{" +
                    "fileName='" + fileName + '\'' +
                    ", downloadUrl='" + downloadUrl + '\'' +
                    '}';
        }
    }

    // ✅ SSL 인증 우회 (테스트용)
    public static void disableSslVerification() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ 파일 다운로드 기능
    public static void downloadFile(String fileURL, String saveFileName) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileURL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(saveFileName)) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;

            System.out.println("Downloading: " + saveFileName);
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            System.out.println("✅ Download complete: " + saveFileName);
        } catch (IOException e) {
            System.err.println("❌ Download failed: " + saveFileName);
            e.printStackTrace();
        }
    }
//https://oda.go.kr/opo/bsin/bsnsDtlsList.do?P_PAGE_NO=1&P_PAGE_SIZE=100
    public static void main(String[] args) throws IOException {
        disableSslVerification();

        String url = "https://www.oda.go.kr/opo/bsin/bsnsSumryDocDetail.do?P_BSNS_NO=2024-00144";
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();

        
        Elements supportFieldRows = doc.select("tr:has(th:contains(지원분야))");

        String supportField = "";
        if (!supportFieldRows.isEmpty()) {
            Element row = supportFieldRows.first();
            supportField = row.select("td").text(); // td 안의 텍스트를 추출
        }

        System.out.println("지원분야: " + supportField);
        
         supportFieldRows = doc.select("tr:has(th:contains(사업대상지))");

        if (!supportFieldRows.isEmpty()) {
            Element row = supportFieldRows.first();
            supportField = row.select("td").text(); // td 안의 텍스트를 추출
        }

        System.out.println("사업대상지: " + supportField);
        
        
        
        List<FileInfo> fileList = new ArrayList<>();
        Elements rows = doc.select("#uploaded-files-tb tr");

        for (Element row : rows) {
            String fileName = row.select(".tb_file_name").text();
            Element linkElement = row.select(".tb_down_btn[href]").first();

            if (fileName != null && linkElement != null) {
                String relativeUrl = linkElement.attr("href");
                String absoluteUrl = "https://www.oda.go.kr" + relativeUrl;
                fileList.add(new FileInfo(fileName, absoluteUrl));
            }
        }

        // ✅ 다운로드 디렉토리 지정
        String downloadDir = "C:/test/";
        new File(downloadDir).mkdirs(); // 폴더 없으면 생성

        // ✅ 다운로드 실행
        for (FileInfo file : fileList) {
            System.out.println("Found file: " + file.fileName + " → " + file.downloadUrl);
            String safeFileName = file.fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
            downloadFile(file.downloadUrl, downloadDir + safeFileName);
        }
    }

}
