package com.futechsoft.framework.common.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
public class FileRangeController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileRangeController.class);


    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFileWithRange(
            @PathVariable String filename,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            HttpServletRequest request
    ) throws IOException {
    	
    
    	 // 1. 먼저 파일명 검증
        if (!isValidFilename(filename)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        File file = new File("/data/" + filename);
        
        // 2. 경로 검증
        if (!file.getCanonicalPath().startsWith("/data/")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // 3. 파일 존재 및 권한 확인
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            return ResponseEntity.notFound().build();
        }
    	    
    	
    	 
        long fileLength = file.length();
        InputStream inputStream=null;
        long start = 0;
        long end = fileLength - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
            try {
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
                if (end >= fileLength) end = fileLength - 1;
                if (start > end || start < 0) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                            .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
                            .build();
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            
            long skipped = 0;
            while (skipped < start) {
                long n = inputStream.skip(start - skipped);
                if (n == 0) {
                    // skip이 0을 반환하면 read()로 한 바이트씩 건너뛰기 시도
                    if (inputStream.read() == -1) {
                        // 파일 끝에 도달
                        break;
                    }
                    skipped++;
                } else {
                    skipped += n;
                }
            }

            if (skipped < start) {
                inputStream.close();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            
            
            long contentLength = end - start + 1;
            InputStreamResource resource = new InputStreamResource(new LimitedInputStream(inputStream, contentLength));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(contentLength);
            headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
            
            if (rangeHeader != null) {
                headers.add(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, fileLength));
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers).body(resource);
            } else {
                return ResponseEntity.ok().headers(headers).body(resource);
            }
            
        } catch (IOException e) {
        	
        	LOGGER.error("File download error for: " + filename, e);  // 로깅 추가
        	 
            if (inputStream != null) {
                try { inputStream.close(); } catch (IOException ignored) {}
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
      
        
    }

    // InputStream wrapper that limits the number of bytes read
    public static class LimitedInputStream extends FilterInputStream {
        private long remaining;

        protected LimitedInputStream(InputStream in, long limit) {
            super(in);
            this.remaining = limit;
        }

        @Override
        public int read() throws IOException {
            if (remaining <= 0) return -1;
            int result = super.read();
            if (result != -1) remaining--;
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (remaining <= 0) return -1;
            len = (int) Math.min(len, remaining);
            int result = super.read(b, off, len);
            if (result != -1) remaining -= result;
            return result;
        }
    }
    
    private boolean isValidFilename(String filename) {
        return filename != null && 
               !filename.contains("..") && 
               !filename.contains("/") && 
               !filename.contains("\\") &&
               filename.matches("[a-zA-Z0-9._-]+");
    }

   
}
