package com.futechsoft.framework.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futechsoft.framework.common.service.RemoteFileDownloader;

@RestController
@RequestMapping("/remote")
public class FileDownloadController {

    @Autowired
    private RemoteFileDownloader downloader;

    @GetMapping("/fetch")
    public ResponseEntity<String> fetchFile() {
        try {
            downloader.downloadFileFromOtherWAS("/tmp/downloaded_from_b.zip", 4);
            return ResponseEntity.ok("File downloaded from B WAS.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Download failed: " + e.getMessage());
        }
    }
}
