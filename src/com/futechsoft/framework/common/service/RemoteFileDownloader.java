package com.futechsoft.framework.common.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.futechsoft.framework.file.service.FileUploadService;
import com.futechsoft.framework.file.vo.FileInfoVo;
import com.futechsoft.framework.util.FtMap;

import kr.go.odakorea.gis.service.KoicaScrapingService;

@Service
public class RemoteFileDownloader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFileDownloader.class);
    
    private String fileUrl;
    private String jwtToken;
    
    @Value("${download.connection.timeout:30000}")
    private int connectionTimeout;
    
    @Value("${download.read.timeout:60000}")
    private int readTimeout;
    
    @Resource(name = "framework.file.service.FileUploadService")
    FileUploadService fileUploadService;
    
	
	@Resource(name = "gis.service.KoicaScrapingService")
	KoicaScrapingService koicaScrapingService;

    public void downloadFileFromOtherWAS(String remoteUrl, String jwtToken, String outputPath, int chunks) throws Exception {
        String originalUrl = this.fileUrl;
        try {
            this.fileUrl = remoteUrl;
            this.jwtToken = jwtToken;
            downloadFileFromOtherWAS(outputPath, chunks);
        } finally {
            this.fileUrl = originalUrl;
        }
    }
    
    public void downloadFileFromOtherWAS(String outputPath, int chunks) throws Exception {
        LOGGER.debug("fileUrl.......{}", fileUrl);
        
        String[] parts = fileUrl.split("/");
        String fileId = parts[parts.length - 1];
        
        FtMap params = new FtMap();
        params.put("fileId", fileId);
        
        FileInfoVo fileInfoVo = null;
        
        if (fileUrl != null && fileUrl.contains("/file/remoteKoicaDownload/")) {
        	fileInfoVo= koicaScrapingService.getFileInfo(params);
        }else {
        	fileInfoVo = fileUploadService.getFileInfo(params);
        }
        
        
        long fileSize = fileInfoVo.getFileSize();
        LOGGER.debug("fileSize.......{}", fileSize);
        
        if (fileSize <= 0) {
            throw new IOException("Cannot determine file size");
        }
        
        // Range 요청 지원 여부 확인
        if (!supportsRangeRequests()) {
            LOGGER.warn("Server does not support Range requests. Falling back to single-threaded download.");
            downloadWholeFile(outputPath);
            return;
        }
        
        // Range 요청이 지원되는 경우 기존 로직 사용
        downloadWithRangeRequests(outputPath, chunks, fileSize);
        
    }
    
    /**
     * Range 요청 지원 여부 확인
     */
    private boolean supportsRangeRequests() throws IOException {
        HttpURLConnection conn = null;
        try {
            // 먼저 작은 Range 요청으로 실제 테스트
            conn = (HttpURLConnection) new URL(fileUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Range", "bytes=0-0");  // 첫 1바이트만 요청
            conn.setConnectTimeout(connectionTimeout);
            conn.setReadTimeout(readTimeout);
            
          
            conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            
            conn.connect();
            
            int responseCode = conn.getResponseCode();
            
            LOGGER.debug("responseCode......{}",responseCode);
            return responseCode == 206;  // Partial Content면 Range 지원
            
        } catch (IOException e) {
            LOGGER.warn("Failed to check Range support: {}", e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    
    /**
     * Range 요청 없이 전체 파일 다운로드
     */
    private void downloadWholeFile(String outputPath) throws IOException {
        HttpURLConnection conn = null;
        InputStream in = null;
        OutputStream out = null;
        
        try {
            // HTTP 연결 설정
            conn = (HttpURLConnection) new URL(fileUrl).openConnection();
            conn.setConnectTimeout(connectionTimeout);
            conn.setReadTimeout(readTimeout);
            
            // 필요한 헤더 설정 (인증이 필요한 경우)
             conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
             conn.setRequestProperty("User-Agent", "RemoteFileDownloader/1.0");
            
            conn.connect();
            
            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode + " - " + conn.getResponseMessage());
            }
            
            // 파일 크기 확인
            long contentLength = conn.getContentLengthLong();
            if (contentLength == -1) {
                LOGGER.warn("Content-Length header not available. Unable to track progress.");
            } else {
                LOGGER.info("Starting download of {} bytes", contentLength);
            }
            
            // 출력 디렉토리 생성
            File outputFile = new File(outputPath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // 스트리밍 다운로드
            in = conn.getInputStream();
            out = new FileOutputStream(outputPath);
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalDownloaded = 0;
            long lastLogTime = System.currentTimeMillis();
            
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalDownloaded += bytesRead;
                
                // 진행률 로깅 (1MB마다 또는 10초마다)
                long currentTime = System.currentTimeMillis();
                if (totalDownloaded % (1024 * 1024) == 0 || (currentTime - lastLogTime) > 10000) {
                    if (contentLength > 0) {
                        double progress = (double) totalDownloaded / contentLength * 100;
                        LOGGER.info("Downloaded: {} bytes ({:.2f}%)", totalDownloaded, progress);
                    } else {
                        LOGGER.info("Downloaded: {} bytes", totalDownloaded);
                    }
                    lastLogTime = currentTime;
                }
            }
            
            LOGGER.info("Download completed successfully: {} bytes written to {}", totalDownloaded, outputPath);
            
            // 파일 크기 검증
            if (contentLength > 0 && totalDownloaded != contentLength) {
                LOGGER.warn("Downloaded size ({}) does not match expected size ({})", 
                           totalDownloaded, contentLength);
            }
            
        } catch (IOException e) {
            LOGGER.error("Download failed: {}", e.getMessage());
            
            // 실패 시 부분 다운로드 파일 삭제
            File outputFile = new File(outputPath);
            if (outputFile.exists()) {
                outputFile.delete();
                LOGGER.info("Partial download file deleted: {}", outputPath);
            }
            
            throw e;
        } finally {
            // 리소스 정리
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.warn("Failed to close input stream: {}", e.getMessage());
                }
            }
            
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.warn("Failed to close output stream: {}", e.getMessage());
                }
            }
            
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    /**
     * Range 요청을 사용한 멀티스레드 다운로드
     */
    private void downloadWithRangeRequests(String outputPath, int chunks, long fileSize) throws Exception {
        long chunkSize = fileSize / chunks;
        ExecutorService executor = Executors.newFixedThreadPool(chunks);
        List<Path> chunkFiles = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicBoolean hasError = new AtomicBoolean(false);

        try {
            for (int i = 0; i < chunks; i++) {
                long start = i * chunkSize;
                long end = (i == chunks - 1) ? fileSize - 1 : (start + chunkSize - 1);
                Path chunkPath = Paths.get(outputPath + ".part" + i);
                chunkFiles.add(chunkPath);

                final long s = start, e = end;
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        if (!hasError.get()) {
                            downloadRange(s, e, chunkPath);
                        }
                    } catch (IOException ex) {
                        hasError.set(true);
                        LOGGER.error("Chunk download failed: {}-{}", s, e, ex);
                        throw new RuntimeException("Chunk download failed: " + s + "-" + e, ex);
                    }
                }, executor);
                
                futures.add(future);
            }

            // 모든 다운로드 완료 대기
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(30, TimeUnit.MINUTES);

            if (hasError.get()) {
                throw new IOException("One or more chunks failed to download");
            }

            // 병합
            mergeChunks(chunkFiles, Paths.get(outputPath));
            
        } catch (Exception e) {
            throw new IOException("Download failed", e);
        } finally {
            executor.shutdown();
            
            // 임시 파일 정리
            for (Path path : chunkFiles) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    LOGGER.warn("Failed to delete temp file: {}", path);
                }
            }
        }
        
        LOGGER.info("Download complete: {}", outputPath);
    }
    
    private void downloadRange(long start, long end, Path output) throws IOException {
        HttpURLConnection conn = null;
        try {
        	 conn = (HttpURLConnection) new URL(fileUrl).openConnection();
             conn.setRequestMethod("GET");
             
             String rangeHeader = "bytes=" + start + "-" + end;
             conn.setRequestProperty("Range", rangeHeader);
             
             // 인증 토큰 설정 (downloadWholeFile과 동일한 방식 사용)
             if (jwtToken != null && !jwtToken.isEmpty()) {
                 conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
                 // 또는 쿠키 방식: conn.setRequestProperty("Cookie", "JWT=" + jwtToken);
             }
             
             conn.setConnectTimeout(connectionTimeout);
             conn.setReadTimeout(readTimeout);
             
             // 디버깅 로그 추가
             LOGGER.debug("Range request - URL: {}", fileUrl);
             LOGGER.debug("Range request - Range header: {}", rangeHeader);
             LOGGER.debug("Range request - Authorization header: {}",   conn.getRequestProperty("Authorization"));
             
             conn.connect();
            
             int responseCode = conn.getResponseCode();
             LOGGER.debug("Range request - Response code: {}", responseCode);
             LOGGER.debug("Range request - Response headers: {}", conn.getHeaderFields());
             
             if (responseCode != 206) {
                 throw new IOException("Expected 206 Partial Content, got " + responseCode + 
                                     ". Server may not support Range requests.");
             }
             
            
            try (InputStream in = conn.getInputStream();
                 OutputStream out = Files.newOutputStream(output)) {
                
                byte[] buffer = new byte[8192];
                int len;
                long downloaded = 0;
                long expectedSize = end - start + 1;
                
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    downloaded += len;
                    
                    if (downloaded > expectedSize) {
                        throw new IOException("Downloaded more data than expected");
                    }
                }
                
                if (downloaded != expectedSize) {
                    throw new IOException("Downloaded " + downloaded + " bytes, expected " + expectedSize);
                }
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    private void mergeChunks(List<Path> chunkPaths, Path finalPath) throws IOException {
        try (OutputStream out = new FileOutputStream(finalPath.toFile())) {
            for (Path path : chunkPaths) {
                if (!Files.exists(path)) {
                    throw new IOException("Chunk file missing: " + path);
                }
                Files.copy(path, out);
            }
        }
    }
}