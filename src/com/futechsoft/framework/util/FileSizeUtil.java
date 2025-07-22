package com.futechsoft.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileSizeUtil {
    
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    
    /**
     * HEAD 요청으로 Content-Length 확인 후, 없으면
     * Range: bytes=0-0 요청해서 Content-Range로 추출
     * 실패 시 -1 반환
     */
    public static long getReliableFileSize(String fileUrl) {
        try {
            long size = getSizeByHead(fileUrl);
            if (size >= 0) {
                return size;
            }
            return getSizeByRangeRequest(fileUrl);
        } catch (IOException e) {
            System.err.println("Failed to get file size for URL: " + fileUrl + ", Error: " + e.getMessage());
            return -1;
        }
    }
    
    private static long getSizeByHead(String fileUrl) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(fileUrl).openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                long length = conn.getContentLengthLong();
                return length; // -1이면 Content-Length가 없음
            } else {
                System.err.println("HEAD request failed with status: " + responseCode);
                return -1;
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    private static long getSizeByRangeRequest(String fileUrl) throws IOException {
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        
        try {
            conn = (HttpURLConnection) new URL(fileUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Range", "bytes=0-0");
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            
            int status = conn.getResponseCode();
            if (status == 206) { // Partial Content
                String contentRange = conn.getHeaderField("Content-Range");
                if (contentRange != null) {
                    // Content-Range 형식: "bytes 0-0/12345678" 또는 "bytes 0-0/*"
                    long totalSize = parseContentRangeSize(contentRange);
                    if (totalSize > 0) {
                        return totalSize;
                    }
                }
                
                // InputStream을 열어서 정리 (일부 서버에서 필요)
                inputStream = conn.getInputStream();
                // 실제로는 데이터를 읽지 않음, 단지 연결 정리용
                
            } else if (status == 416) {
                // Range Not Satisfiable - 파일이 0바이트일 수 있음
                return 0;
            } else {
                System.err.println("Range request failed with status: " + status);
            }
            
            return -1;
            
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {}
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    private static long parseContentRangeSize(String contentRange) {
        try {
            // "bytes 0-0/12345678" 형식 파싱
            int slashIndex = contentRange.lastIndexOf('/');
            if (slashIndex != -1) {
                String totalLengthStr = contentRange.substring(slashIndex + 1).trim();
                
                // "*" 인 경우 크기를 알 수 없음
                if ("*".equals(totalLengthStr)) {
                    return -1;
                }
                
                return Long.parseLong(totalLengthStr);
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            System.err.println("Failed to parse Content-Range: " + contentRange + ", Error: " + e.getMessage());
        }
        return -1;
    }
}