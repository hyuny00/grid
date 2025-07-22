package com.futechsoft.framework.file.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.futechsoft.framework.common.controller.AbstractController;
import com.futechsoft.framework.common.service.RemoteFileDownloader;
import com.futechsoft.framework.exception.ErrorCode;
import com.futechsoft.framework.exception.FileDownloadException;
import com.futechsoft.framework.exception.FileUploadException;
import com.futechsoft.framework.file.service.FileUploadService;
import com.futechsoft.framework.file.vo.FileInfoVo;
import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.FileUtil;
import com.futechsoft.framework.util.FtMap;
import com.google.gson.Gson;

import kr.go.odakorea.gis.service.KoicaScrapingService;

@Controller
public class FileUploadController extends AbstractController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

	@Resource(name = "framework.file.service.FileUploadService")
	FileUploadService fileUploadService;
	
	@Resource(name = "gis.service.KoicaScrapingService")
	KoicaScrapingService koicaScrapingService;

	//@Autowired
	//PropertiesConfiguration propertiesConfiguration;
	
	@Value("${file.uploadPath.temp}")
	private String tempUploadPath;
	
	@Value("${file.uploadPath}")
	private String realUploadPath;
	
	@Value("${file.uploadPath.temp.zip}")
	private String tempZipPath;

	
	@Value("${file.upload.accept.doc}")
	private String[] acceptDocs;
	
	@Value("${file.upload.accept.image}")
	private String[] acceptImages;
	
	@Value("${file.upload.accept.multimedia}")
	private String[] acceptMultimedias;
	
	@Value("${file.upload.size}")
	private Long uploadSize;
	
	@Value("${file.upload.chunkSize}")
	private Long chunkSize;
	
	
	
	@Value("${ap.inOutDiv}")
	private String inOutDiv;
	
	
   @Value("${ap.inIp}")
   private String[] inIp;
   
   @Value("${ap.outIp}")
   private String[] outIp;
	
	
	@Autowired
	private RemoteFileDownloader remoteFileDownloader;

	@RequestMapping(value = "/file/upload")
	@ResponseBody
	public Map<String, Object> upload(Model model, @RequestParam MultipartFile file, @RequestParam String metadata)
			throws JsonMappingException, JsonProcessingException, FileUploadException {

		
		String acceptDoc = "";
		String acceptImage = "";
		String acceptMultimedia = "";
		for (String accept : acceptDocs) {
			acceptDoc += accept + ",";
		}
		for (String accept : acceptImages) {
			acceptImage += accept + ",";
		}
		for (String accept : acceptMultimedias) {
			acceptMultimedia += accept + ",";
		}

	

		Gson gson = new Gson();
		FileInfoVo fileInfoVo = gson.fromJson(metadata, FileInfoVo.class);

		Map<String, Object> fileInfo = new HashMap<String, Object>();

		String originFilename = fileInfoVo.getFileNm();

		String extName = "";
		if (originFilename.lastIndexOf(".") != -1) {
			extName = originFilename.substring(originFilename.lastIndexOf("."), originFilename.length());
		}

		if ((acceptDoc + "," + acceptImage + "," + acceptMultimedia).toUpperCase().indexOf(extName.toUpperCase()) == -1) {
			fileInfo.put("errorCode", ErrorCode.FILE_ACCEPT_ERROR.getCode());
			fileInfo.put("errorMessage",
					StringUtils.replace(ErrorCode.FILE_ACCEPT_ERROR.getMessage(), "{{fileExt}}", extName));
		}

		if (fileInfoVo.getFileSize() > uploadSize || file.getSize() > uploadSize || file.getSize() > chunkSize) {
			fileInfo.put("errorCode", ErrorCode.FILE_SIZE_ERROR.getCode());
			fileInfo.put("errorMessage", StringUtils.replace(ErrorCode.FILE_SIZE_ERROR.getMessage(), "{{fileSize}}",
					(uploadSize / 1024 / 1024) + "M"));

		} else {
			fileInfoVo = fileUploadService.upload(file, fileInfoVo);
			List<FileInfoVo> fileList = new ArrayList<FileInfoVo>();
			fileList.add(fileInfoVo);
			fileInfo.put("fileInfo", fileList);
		}

		return fileInfo;
	}

	@RequestMapping(value = "/file/fileList")
	@ResponseBody
	public Map<String, List<FtMap>> fileList(@RequestParam String docId) throws Exception {
		
		FtMap params = new FtMap();
		params.put("docId", docId);
		
		List<FtMap> fileList = fileUploadService.selectFileList(params);
		Map<String, List<FtMap>> fileInfo = new HashMap<String, List<FtMap>>();
		fileInfo.put("fileInfo", fileList);
		return fileInfo;

	}

	

	
	@RequestMapping(value = "/file/deleteFile")
	@ResponseBody
	public Map<String, List<FileInfoVo>> deleteFile(@RequestBody FileInfoVo[] fileInfoVos) throws Exception {


	
		
		/*
		 * Gson gson = new Gson();
		 * FileInfoVo[] fileInfoVos = gson.fromJson(delFileInfo, FileInfoVo[].class);
		 */
		List<FileInfoVo> fileList = new ArrayList<FileInfoVo>();

		for (FileInfoVo fileInfoVo : fileInfoVos) {

			File delFile = Paths.get(tempUploadPath, fileInfoVo.getFileId() + ".TEMP").toFile();

			if (delFile.exists()) {
				delFile.delete();
			}

			fileInfoVo.setDelYn("Y");
			fileList.add(fileInfoVo);

			// 실제파일삭제
			if (!(StringUtils.defaultString(fileInfoVo.getTemp()).equals("Y")) && fileInfoVo.getDelYn().equals("Y")) {

				FtMap params = new FtMap();
				params.put("fileId", fileInfoVo.getFileId());

				FileInfoVo realFileInfoVo = fileUploadService.getFileInfo(params);

				// 파일삭제권한
				boolean deleteFileCheck = true;
				deleteFileCheck = FileUtil.hasFileDeleteAuth(fileInfoVo, realFileInfoVo);

				/*
				delFile = Paths.get(realUploadPath, realFileInfoVo.getFilePath(), realFileInfoVo.getFileId() + ".FILE")
						.toFile();

				if (delFile.exists() && deleteFileCheck) {
					delFile.delete();
				}
				*/
				if (deleteFileCheck) {
					fileUploadService.deleteFile(params);
				}

			}

		}

		Map<String, List<FileInfoVo>> fileInfo = new HashMap<String, List<FileInfoVo>>();
		fileInfo.put("fileInfo", fileList);

		return fileInfo;
	}

	@RequestMapping(value = "/file/uploadForm")
	public String uploadForm(@RequestParam(required = false, defaultValue = "") String acceptType, HttpServletRequest req)
			throws Exception {

		setuploadForm(acceptType, req);
		return "framework/file/upload";
	}

	/**
	 * 업로드 폼 세팅
	 * 
	 * @param acceptType
	 * @param req
	 * @throws Exception
	 */
	private void setuploadForm(String acceptType, HttpServletRequest req) throws Exception {

		
		String acceptDoc = "";
		String acceptImage = "";
		String acceptMultimedia = "";
		for (String accept : acceptDocs) {
			acceptDoc += accept + ",";
		}
		for (String accept : acceptImages) {
			acceptImage += accept + ",";
		}
		for (String accept : acceptMultimedias) {
			acceptMultimedia += accept + ",";
		}

		String uploadFormId = FileUtil.getRandomId();

		req.setAttribute("uploadFormId", uploadFormId);

		if (acceptType.equals("doc")) {
			req.setAttribute("accept", acceptDoc);

		} else if (acceptType.equals("image")) {
			req.setAttribute("accept", acceptImage);

		} else if (acceptType.equals("multimedia")) {
			req.setAttribute("accept", acceptMultimedia);

		} else {
			req.setAttribute("accept", acceptDoc + "," + acceptImage + "," + acceptMultimedia);
		}
	}

	@RequestMapping(value = "/file/download")
	public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String[] remoteIps=null;
		
		
		String filePstnSecd="";
		if(inOutDiv.equals("in")){
			filePstnSecd="01";
			remoteIps= outIp;
			
		}else if(inOutDiv.equals("out")){
			filePstnSecd="02";
			remoteIps= inIp;
		}
		
		String selectedIp = remoteIps[ThreadLocalRandom.current().nextInt(remoteIps.length)];
		
		LOGGER.debug("selectedIp......{}",selectedIp);
		

		FtMap params = getFtMap(request);

		String downloadFileInfo = params.getString("downloadFileInfo");
		
		
		
		Gson gson = new Gson();
		FileInfoVo fileInfoVo = gson.fromJson(downloadFileInfo, FileInfoVo.class);

		File downloadFile = null;
		
		String fullRemoteUrl="";

		try {
			if (StringUtils.defaultString(fileInfoVo.getTemp(), "").equals("Y")) {
				downloadFile = Paths.get(tempUploadPath, fileInfoVo.getFileId() + ".TEMP").toFile();

			} else {
				
				params.put("fileId", fileInfoVo.getFileId());
				fileInfoVo = fileUploadService.getFileInfo(params);

				// 파일다운로드 권한
				boolean fileDownloadCheck = FileUtil.hasFileDownloadAuth(fileInfoVo);
				if (!fileDownloadCheck) {
					throw new FileDownloadException(ErrorCode.FILE_ACCESS_DENIED.getMessage());
				}
				
				
				if(filePstnSecd.equals(fileInfoVo.getFilePstnSecd())) {
					
					if(fileInfoVo.getFileId().length()==32) {
						downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();
					}else {
						downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId()).toFile();
					}
					
				}else {
					
					String jwtToken = null;
					    
				    Cookie[] cookies = request.getCookies();
				    if (cookies != null) {
				        for (Cookie cookie : cookies) {
				            if ("JWT".equals(cookie.getName())) {
				                jwtToken = cookie.getValue();
				                break;
				            }
				        }
				    }
					    
				    LOGGER.debug("jwtToken....{}", jwtToken);

					// 최적의 청크 수 계산
					int optimalChunks = calculateOptimalChunks(fileInfoVo.getFileSize());

					// 임시 파일 경로 생성
					String tempFileName = fileInfoVo.getFileId() + "_" + System.currentTimeMillis() + ".tmp";
					downloadFile = Paths.get(tempUploadPath, tempFileName).toFile();

					fullRemoteUrl = "http://"+selectedIp+"/file/remoteDownload/" + fileInfoVo.getFileId();

					// 여기서 사용!
					remoteFileDownloader.downloadFileFromOtherWAS(fullRemoteUrl, jwtToken, downloadFile.getAbsolutePath(),
							optimalChunks);
					 
				}
				
				
			

			}

			if (downloadFile.isFile() && downloadFile.exists()) {

				response.setContentType("application/octet-stream; charset=utf-8");

				if (downloadFile.length() > 1024 * 1024 * 20) { // 20M
					response.setHeader("Content-Transfer-Encoding", "chunked");
				} else {
					response.setContentLength((int) downloadFile.length());
					response.setHeader("Content-Transfer-Encoding", "binary");
				}
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + java.net.URLEncoder.encode(fileInfoVo.getFileNm(), "utf-8") + "\";");

				try (OutputStream out = response.getOutputStream(); FileInputStream fis = new FileInputStream(downloadFile);) {
					// FileCopyUtils.copy(fis, out);
					CommonUtil.copy(fis, out);
				} catch (Exception e) {
					e.printStackTrace();
					throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
				}
			} else {
				throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new FileDownloadException(e.getMessage());
		
		}finally {
	        // 원격에서 받은 임시 파일 정리
	        if (StringUtils.isNotEmpty(fullRemoteUrl) && downloadFile != null) {
	            try {
	              //  Files.deleteIfExists(downloadFile.toPath());
	            } catch (Exception ignored) {}
	        }
	    }

	}
	
	@RequestMapping(value = "/file/remoteDownload/{fileId}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public void remoteDownload(
	    @PathVariable String fileId,
	    @RequestHeader(value = "Range", required = false) String rangeHeader,
	    HttpServletRequest request, 
	    HttpServletResponse response) throws Exception {
		
		
		 LOGGER.info("Request Method: {}", request.getMethod());
		    LOGGER.info("Request Headers: {}", Collections.list(request.getHeaderNames()));
		    LOGGER.info("Range Header: {}", rangeHeader);
	    
	    File downloadFile = null;
	    try {
	        FtMap params = new FtMap();
	        params.put("fileId", fileId);
	        FileInfoVo fileInfoVo = fileUploadService.getFileInfo(params);
	        
	        // 파일다운로드 권한
	        boolean fileDownloadCheck = FileUtil.hasFileDownloadAuth(fileInfoVo);
	        if (!fileDownloadCheck) {
	            throw new FileDownloadException(ErrorCode.FILE_ACCESS_DENIED.getMessage());
	        }
	        
	       
	        if(fileInfoVo.getFileId().length()==32) {
				downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();
			}else {
				downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId()).toFile();
			}
	        
	        
	        if (!downloadFile.exists() || !downloadFile.isFile()) {
	            throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
	        }
	        
	        long fileLength = downloadFile.length();
	        long start = 0;
	        long end = fileLength - 1;
	        
	        // Range 헤더 처리
	        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
	            String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
	            try {
	                start = Long.parseLong(ranges[0]);
	                if (ranges.length > 1 && !ranges[1].isEmpty()) {
	                    end = Long.parseLong(ranges[1]);
	                }
	                if (end >= fileLength) end = fileLength - 1;
	                if (start > end || start < 0) {
	                    response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
	                    response.setHeader("Content-Range", "bytes */" + fileLength);
	                    return;
	                }
	            } catch (NumberFormatException e) {
	                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	                return;
	            }
	        }
	        
	        long contentLength = end - start + 1;
	        
	        // 헤더 설정
	        response.setContentType("application/octet-stream; charset=utf-8");
	        response.setHeader("Accept-Ranges", "bytes");
	        response.setHeader("Content-Disposition",
	                "attachment; filename=\"" + java.net.URLEncoder.encode(fileInfoVo.getFileNm(), "utf-8") + "\";");
	        
	        if (rangeHeader != null) {
	            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206
	            response.setHeader("Content-Range", String.format("bytes %d-%d/%d", start, end, fileLength));
	        }
	        
	        response.setContentLength((int) contentLength);
	        
	        // 파일 전송
	        try (OutputStream out = response.getOutputStream(); 
	             FileInputStream fis = new FileInputStream(downloadFile);) {
	            
	            // 시작 위치로 이동
	            fis.skip(start);
	            
	            byte[] buffer = new byte[8192];
	            long remaining = contentLength;
	            int bytesRead;
	            
	            while (remaining > 0 && (bytesRead = fis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
	                out.write(buffer, 0, bytesRead);
	                remaining -= bytesRead;
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
	        }
	        
	    } catch (Exception e) {
	        throw new FileDownloadException(e.getMessage());
	    }
	}
	
	
	/**
	 * 외부망에서 수집한 코이카 파일다운로드용
	 * @param fileId
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/file/koicaFileDownload/{fileId}")
	public void koicaFileDownload( @PathVariable String fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String[] remoteIps=null;
		
		
		String filePstnSecd="";
		if(inOutDiv.equals("in")){
			filePstnSecd="01";
			remoteIps= outIp;
		}else if(inOutDiv.equals("out")){
			filePstnSecd="02";
		}
		
	
		//코이카 파일정보를 db에서 가져와야함
		FtMap params = new FtMap();
		params.put("fileId", fileId);
		FileInfoVo fileInfoVo = koicaScrapingService.getFileInfo(params);
				


		File downloadFile = null;
		
		String fullRemoteUrl="";

		try {
				//외부망이면 바로 다운로드
				if(filePstnSecd.equals("02")) {
					
					downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();
					
				}else {
					
					String selectedIp = remoteIps[ThreadLocalRandom.current().nextInt(remoteIps.length)];
					
					LOGGER.debug("selectedIp......{}",selectedIp);
					
					
					String jwtToken = null;
					    
				    Cookie[] cookies = request.getCookies();
				    if (cookies != null) {
				        for (Cookie cookie : cookies) {
				            if ("JWT".equals(cookie.getName())) {
				                jwtToken = cookie.getValue();
				                break;
				            }
				        }
				    }
					    
				    LOGGER.debug("jwtToken....{}", jwtToken);

					// 최적의 청크 수 계산
					int optimalChunks = calculateOptimalChunks(fileInfoVo.getFileSize());

					// 임시 파일 경로 생성
					String tempFileName = fileInfoVo.getFileId() + "_" + System.currentTimeMillis() + ".tmp";
					downloadFile = Paths.get(tempUploadPath, tempFileName).toFile();

					fullRemoteUrl = "http://"+selectedIp+"/file/remoteKoicaDownload/" + fileInfoVo.getFileId();

					// 여기서 사용!
					remoteFileDownloader.downloadFileFromOtherWAS(fullRemoteUrl, jwtToken, downloadFile.getAbsolutePath(),
							optimalChunks);
					 
				}
				
				
			

			

			if (downloadFile.isFile() && downloadFile.exists()) {

				response.setContentType("application/octet-stream; charset=utf-8");

				if (downloadFile.length() > 1024 * 1024 * 20) { // 20M
					response.setHeader("Content-Transfer-Encoding", "chunked");
				} else {
					response.setContentLength((int) downloadFile.length());
					response.setHeader("Content-Transfer-Encoding", "binary");
				}
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + java.net.URLEncoder.encode(fileInfoVo.getFileNm(), "utf-8") + "\";");

				try (OutputStream out = response.getOutputStream(); FileInputStream fis = new FileInputStream(downloadFile);) {
					// FileCopyUtils.copy(fis, out);
					CommonUtil.copy(fis, out);
				} catch (Exception e) {
					e.printStackTrace();
					throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
				}
			} else {
				throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new FileDownloadException(e.getMessage());
		
		}finally {
	        // 원격에서 받은 임시 파일 정리
	        if (StringUtils.isNotEmpty(fullRemoteUrl) && downloadFile != null) {
	            try {
	              //  Files.deleteIfExists(downloadFile.toPath());
	            } catch (Exception ignored) {}
	        }
	    }

	}
	
	/**
	 * 
	 * 내부망에서만 호출함. 이메소드는 외부망에만 있으면 됨
	 * 외부망에서  oda지식서비스 자료 수집한 파일조회
	 */
	@RequestMapping(value = "/file/remoteKoicaDownload/{fileId}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public void remoteOdaDownload(
	    @PathVariable String fileId,
	    @RequestHeader(value = "Range", required = false) String rangeHeader,
	    HttpServletRequest request, 
	    HttpServletResponse response) throws Exception {
		
		
		 LOGGER.info("Request Method: {}", request.getMethod());
		    LOGGER.info("Request Headers: {}", Collections.list(request.getHeaderNames()));
		    LOGGER.info("Range Header: {}", rangeHeader);
	    
	    File downloadFile = null;
	    try {
	        FtMap params = new FtMap();
	        params.put("fileId", fileId);
	        
	        //ODA지식서비스용으로 수정
	        FileInfoVo fileInfoVo = koicaScrapingService.getFileInfo(params);
	        
	        // 파일다운로드 권한
	        boolean fileDownloadCheck = FileUtil.hasFileDownloadAuth(fileInfoVo);
	        if (!fileDownloadCheck) {
	            throw new FileDownloadException(ErrorCode.FILE_ACCESS_DENIED.getMessage());
	        }
	        
	       
			downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();
			
	        
	        
	        if (!downloadFile.exists() || !downloadFile.isFile()) {
	            throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
	        }
	        
	        long fileLength = downloadFile.length();
	        long start = 0;
	        long end = fileLength - 1;
	        
	        // Range 헤더 처리
	        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
	            String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
	            try {
	                start = Long.parseLong(ranges[0]);
	                if (ranges.length > 1 && !ranges[1].isEmpty()) {
	                    end = Long.parseLong(ranges[1]);
	                }
	                if (end >= fileLength) end = fileLength - 1;
	                if (start > end || start < 0) {
	                    response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
	                    response.setHeader("Content-Range", "bytes */" + fileLength);
	                    return;
	                }
	            } catch (NumberFormatException e) {
	                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	                return;
	            }
	        }
	        
	        long contentLength = end - start + 1;
	        
	        // 헤더 설정
	        response.setContentType("application/octet-stream; charset=utf-8");
	        response.setHeader("Accept-Ranges", "bytes");
	        response.setHeader("Content-Disposition",
	                "attachment; filename=\"" + java.net.URLEncoder.encode(fileInfoVo.getFileNm(), "utf-8") + "\";");
	        
	        if (rangeHeader != null) {
	            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206
	            response.setHeader("Content-Range", String.format("bytes %d-%d/%d", start, end, fileLength));
	        }
	        
	        response.setContentLength((int) contentLength);
	        
	        // 파일 전송
	        try (OutputStream out = response.getOutputStream(); 
	             FileInputStream fis = new FileInputStream(downloadFile);) {
	            
	            // 시작 위치로 이동
	            fis.skip(start);
	            
	            byte[] buffer = new byte[8192];
	            long remaining = contentLength;
	            int bytesRead;
	            
	            while (remaining > 0 && (bytesRead = fis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
	                out.write(buffer, 0, bytesRead);
	                remaining -= bytesRead;
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
	        }
	        
	    } catch (Exception e) {
	        throw new FileDownloadException(e.getMessage());
	    }
	}
	
	 

	@RequestMapping(value = "/file/save")
	public String save(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		fileUploadService.saveFile(params, "");

		return "core/file/upload";
	}

	@RequestMapping(value = "/file/download/zip")
	public void downloadZip(HttpServletRequest request, HttpServletResponse res) throws Exception {
		
		
		

		FtMap params = getFtMap(request);

		String downloadFileInfo = params.getString("downloadFileInfo");

		Gson gson = new Gson();
		FileInfoVo[] fileInfoVos = gson.fromJson(downloadFileInfo, FileInfoVo[].class);

		String zipFileName = FileUtil.getRandomId() + ".zip";

		fileUploadService.createZip(fileInfoVos, tempZipPath + File.separator + zipFileName);

		// 파일다운로드
		File downloadFile = Paths.get(tempZipPath, zipFileName).toFile();

		if (downloadFile.isFile() && downloadFile.exists()) {

			res.setContentType("application/octet-stream; charset=utf-8");

			if (downloadFile.length() > 1024 * 1024 * 20) { // 20M
				res.setHeader("Content-Transfer-Encoding", "chunked");
			} else {
				res.setContentLength((int) downloadFile.length());
				res.setHeader("Content-Transfer-Encoding", "binary");
			}

			res.setHeader("Content-Disposition",
					"attachment; filename=\"" + java.net.URLEncoder.encode(zipFileName, "utf-8") + "\";");

			try (OutputStream out = res.getOutputStream(); FileInputStream fis = new FileInputStream(downloadFile);) {
				// FileCopyUtils.copy(fis, out);
				CommonUtil.copy(fis, out);
				if (downloadFile.exists())
					downloadFile.delete();
			} catch (IOException e) {
				if (downloadFile.exists())
					downloadFile.delete();
				e.printStackTrace();
			}

		} else {
			throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
		}
	}

	@RequestMapping(value = "/file/isExistFile")
	@ResponseBody
	public Map<String, Object> isExistFile(HttpServletRequest request, FileInfoVo fileInfoVo) throws Exception {
		
		

		FtMap params = getFtMap(request);

		boolean fileDownloadCheck = true;

		File downloadFile = null;

		if (StringUtils.defaultString(params.getString("temp"), "").equals("Y")) {
			downloadFile = Paths.get(tempUploadPath, fileInfoVo.getFileId() + ".TEMP").toFile();

		} else {
			params.put("fileId", fileInfoVo.getFileId());
			fileInfoVo = fileUploadService.getFileInfo(params);

			// 파일다운로드 권한
			fileDownloadCheck = FileUtil.hasFileDownloadAuth(fileInfoVo);

			downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();

		}

		Map<String, Object> map = new HashMap<String, Object>();

		if (!downloadFile.exists()) {
			map.put("msg", ErrorCode.FILE_NOT_FOUND.getMessage());
		} else if (!fileDownloadCheck) {
			map.put("msg", ErrorCode.FILE_ACCESS_DENIED.getMessage());
		} else {
			map.put("msg", "SUCCESS");
		}

		return map;

	}

	

	 private int calculateOptimalChunks(long fileSize) {
        // 최소 청크 크기 (10MB)
        long minChunkSize = 10 * 1024 * 1024;
        // 최대 청크 크기 (100MB)
        long maxChunkSize = 100 * 1024 * 1024;
        
        if (fileSize <= minChunkSize) {
            return 1; // 10MB 이하면 단일 스레드
        } else if (fileSize <= 50 * 1024 * 1024) { // 50MB 이하
            return 2;
        } else if (fileSize <= 100 * 1024 * 1024) { // 100MB 이하
            return 4;
        } else if (fileSize <= 500 * 1024 * 1024) { // 500MB 이하
            return 6;
        } else if (fileSize <= 1024 * 1024 * 1024) { // 1GB 이하
            return 8;
        } else if (fileSize <= 5L * 1024 * 1024 * 1024) { // 5GB 이하
            return 10;
        } else {
            return 12; // 최대 12개 청크
        }
    }
}
