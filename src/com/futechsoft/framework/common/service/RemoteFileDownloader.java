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

    @Value("${download.chunk.size:10485760}") // 10MB 기본값
    private long chunkSize;

    @Value("${download.retry.max:3}")
    private int maxRetries;

    @Value("${download.retry.delay:1000}")
    private long retryDelay;

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
        	fileInfoVo = koicaScrapingService.getFileInfo(params);
        } else {
        	fileInfoVo = fileUploadService.getFileInfo(params);
        }

        long fileSize = fileInfoVo.getFileSize();
        LOGGER.debug("fileSize.......{}", fileSize);

        if (fileSize <= 0) {
            throw new IOException("Cannot determine file size");
        }

        // Range 요청 지원 여부 확인
        if (!supportsRangeRequests()) {
            LOGGER.warn("Server does not support Range requests. Using chunked download method.");
            downloadWithChunks(outputPath, fileSize);
            return;
        }

        // Range 요청이 지원되는 경우 기존 로직 사용 (대용량 파일의 경우)
        if (fileSize > 100 * 1024 * 1024) { // 100MB 이상이면 멀티스레드 사용
            downloadWithRangeRequests(outputPath, chunks, fileSize);
        } else {
            // 100MB 이하면 청크 방식으로 단순 다운로드
            downloadWithChunks(outputPath, fileSize);
        }
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

            if (jwtToken != null && !jwtToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            }

            conn.connect();

            int responseCode = conn.getResponseCode();

            LOGGER.debug("Range support check - responseCode: {}", responseCode);
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
     * 커넥션을 짧게 유지하면서 청크 단위로 다운로드
     */
    private void downloadWithChunks(String outputPath, long fileSize) throws IOException {
        File outputFile = new File(outputPath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            long downloaded = 0;
            int chunkIndex = 0;

            while (downloaded < fileSize) {
                long start = downloaded;
                long end = Math.min(downloaded + chunkSize - 1, fileSize - 1);

                LOGGER.debug("Downloading chunk {}: bytes {}-{}", chunkIndex, start, end);

                // 각 청크마다 새로운 커넥션으로 재시도 로직 포함 다운로드
                downloadChunkWithRetry(start, end, fos);

                downloaded = end + 1;
                chunkIndex++;

                // 진행률 로깅
                double progress = (double) downloaded / fileSize * 100;
                LOGGER.info("Downloaded chunk {}: {}/{} bytes ({:.2f}%)",
                           chunkIndex, downloaded, fileSize, progress);
            }

            LOGGER.info("Chunked download completed: {} bytes written to {}", downloaded, outputPath);
        }
    }

    /**
     * 재시도 로직을 포함한 단일 청크 다운로드
     */
    private void downloadChunkWithRetry(long start, long end, FileOutputStream fos) throws IOException {
        IOException lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                downloadChunk(start, end, fos);
                return; // 성공시 리턴

            } catch (IOException e) {
                lastException = e;
                LOGGER.warn("Chunk download attempt {} failed for range {}-{}: {}",
                           attempt, start, end, e.getMessage());

                if (attempt < maxRetries) {
                    // 재시도 전 잠시 대기 (지수 백오프)
                    try {
                        Thread.sleep(retryDelay * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Download interrupted", ie);
                    }
                }
            }
        }

        throw new IOException("Failed to download chunk after " + maxRetries + " attempts", lastException);
    }

    /**
     * Range 지원 시에만 사용 - 단일 청크 다운로드 (짧은 커넥션)
     */
    private void downloadChunk(long start, long end, FileOutputStream fos) throws IOException {
        HttpURLConnection conn = null;
        InputStream in = null;

        try {
            // 새로운 커넥션 생성
            conn = (HttpURLConnection) new URL(fileUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(connectionTimeout);
            conn.setReadTimeout(readTimeout);

            // Range 헤더 설정 (Range 지원이 확인된 경우에만 호출됨)
            conn.setRequestProperty("Range", "bytes=" + start + "-" + end);

            // 인증 설정
            if (jwtToken != null && !jwtToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            }

            conn.connect();

            int responseCode = conn.getResponseCode();

            // 206 (Partial Content) 응답을 기대
            if (responseCode != 206) {
                throw new IOException("Expected 206 Partial Content, got " + responseCode +
                                    " - Range request failed");
            }

            in = conn.getInputStream();

            // 청크 데이터 읽기
            byte[] buffer = new byte[8192];
            long expectedBytes = end - start + 1;
            long readBytes = 0;
            int len;

            while (readBytes < expectedBytes && (len = in.read(buffer)) != -1) {
                // 예상보다 많이 읽지 않도록 제한
                int writeLen = (int) Math.min(len, expectedBytes - readBytes);
                fos.write(buffer, 0, writeLen);
                readBytes += writeLen;

                // 예상 바이트를 다 읽었으면 종료
                if (readBytes >= expectedBytes) {
                    break;
                }
            }

            if (readBytes != expectedBytes) {
                throw new IOException("Read " + readBytes + " bytes, expected " + expectedBytes +
                                    " bytes for range " + start + "-" + end);
            }

        } finally {
            // 리소스 정리 (커넥션을 빨리 해제)
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.warn("Failed to close input stream: {}", e.getMessage());
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
                            downloadRangeToFile(s, e, chunkPath);
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
            throw new IOException("Parallel download failed", e);
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

        LOGGER.info("Parallel download complete: {}", outputPath);
    }

    /**
     * Range 요청으로 파일에 직접 다운로드
     */
    private void downloadRangeToFile(long start, long end, Path output) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(fileUrl).openConnection();
            conn.setRequestMethod("GET");

            String rangeHeader = "bytes=" + start + "-" + end;
            conn.setRequestProperty("Range", rangeHeader);

            // 인증 토큰 설정
            if (jwtToken != null && !jwtToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            }

            conn.setConnectTimeout(connectionTimeout);
            conn.setReadTimeout(readTimeout);

            // 디버깅 로그 추가
            LOGGER.debug("Range request - URL: {}", fileUrl);
            LOGGER.debug("Range request - Range header: {}", rangeHeader);

            conn.connect();

            int responseCode = conn.getResponseCode();
            LOGGER.debug("Range request - Response code: {}", responseCode);

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

    /**
     * 청크 파일들을 병합
     */
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

    /**
     * Range 미지원 시 사용 - 재시도 로직이 포함된 단일 연결 다운로드
     */
    /*
    private void downloadWholeFileWithRetry(String outputPath) throws IOException {
        IOException lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                downloadWholeFileOriginal(outputPath);
                return; // 성공시 리턴

            } catch (IOException e) {
                lastException = e;
                LOGGER.warn("Whole file download attempt {} failed: {}", attempt, e.getMessage());

                // 실패 시 부분 다운로드 파일 삭제
                File outputFile = new File(outputPath);
                if (outputFile.exists()) {
                    outputFile.delete();
                    LOGGER.info("Partial download file deleted: {}", outputPath);
                }

                if (attempt < maxRetries) {
                    // 재시도 전 잠시 대기 (지수 백오프)
                    try {
                        Thread.sleep(retryDelay * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Download interrupted", ie);
                    }
                }
            }
        }

        throw new IOException("Failed to download file after " + maxRetries + " attempts", lastException);
    }*/

    /**
     * 파일 크기를 얻기 위한 HEAD 요청
     */
    private long getFileSize() throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(fileUrl).openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(connectionTimeout);
            conn.setReadTimeout(readTimeout);

            if (jwtToken != null && !jwtToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            }

            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                return conn.getContentLengthLong();
            }

            return -1; // 크기를 알 수 없음

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}