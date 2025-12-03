package com.futechsoft.framework.file.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
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
import com.futechsoft.framework.common.pagination.Page;
import com.futechsoft.framework.common.pagination.Pageable;
import com.futechsoft.framework.common.service.RemoteFileDownloader;
import com.futechsoft.framework.exception.ErrorCode;
import com.futechsoft.framework.exception.FileDownloadException;
import com.futechsoft.framework.exception.FileUploadException;
import com.futechsoft.framework.file.service.FileUploadService;
import com.futechsoft.framework.file.vo.FileInfoVo;
import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.FileUtil;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;
import com.google.gson.Gson;
import java.util.Base64;
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
	public Map<String, Object> upload(Model model, HttpSession session,  @RequestParam MultipartFile file, @RequestParam String metadata)
			throws JsonMappingException, JsonProcessingException, FileUploadException {

/*
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
*/

		String acceptDoc = String.join(",", acceptDocs);
		String acceptImage = String.join(",", acceptImages);
		String acceptMultimedia = String.join(",", acceptMultimedias);


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

			if(CommonUtil.nvl(fileInfoVo.getExtractZipYn()).equals("Y") && fileInfoVo.getUploadComplete().equals("Y")) {


				File tempFile = new File(fileInfoVo.getTempFilePath());

				// 새 파일명: fileId.zip
				File zipFile = new File(tempFile.getParent(), fileInfoVo.getFileId() + ".zip");

				 List<FileInfoVo> fileList = new ArrayList<>();

				if (tempFile.renameTo(zipFile)) {
					try {
						fileList = FileUtil.extractZip(zipFile, tempZipPath);
						fileInfo.put("fileInfo", fileList);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						fileInfo.put("errorCode", ErrorCode.ZIP_EXT_ERROR.getCode());
						fileInfo.put("errorMessage", ErrorCode.ZIP_EXT_ERROR.getMessage());

					}
				}




			}else {
				List<FileInfoVo> fileList = new ArrayList<FileInfoVo>();

				session.setAttribute("getFileExt", fileInfoVo.getFileExt());
				session.setAttribute("getTempFilePath", fileInfoVo.getTempFilePath());

				fileList.add(fileInfoVo);
				fileInfo.put("fileInfo", fileList);
			}

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

/*
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
*/

		String acceptDoc = String.join(",", acceptDocs);
		String acceptImage = String.join(",", acceptImages);
		String acceptMultimedia = String.join(",", acceptMultimedias);

		String acceptAll = String.join(",", acceptDoc, acceptImage, acceptMultimedia);


		String uploadFormId = FileUtil.getRandomId();

		req.setAttribute("uploadFormId", uploadFormId);

		if (acceptType.equals("doc")) {
			req.setAttribute("accept", acceptDoc);

		} else if (acceptType.equals("image")) {
			req.setAttribute("accept", acceptImage);

		} else if (acceptType.equals("multimedia")) {
			req.setAttribute("accept", acceptMultimedia);

		} else {
			req.setAttribute("accept", acceptAll);
		}
	}


	/**
	 * 외부사용자 파일다운로드(홈페이지 , 기타).csrf 해제필요  ROLE_ANONYMOUS 권한, 외부사용자도 쿠기설정되느지 확인필요
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/file/download")
	public void apiFiledownload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		download( request,  response);
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



				if(fileInfoVo.getFileId().length()==32) {
					downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();
				}else {
					downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId()).toFile();
				}


				if(!filePstnSecd.equals(fileInfoVo.getFilePstnSecd())) {

					if(!downloadFile.exists()) {
						LOGGER.debug("다른서버에서 파일 조회....{}", downloadFile.toString());





						 // 1. JWT 가져오기
				        String authHeader = request.getHeader("Authorization");
				        String jwtToken = null;

				        // Authorization 헤더 확인
				        if (authHeader != null && authHeader.startsWith("Bearer ")) {
				            jwtToken = authHeader.substring(7);
				        }

				        if(jwtToken==null) {
				        	Cookie[] cookies = request.getCookies();
						    if (cookies != null) {
						        for (Cookie cookie : cookies) {
						            if ("JWT".equals(cookie.getName())) {
						                jwtToken = cookie.getValue();
						                break;
						            }
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
						remoteFileDownloader.downloadFileFromOtherWAS(fullRemoteUrl, jwtToken, downloadFile.getAbsolutePath(), optimalChunks);
					}

				}


			}

			if (downloadFile.isFile() && downloadFile.exists()) {



				////////////////////////
				//평가   && !fileInfoVo.getTaskSecd().equals("06")
				if(!filePstnSecd.equals(fileInfoVo.getFilePstnSecd()) && !StringUtils.defaultString(fileInfoVo.getTemp(), "").equals("Y")) {

					File fileToMove = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();

					LOGGER.debug("기존서버파일 활용....{}", fileToMove.toString());

					if(!fileToMove.exists()) {

						LOGGER.debug("다른서버에 파일저장....{}", fileToMove.toString());

						FileUtils.forceMkdir(fileToMove.getParentFile());
						Files.move(downloadFile.toPath(), fileToMove.toPath(), StandardCopyOption.REPLACE_EXISTING);


						downloadFile=fileToMove;
					}

				}

				///////////////////

				response.setContentType("application/octet-stream; charset=utf-8");

				/*
				if (downloadFile.length() > 1024 * 1024 * 20) { // 20M
					response.setHeader("Content-Transfer-Encoding", "chunked");
				} else {
					response.setContentLength((int) downloadFile.length());
					response.setHeader("Content-Transfer-Encoding", "binary");
				}
				*/
				long fileLength= downloadFile.length();
				//response.setContentLength((int) downloadFile.length());
				response.setContentLengthLong( downloadFile.length());


				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + java.net.URLEncoder.encode(fileInfoVo.getFileNm(), "utf-8") + "\";");

				try ( FileInputStream fis = new FileInputStream(downloadFile);) {
					// FileCopyUtils.copy(fis, out);
					OutputStream out = response.getOutputStream();
					CommonUtil.copy(fis, out, fileLength);
				} catch (Exception e) {
					//e.printStackTrace();
					//throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
					if (CommonUtil.isClientAbortException(e)) {
				        // 사용자 다운로드 중단, 조용히 로그만 남기고 끝내도 됨
				        System.out.println("[INFO] 사용자 다운로드 중단 감지");
				    } else {
				        e.printStackTrace();
				        throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
				    }

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
	 * 썸네일을 다운로드한다
	 *
	 */
	@RequestMapping(value = "/file/thumbnail/{fileId}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public void thumbnailDownload( @PathVariable String fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {

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


		FtMap params = getFtMap(request);
        params.put("fileId", fileId);
        FileInfoVo fileInfoVo = fileUploadService.getFileInfo(params);


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
						downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + "."+fileInfoVo.getFileExt()).toFile();
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

				if(fileInfoVo.getFileExt().endsWith("jpg") || fileInfoVo.getFileExt().endsWith("jpeg")) {
					response.setContentType("image/jpeg");
				}else if( fileInfoVo.getFileExt().endsWith("png")) {
					response.setContentType("image/png");
				}else if( fileInfoVo.getFileExt().endsWith("gif")) {
					response.setContentType("image/gif");
				}else {
					 response.setContentType("application/octet-stream; charset=utf-8");
				}

				//response.setContentLength((int) fileInfoVo.getFileSize());
				//response.setHeader("Content-Transfer-Encoding", "binary");
				response.setContentLengthLong(downloadFile.length());


				try ( FileInputStream fis = new FileInputStream(downloadFile);) {
					// FileCopyUtils.copy(fis, out);
					OutputStream out = response.getOutputStream();
					CommonUtil.copy(fis, out, fileInfoVo.getFileSize());
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
	               Files.deleteIfExists(downloadFile.toPath());
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

	        	if( fileInfoVo.getFilePath().endsWith("thumbnail")) {
	        		downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + "."+fileInfoVo.getFileExt()).toFile();
	        	}else {
	        		downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();
	        	}

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

	       // response.setContentLength((int) contentLength);

	        response.setContentLengthLong(contentLength);

	        // 파일 전송
	        try (FileInputStream fis = new FileInputStream(downloadFile);) {
	        	OutputStream out = response.getOutputStream();
	            // 시작 위치로 이동
	            //fis.skip(start);
	        	if (start > 0) {
	        	    byte[] skipBuffer = new byte[8192];
	        	    long remaining = start;
	        	    while (remaining > 0) {
	        	        int toRead = (int) Math.min(skipBuffer.length, remaining);
	        	        int bytesRead = fis.read(skipBuffer, 0, toRead);
	        	        if (bytesRead == -1) {
	        	            throw new IOException("EOF reached before start position");
	        	        }
	        	        remaining -= bytesRead;
	        	    }
	        	}

	            byte[] buffer = new byte[8192];
	            long remaining = contentLength;
	            int bytesRead;

	            /*
	            while (remaining > 0 && (bytesRead = fis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
	                out.write(buffer, 0, bytesRead);
	                remaining -= bytesRead;
	            }*/
	            while (remaining > 0 && (bytesRead = fis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
	                try {
	                    out.write(buffer, 0, bytesRead);
	                    remaining -= bytesRead;

	                    // 주기적으로 flush (선택사항)
	                    if (remaining % (8192 * 10) == 0) {
	                        out.flush();
	                    }
	                } catch (IOException e) {
	                    // 클라이언트 연결 중단 등
	                    LOGGER.warn("Client disconnected during download");
	                    break;
	                }
	            }

	            out.flush();

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

				/*
				if (downloadFile.length() > 1024 * 1024 * 20) { // 20M
					response.setHeader("Content-Transfer-Encoding", "chunked");
				} else {
					response.setContentLength((int) downloadFile.length());
					response.setHeader("Content-Transfer-Encoding", "binary");
				}
				*/
				//response.setContentLength((int)downloadFile.length());

				response.setContentLengthLong(downloadFile.length());

				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + java.net.URLEncoder.encode(fileInfoVo.getFileNm(), "utf-8") + "\";");

				try ( FileInputStream fis = new FileInputStream(downloadFile);) {
					// FileCopyUtils.copy(fis, out);
					OutputStream out = response.getOutputStream();
					CommonUtil.copy(fis, out, downloadFile.length());
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
	              Files.deleteIfExists(downloadFile.toPath());
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

	        response.setContentLengthLong(contentLength);

	        // 파일 전송
	        try ( FileInputStream fis = new FileInputStream(downloadFile);) {

	        	OutputStream out = response.getOutputStream();
	            // 시작 위치로 이동
	            //fis.skip(start);

	        	// 개선된 코드 - 더 안전한 방법
	        	if (start > 0) {
	        	    byte[] skipBuffer = new byte[8192];
	        	    long remaining = start;
	        	    while (remaining > 0) {
	        	        int toRead = (int) Math.min(skipBuffer.length, remaining);
	        	        int bytesRead = fis.read(skipBuffer, 0, toRead);
	        	        if (bytesRead == -1) {
	        	            throw new IOException("EOF reached before start position");
	        	        }
	        	        remaining -= bytesRead;
	        	    }
	        	}



	            byte[] buffer = new byte[8192];
	            long remaining = contentLength;
	            int bytesRead;

	            /*
	            while (remaining > 0 && (bytesRead = fis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
	                out.write(buffer, 0, bytesRead);
	                remaining -= bytesRead;
	            }*/

	            while (remaining > 0 && (bytesRead = fis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
	                try {
	                    out.write(buffer, 0, bytesRead);
	                    remaining -= bytesRead;

	                    // 주기적으로 flush (선택사항)
	                    if (remaining % (8192 * 10) == 0) {
	                        out.flush();
	                    }
	                } catch (IOException e) {
	                    // 클라이언트 연결 중단 등
	                    LOGGER.warn("Client disconnected during download");
	                    break;
	                }
	            }


	            out.flush();
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


	/**
	 * 내외부망 파일을 다운로드한다
	 * @param request
	 * @param res
	 * @throws Exception
	 */
	@RequestMapping(value = "/file/download/zip")
	public void downloadZip(HttpServletRequest request, HttpServletResponse res) throws Exception {

	    String[] inRemoteIps = null;
	    String[] outRemoteIps = null;

	    // 내부망/외부망 IP 설정
	    if(inOutDiv.equals("in")){
	        outRemoteIps = outIp;
	        inRemoteIps = inIp;
	    } else if(inOutDiv.equals("out")){
	        outRemoteIps = inIp;
	        inRemoteIps = outIp;
	    }

	    FtMap params = getFtMap(request);
	    String downloadFileInfo = params.getString("downloadFileInfo");
	    Gson gson = new Gson();
	    FileInfoVo[] fileInfoVos = gson.fromJson(downloadFileInfo, FileInfoVo[].class);

	    // JWT 토큰 추출
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

	    // 임시 다운로드 디렉토리 생성
	    String tempDownloadDir = tempUploadPath + File.separator + "temp_" + System.currentTimeMillis();
	    File tempDir = new File(tempDownloadDir);
	    if (!tempDir.exists()) {
	        tempDir.mkdirs();
	    }

	    List<FileInfoVo> processedFileInfos = new ArrayList<>();

	    try {
	        // 각 파일에 대해 처리 - 모든 파일을 임시 폴더에 다운로드
	        for (FileInfoVo fileInfoVo : fileInfoVos) {
	            File downloadFile = null;

	            try {
	                if (StringUtils.defaultString(fileInfoVo.getTemp(), "").equals("Y")) {
	                    // 임시 파일인 경우 - 임시 폴더로 복사
	                    File originalTempFile = Paths.get(tempUploadPath, fileInfoVo.getFileId() + ".TEMP").toFile();
	                    if (originalTempFile.exists()) {
	                        downloadFile = Paths.get(tempDownloadDir, fileInfoVo.getFileId() + ".FILE").toFile();
	                        Files.copy(originalTempFile.toPath(), downloadFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	                    }

	                } else {
	                    // 일반 파일인 경우 - DB에서 파일 정보 조회
	                    FtMap fileParams = new FtMap();
	                    fileParams.put("fileId", fileInfoVo.getFileId());
	                    fileInfoVo = fileUploadService.getFileInfo(fileParams);

	                    // 파일 다운로드 권한 체크
	                    boolean fileDownloadCheck = FileUtil.hasFileDownloadAuth(fileInfoVo);
	                    if (!fileDownloadCheck) {
	                        LOGGER.warn("File access denied for fileId: {}", fileInfoVo.getFileId());
	                        continue; // 권한 없는 파일은 스킵
	                    }

	                    String currentFilePstnSecd = "";
	                    if(inOutDiv.equals("in")){
	                        currentFilePstnSecd = "01";
	                    } else if(inOutDiv.equals("out")){
	                        currentFilePstnSecd = "02";
	                    }

	                    if(currentFilePstnSecd.equals(fileInfoVo.getFilePstnSecd())) {
	                        // 같은 망에 있는 파일 - 임시 폴더로 복사
	                        File originalFile = null;
	                        if(fileInfoVo.getFileId().length() == 32) {
	                            originalFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();
	                        } else {
	                            originalFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId()).toFile();
	                        }

	                        if (originalFile.exists()) {
	                            downloadFile = Paths.get(tempDownloadDir, fileInfoVo.getFileId() + ".FILE").toFile();
	                            Files.copy(originalFile.toPath(), downloadFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	                        }

	                    } else {
	                        // 다른 망에 있는 파일 - 원격에서 임시 폴더로 다운로드
	                        String[] remoteIps = null;

	                        if(fileInfoVo.getFilePstnSecd().equals("01")) {
	                            remoteIps = inRemoteIps;
	                        } else if(fileInfoVo.getFilePstnSecd().equals("02")) {
	                            remoteIps = outRemoteIps;
	                        }

	                        if(remoteIps != null && remoteIps.length > 0) {
	                            String selectedIp = remoteIps[ThreadLocalRandom.current().nextInt(remoteIps.length)];
	                            LOGGER.debug("selectedIp for remote download: {}", selectedIp);

	                            // 최적의 청크 수 계산
	                            int optimalChunks = calculateOptimalChunks(fileInfoVo.getFileSize());

	                            // 임시 폴더에 파일 다운로드
	                            downloadFile = Paths.get(tempDownloadDir, fileInfoVo.getFileId() + ".FILE").toFile();

	                            String fullRemoteUrl = "http://" + selectedIp + "/file/remoteDownload/" + fileInfoVo.getFileId();

	                            // 원격에서 파일 다운로드
	                            remoteFileDownloader.downloadFileFromOtherWAS(fullRemoteUrl, jwtToken, downloadFile.getAbsolutePath(), optimalChunks);
	                        }
	                    }
	                }

	                // 파일이 정상적으로 다운로드되었는지 확인
	                if (downloadFile != null && downloadFile.isFile() && downloadFile.exists()) {
	                    // ZIP에 포함될 파일 정보 생성 (임시 폴더 기준)
	                    FileInfoVo processedFileInfo = new FileInfoVo();
	                    processedFileInfo.setFileId(fileInfoVo.getFileId());
	                    processedFileInfo.setFileNm(fileInfoVo.getFileNm());
	                    processedFileInfo.setFilePath(""); // 임시 폴더이므로 경로는 빈값
	                    processedFileInfo.setFileSize(downloadFile.length());
	                    processedFileInfos.add(processedFileInfo);

	                } else {
	                    LOGGER.warn("File not found or download failed for fileId: {}", fileInfoVo.getFileId());
	                }

	            } catch (Exception e) {
	                LOGGER.error("Error processing file: " + fileInfoVo.getFileId(), e);
	                // 개별 파일 오류는 로그만 남기고 계속 진행
	                continue;
	            }
	        }

	        // 처리된 파일이 없으면 예외 발생
	        if (processedFileInfos.isEmpty()) {
	            throw new FileDownloadException("다운로드 가능한 파일이 없습니다.");
	        }


	        // ZIP 파일 생성 - 임시 폴더의 파일들로 생성
	        FileInfoVo[] processedFileArray = processedFileInfos.toArray(new FileInfoVo[0]);
	        String zipFileName = FileUtil.getRandomId() + ".zip";
	       // String zipFilePath = tempZipPath + File.separator + zipFileName;
	        String zipFilePath = tempZipPath;
	       // fileUploadService.createZipFromTempFolder(processedFileArray, zipFilePath, tempDownloadDir);

	        fileUploadService.streamZipFromTempFolder(processedFileArray, tempDownloadDir, res);
/*
	        // ZIP 파일 다운로드
	        File zipFile = Paths.get(tempZipPath, zipFileName).toFile();
	        if (zipFile.isFile() && zipFile.exists()) {
	            res.setContentType("application/octet-stream; charset=utf-8");


	            res.setContentLengthLong(zipFile.length());

	            res.setHeader("Content-Disposition",
	                    "attachment; filename=\"" + java.net.URLEncoder.encode(zipFileName, "utf-8") + "\";");

	            try (FileInputStream fis = new FileInputStream(zipFile)) {
	            	OutputStream out = res.getOutputStream();
	                CommonUtil.copy(fis, out,zipFile.length());
	            } catch (IOException e) {
	                e.printStackTrace();
	                throw new FileDownloadException("ZIP 파일 다운로드 중 오류가 발생했습니다.");
	            } finally {
	                // ZIP 파일 정리
	                if (zipFile.exists()) {
	                    zipFile.delete();
	                }
	            }
	        } else {
	            throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
	        }
*/
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new FileDownloadException(e.getMessage());

	    } finally {
	        // 임시 디렉토리 전체 정리
	        try {
	            if (tempDir.exists()) {
	                // 디렉토리 내 모든 파일 삭제 후 디렉토리 삭제
	                File[] files = tempDir.listFiles();
	                if (files != null) {
	                    for (File file : files) {
	                        file.delete();
	                    }
	                }
	                tempDir.delete();
	            }
	        } catch (Exception ignored) {}
	    }
	}


	@RequestMapping(value = "/file/isExistFile")
	@ResponseBody
	public Map<String, Object> isExistFile(HttpServletRequest request, FileInfoVo fileInfoVo) throws Exception {


		FtMap params = getFtMap(request);

		boolean isExistFile=false;

		if (StringUtils.defaultString(params.getString("temp"), "").equals("Y")) {
			isExistFile=true;
		} else {
			params.put("fileId", fileInfoVo.getFileId());
			fileInfoVo = fileUploadService.getFileInfo(params);


			if(!fileInfoVo.getDelYn().equals("Y")) {
				isExistFile=true;
			}

		}

		Map<String, Object> map = new HashMap<String, Object>();

		if (isExistFile) {
			map.put("msg", "SUCCESS");
		} else {
			map.put("msg", ErrorCode.FILE_NOT_FOUND.getMessage());
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


	 @RequestMapping(value = "/file/hwpCtrlPopup")
	 public String  hwpCtrlPopup(HttpServletRequest request) throws Exception {


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

			String fileId = params.getString("fileId");
			String temp = params.getString("temp");
			String hwpPopupUrl = params.getString("hwpPopupUrl");


			params.put("fileId", fileId);

			File downloadFile = null;
			String base64Str = "";
			String fullRemoteUrl="";

			try {
				if (StringUtils.defaultString(temp, "").equals("Y")) {

					downloadFile = Paths.get(tempUploadPath, fileId + ".TEMP").toFile();

				} else {

					FileInfoVo fileInfoVo = fileUploadService.getFileInfo(params);

					// 파일다운로드 권한
					boolean fileDownloadCheck = FileUtil.hasFileDownloadAuth(fileInfoVo);
					if (!fileDownloadCheck) {
						throw new FileDownloadException(ErrorCode.FILE_ACCESS_DENIED.getMessage());
					}


					if(filePstnSecd.equals(fileInfoVo.getFilePstnSecd())) {

						if(fileInfoVo.getFileId().length()==32) {
							downloadFile = Paths.get(realUploadPath, StringUtils.defaultString(fileInfoVo.getFilePath()), fileInfoVo.getFileId() + ".FILE").toFile();
						}else {
							downloadFile = Paths.get(realUploadPath, StringUtils.defaultString(fileInfoVo.getFilePath()), fileInfoVo.getFileId()).toFile();
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

						int optimalChunks = calculateOptimalChunks(fileInfoVo.getFileSize());

						String tempFileName = fileInfoVo.getFileId() + "_" + System.currentTimeMillis() + ".tmp";
						downloadFile = Paths.get(tempUploadPath, tempFileName).toFile();

						fullRemoteUrl = "http://"+selectedIp+"/file/remoteDownload/" + fileInfoVo.getFileId();

						remoteFileDownloader.downloadFileFromOtherWAS(fullRemoteUrl, jwtToken, downloadFile.getAbsolutePath(),
								optimalChunks);

					}

				}

				if (downloadFile.isFile() && downloadFile.exists()) {

					byte[] fileByte = FileUtil.getFileBinary(downloadFile);
					base64Str = Base64.getEncoder().encodeToString(fileByte);

				} else {
					throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
				}
			} catch (Exception e) {
				LOGGER.error(e.toString());
				throw new FileDownloadException(e.getMessage());
			}


			request.setAttribute("hwpFileId", fileId);
			request.setAttribute("hwpFileNm", params.getString("fileNm"));
			request.setAttribute("hwpTemp", temp);

			request.setAttribute("base64Str", base64Str);
			return hwpPopupUrl;


		}



	   /**
		 * 한글파일을 저장한다
		 *
		 * @param request
		 * @param response
		 * @return 성공실패여부
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping(value = "/file/saveHwp")
		public String saveHwp(@RequestBody Map<String, Object> map, HttpServletRequest request) throws Exception {


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


			FtMap params = getFtMap(map);


			LOGGER.debug("params......{}",params);


			File downloadFile = null;

			String base64Str = params.getString("base64Data");
			byte[] base64DecodeFileData = Base64.getDecoder().decode(base64Str);

			String result = "";

			String fullRemoteUrl="";

			FileInfoVo fileInfoVo =null;

			try {
				if (StringUtils.defaultString(params.getString("temp"), "").equals("Y")) {
					downloadFile = Paths.get(tempUploadPath, params.getString("fileId") + ".TEMP").toFile();
				} else {
					//FileInfoVo fileInfoVo = fileUploadService.getFileInfo(params);
					//downloadFile = Paths.get(realUploadPath, StringUtils.defaultString(fileInfoVo.getFilePath()), fileInfoVo.getFileId() + ".FILE").toFile();

					fileInfoVo = fileUploadService.getFileInfo(params);

					// 파일다운로드 권한
					boolean fileDownloadCheck = FileUtil.hasFileDownloadAuth(fileInfoVo);
					if (!fileDownloadCheck) {
						throw new FileDownloadException(ErrorCode.FILE_ACCESS_DENIED.getMessage());
					}

					if(filePstnSecd.equals(fileInfoVo.getFilePstnSecd())) {

						if(fileInfoVo.getFileId().length()==32) {
							downloadFile = Paths.get(realUploadPath, StringUtils.defaultString(fileInfoVo.getFilePath()), fileInfoVo.getFileId() + ".FILE").toFile();
						}else {
							downloadFile = Paths.get(realUploadPath, StringUtils.defaultString(fileInfoVo.getFilePath()), fileInfoVo.getFileId()).toFile();
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

						remoteFileDownloader.downloadFileFromOtherWAS(fullRemoteUrl, jwtToken, downloadFile.getAbsolutePath(),
								optimalChunks);

					}
				}


				if (downloadFile.isFile() && downloadFile.exists()) {

					if(filePstnSecd.equals(fileInfoVo.getFilePstnSecd())) {

						FileOutputStream fos = new FileOutputStream(downloadFile);
						FileCopyUtils.copy(base64DecodeFileData, fos);

						params.put("filePstnSecd", filePstnSecd);
						params.put("fileSz", downloadFile.length());

						fileUploadService.updateNewFileInfo(params);

					}

					//파일 원본과 파일호출이 내부망 외부망 다를경우
					if (!StringUtils.defaultString(params.getString("temp"), "").equals("Y")) {

						if(!filePstnSecd.equals(fileInfoVo.getFilePstnSecd())) {

							FileOutputStream fos = new FileOutputStream(downloadFile);
							FileCopyUtils.copy(base64DecodeFileData, fos);

							params.put("filePstnSecd", filePstnSecd);
							params.put("fileSz", downloadFile.length());

							File fileToMove = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();

							FileUtils.forceMkdir(fileToMove.getParentFile());
							Files.move(downloadFile.toPath(), fileToMove.toPath(), StandardCopyOption.REPLACE_EXISTING);

							fileUploadService.updateNewFileInfo(params);

						}
					}

					result = "OK";

				} else {
					result = "FAIL";
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.toString());
				throw new FileDownloadException(e.getMessage());

			}finally {
		        // 원격에서 받은 임시 파일 정리
		        if (StringUtils.isNotEmpty(fullRemoteUrl) && downloadFile != null) {
		            try {
		              Files.deleteIfExists(downloadFile.toPath());
		            } catch (Exception ignored) {}
		        }
		    }

			return result;

		}
}


