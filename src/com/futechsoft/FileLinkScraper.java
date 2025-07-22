package com.futechsoft;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileLinkScraper {
	
	
	
//https://oda.go.kr/opo/bsin/bsnsDtlsList.do?P_PAGE_NO=1&P_PAGE_SIZE=100
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

    public static void main(String[] args) throws IOException {
        String url = "https://www.oda.go.kr/opo/bsin/bsnsSumryDocDetail.do?P_BSNS_NO=2024-00144";

        SslBypassUtil.disableSslVerification(); // << 인증 우회

        // 기존 Jsoup 코드
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36")
                .timeout(10000)
                .get();

        List<FileInfo> fileList = new ArrayList<>();

        Elements rows = doc.select("#uploaded-files-tb tr");

        for (Element row : rows) {
            String fileName = row.select(".tb_file_name").text();
            Element linkElement = row.select(".tb_down_btn[href]").first(); // 다운로드 링크 (다운로드용 a 태그)

            if (fileName != null && linkElement != null) {
                String relativeUrl = linkElement.attr("href");
                String absoluteUrl = "https://www.oda.go.kr" + relativeUrl;

                fileList.add(new FileInfo(fileName, absoluteUrl));
            }
        }

        // 결과 출력
        for (FileInfo file : fileList) {
            System.out.println(file);
        }
    }
}
