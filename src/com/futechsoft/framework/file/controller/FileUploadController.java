package com.futechsoft.framework.file.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.futechsoft.framework.exception.BizException;
import com.futechsoft.framework.exception.ErrorCode;
import com.futechsoft.framework.exception.FileDownloadException;
import com.futechsoft.framework.exception.FileUploadException;
import com.futechsoft.framework.file.service.FileUploadService;
import com.futechsoft.framework.file.vo.FileInfoVo;
import com.futechsoft.framework.security.auth.JwtTokenProvider;
import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.FileUtil;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SynnapConvertToHtml;
import com.google.gson.Gson;

import kr.go.odakorea.gis.service.KoicaScrapingService;

@Controller
public class FileUploadController extends AbstractController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

	@Resource(name = "framework.file.service.FileUploadService")
	FileUploadService fileUploadService;

	@Resource(name = "gis.service.KoicaScrapingService")
	KoicaScrapingService koicaScrapingService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;


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



   @Value("${hwp.template.path}")
   private String hwpTemplatePath;

   @Value("${excel.template.path}")
   private String excelTemplatePath;


	@Autowired
	private RemoteFileDownloader remoteFileDownloader;



	@Value("${synap.resultPath}")
	private String resultPath;

	@Value("${synap.moduleBasePath}")
	private String moduleBasePath;

	@Value("${synap.convert_count}")
	private int convert_count;


	@Value("${synap.synapContextPath}")
	private String synapContextPath;


	@Value("${file.uploadPath.iati}")
	private String iatiUploadPath;


	@RequestMapping(value = "/file/upload")
	@ResponseBody
	public Map<String, Object> upload(Model model, HttpSession session,  @RequestParam MultipartFile file, @RequestParam String metadata)
			throws Exception {


		String acceptDoc=String.join(",", acceptDocs);
		String acceptImage=String.join(",", acceptImages);
		String acceptMultimedia=String.join(",", acceptMultimedias);



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


			if (CommonUtil.nvl(fileInfoVo.getExtractZipYn()).equals("Y") && fileInfoVo.getUploadComplete().equals("Y")) {

				File tempFile = new File(fileInfoVo.getTempFilePath());

				// 새 파일명: fileId.zip
				File zipFile = new File(tempFile.getParent(), fileInfoVo.getFileId() + ".zip");

				List<FileInfoVo> fileList = new ArrayList<>();

				if (tempFile.renameTo(zipFile)) {
					try {
						fileList = FileUtil.extractZip(zipFile, tempUploadPath);
						fileInfo.put("fileInfo", fileList);

						if(zipFile.exists()) {
							boolean check=zipFile.delete();
							LOGGER.debug("delete Zip file..{}",check);
						}
					} catch (BizException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();

						fileInfo.put("errorCode", ErrorCode.ZIP_EXT_ERROR.getCode());
						fileInfo.put("errorMessage", ErrorCode.ZIP_EXT_ERROR.getMessage());

					}
				}



			} else {

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



		String acceptDoc=String.join(",", acceptDocs);
		String acceptImage=String.join(",", acceptImages);
		String acceptMultimedia=String.join(",", acceptMultimedias);

		String acceptAll =String.join(",", acceptDoc,acceptImage,acceptMultimedia);

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

		req.setAttribute("token", jwtTokenProvider.createAnonymousToken("fileUser"));
	}


	/**
	 * 외부사용자 파일다운로드(홈페이지 , 기타).csrf 해제필요  ROLE_ANONYMOUS 권한
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/file/download/{fileId}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public void apiFiledownload( @PathVariable String fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {


		FtMap params =new FtMap();
		params.put("fileId", fileId);
		FileInfoVo fileInfoVo = fileUploadService.getFileInfo(params);

		if(fileInfoVo==null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"error\":\"권한없음\"}");
			return;
		}

		if(CommonUtil.nvl(fileInfoVo.getHmpgRlsYn()).equals("Y") || CommonUtil.nvl(fileInfoVo.getIipsRlsYn()).equals("Y")) {

			String homePageToken= jwtTokenProvider.createAnonymousToken("homePageUser");

			download( request,  response,  fileId, homePageToken, null);
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"error\":\"권한없음\"}");
			return;
		}


	}

	@RequestMapping(value = "/api/file/commonDownload/{fileId}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public void commonDownload( @PathVariable String fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {


		FtMap params =new FtMap();
		params.put("fileId", fileId);
		FileInfoVo fileInfoVo = fileUploadService.getFileInfo(params);

		if(fileInfoVo==null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"error\":\"권한없음\"}");
			return;
		}
		download( request,  response,  fileId, null, null);
	}


	@ResponseBody
	@RequestMapping("/file/getToken")
	public Map<String, String>  getToken() throws Exception{

		String token= jwtTokenProvider.createAnonymousToken("anonymousUser");

		Map<String, String> response = new HashMap<>();
	    response.put("token", token);

	    return response;
	}


	@RequestMapping(value = "/file/iatiDownload/{fileNm:.+}")
	public void iatiDownloadFile(@PathVariable String fileNm, HttpServletRequest request, HttpServletResponse response) throws Exception {


		File iatiFile =  Paths.get(iatiUploadPath,fileNm).toFile();

		if(iatiFile.exists()) {

			if (iatiFile.isFile() && iatiFile.exists()) {

				response.setContentType("application/octet-stream; charset=utf-8");
				response.setContentLengthLong(iatiFile.length());

				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + java.net.URLEncoder.encode(fileNm, "utf-8") + "\";");

				try ( FileInputStream fis = new FileInputStream(iatiFile);) {
					OutputStream out = response.getOutputStream();
					CommonUtil.copy(fis, out, iatiFile.length());
				} catch (BizException e) {
					throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
				}

			} else {
				throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
			}

		}else {
			String token= jwtTokenProvider.createAnonymousToken("homePageUser");
			iatiDownload( request,  response,  fileNm, token, null);
		}


	}

	private void iatiDownload(HttpServletRequest request, HttpServletResponse response, String fileNm, String token, String thumbnailYn) throws Exception {


		String[] remoteIps=null;

		if(inOutDiv.equals("in")){
			remoteIps= outIp;
		}else if(inOutDiv.equals("out")){
			remoteIps= inIp;
		}

		String selectedIp = remoteIps[ThreadLocalRandom.current().nextInt(remoteIps.length)];

		LOGGER.debug("selectedIp......{}",selectedIp);

		File downloadFile = null;

		String fullRemoteUrl="";

		try {

			downloadFile = Paths.get(iatiUploadPath,fileNm).toFile();

			if(!downloadFile.exists()) {
				LOGGER.debug("다른서버에서 파일 조회....{}", downloadFile.toString());

				String jwtToken = null;
				if (token != null) {
					jwtToken = token;
				}

				// 1. JWT 가져오기
				if(jwtToken==null) {
					String authHeader = request.getHeader("Authorization");

			        // Authorization 헤더 확인
			        if (authHeader != null && authHeader.startsWith("Bearer ")) {
			            jwtToken = authHeader.substring(7);
			        }
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

				// 임시 파일 경로 생성
				String tempFileName = fileNm + "_" + System.currentTimeMillis() + ".tmp";
				downloadFile = Paths.get(tempUploadPath, tempFileName).toFile();

				fullRemoteUrl = "http://"+selectedIp+"/file/iatiRemoteDownload/" + fileNm;

				// 여기서 사용!
				remoteFileDownloader.downloadFileFromOtherWAS(fullRemoteUrl, jwtToken, downloadFile.getAbsolutePath(), 0);
			}



			if (downloadFile.isFile() && downloadFile.exists()) {


				File fileToMove = Paths.get(iatiUploadPath, fileNm).toFile();

				LOGGER.debug("기존서버파일 활용....{}", fileToMove.toString());

				if(!fileToMove.exists()) {

					LOGGER.debug("다른서버에 파일저장....{}", fileToMove.toString());

					FileUtils.forceMkdir(fileToMove.getParentFile());
					Files.move(downloadFile.toPath(), fileToMove.toPath(), StandardCopyOption.REPLACE_EXISTING);


					downloadFile=fileToMove;
				}

				response.setContentType("application/octet-stream; charset=utf-8");

				long fileLength= downloadFile.length();
				response.setContentLengthLong( downloadFile.length());


				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + java.net.URLEncoder.encode(fileNm, "utf-8") + "\";");

				try ( FileInputStream fis = new FileInputStream(downloadFile);) {
					OutputStream out = response.getOutputStream();
					CommonUtil.copy(fis, out, fileLength);
				} catch (BizException e) {
					if (CommonUtil.isClientAbortException(e)) {
				        // 사용자 다운로드 중단, 조용히 로그만 남기고 끝내도 됨
				      //  System.out.println("[INFO] 사용자 다운로드 중단 감지");
				    } else {
				        //e.printStackTrace();
				        throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
				    }
				}
			} else {
				throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
			}

		} catch (BizException e) {
			//e.printStackTrace();
			throw new FileDownloadException(e.getMessage());

		}

	}




	@RequestMapping(value = "/file/download/{fileId}/{fileToken}")
	public void downloadFile(@PathVariable String fileId,@PathVariable String fileToken, HttpServletRequest request, HttpServletResponse response) throws Exception {

		FtMap params =new FtMap();
		params.put("fileId", fileId);
		FileInfoVo fileInfoVo = fileUploadService.getFileInfo(params);

		if(fileInfoVo==null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"error\":\"권한없음\"}");
			return;
		}

		download( request,  response,  fileId, fileToken, "N");
	}

	@RequestMapping(value = "/file/download")
	public void download(HttpServletRequest request, HttpServletResponse response, String fileId, String token, String thumbnailYn) throws Exception {


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


		FileInfoVo fileInfoVo = null;
		if(CommonUtil.nvl(fileId).equals("")) {
			String downloadFileInfo = params.getString("downloadFileInfo");
			Gson gson = new Gson();
			fileInfoVo = gson.fromJson(downloadFileInfo, FileInfoVo.class);
		}else {
			fileInfoVo= new FileInfoVo();
			fileInfoVo.setFileId(fileId);
		}



		File downloadFile = null;

		String fullRemoteUrl="";

		try {
			if (StringUtils.defaultString(fileInfoVo.getTemp(), "").equals("Y")) {
				downloadFile = Paths.get(tempUploadPath, fileInfoVo.getFileId() + ".TEMP").toFile();

			} else {

				params.put("fileId", fileInfoVo.getFileId());
				fileInfoVo = fileUploadService.getFileInfo(params);


				/*
				// 파일다운로드 권한
				boolean fileDownloadCheck = FileUtil.hasFileDownloadAuth(fileInfoVo);
				if (!fileDownloadCheck) {
					throw new FileDownloadException(ErrorCode.FILE_ACCESS_DENIED.getMessage());
				}
				 */

				LOGGER.debug("fileInfoVo.getFileId().length()......{}",fileInfoVo.getFileId().length());


				if(fileInfoVo.getFileId().length()==32) {

					if(CommonUtil.nvl(thumbnailYn).equals("Y")) {
						downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + "."+fileInfoVo.getFileExt()).toFile();
					}else {
						downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();
					}

				}else {
					String temFileId="";
					if(fileInfoVo!=null &&  fileInfoVo.getFileId().length() >4) {
						 temFileId=fileInfoVo.getFileId().substring(fileInfoVo.getFileId().length()-4);
					}

					LOGGER.debug("temFileId...................{}",temFileId);



					temFileId =  String.valueOf(Integer.parseInt(temFileId));

					LOGGER.debug("temFileId2...................{}",temFileId);


					downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), temFileId).toFile();

					LOGGER.debug("downloadFile...................{}",downloadFile);
					LOGGER.debug("downloadFile...................{}",downloadFile.toPath());
					LOGGER.debug("downloadFile...................{}",downloadFile.getName());
				}

				LOGGER.debug("downloadFile.exists()...................{}",downloadFile.exists());


				if(!filePstnSecd.equals(fileInfoVo.getFilePstnSecd())) {

					LOGGER.debug("not eq filePstnSecd");

					if(!downloadFile.exists()) {
						LOGGER.debug("다른서버에서 파일 조회....{}", downloadFile.toString());


						String jwtToken = null;

						if (token != null) {
							jwtToken = token;
						}


						// 1. JWT 가져오기
						if(jwtToken==null) {
							String authHeader = request.getHeader("Authorization");

					        // Authorization 헤더 확인
					        if (authHeader != null && authHeader.startsWith("Bearer ")) {
					            jwtToken = authHeader.substring(7);
					        }
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

				//response.setContentType("application/octet-stream; charset=utf-8");
				/*
				if(fileInfoVo.getFileNm().endsWith("jpg") || fileInfoVo.getFileNm().endsWith("jpeg")) {
					response.setContentType("image/jpeg");
				}else if( fileInfoVo.getFileNm().endsWith("png")) {
					response.setContentType("image/png");
				}else if( fileInfoVo.getFileNm().endsWith("gif")) {
					response.setContentType("image/gif");
				}else {
					 response.setContentType("application/octet-stream; charset=utf-8");
				}
				 */

				 response.setContentType("application/octet-stream; charset=utf-8");

				long fileLength= downloadFile.length();
				response.setContentLengthLong( downloadFile.length());


				LOGGER.debug("fileInfoVo.getFileNm()....{}", fileInfoVo.getFileNm());

				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + java.net.URLEncoder.encode(fileInfoVo.getFileNm(), "utf-8") + "\";");

				try ( FileInputStream fis = new FileInputStream(downloadFile);) {
					OutputStream out = response.getOutputStream();
					CommonUtil.copy(fis, out, fileLength);
				} catch (BizException e) {
					if (CommonUtil.isClientAbortException(e)) {
				        // 사용자 다운로드 중단, 조용히 로그만 남기고 끝내도 됨
				      //  System.out.println("[INFO] 사용자 다운로드 중단 감지");
				    } else {
				        //e.printStackTrace();
				        throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
				    }
				}
			} else {
				throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
			}

		} catch (BizException e) {
			//e.printStackTrace();
			throw new FileDownloadException(e.getMessage());

		}

	}



	/**
	 * 썸네일을 다운로드한다
	 *
	 */
	@RequestMapping(value = "/file/thumbnail/{fileId}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public void thumbnailDownload( @PathVariable String fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {

		download( request,  response,  fileId, null, "Y");
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


				String temFileId="";
				if(fileInfoVo!=null &&  fileInfoVo.getFileId().length() >4) {
					 temFileId=fileInfoVo.getFileId().substring(fileInfoVo.getFileId().length()-4);
				}
				temFileId =  String.valueOf(Integer.parseInt(temFileId));

				downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), temFileId).toFile();

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

	        } catch (BizException e) {
	            //e.printStackTrace();
	            throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
	        }

	    } catch (BizException e) {
	    	//e.printStackTrace();
	        throw new FileDownloadException(e.getMessage());
	    }
	}

	@RequestMapping(value = "/file/iatiRemoteDownload/{fileNm:.+}", method = {RequestMethod.GET, RequestMethod.HEAD})
	public void iatiRemoteDownload(
	    @PathVariable String fileNm,
	    HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	    LOGGER.info("Request Method: {}", request.getMethod());

	    File downloadFile = null;
	    try {

	    	downloadFile = Paths.get(iatiUploadPath, fileNm).toFile();

	        if (!downloadFile.exists() || !downloadFile.isFile()) {
	            throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
	        }

	        if (!downloadFile.exists() || !downloadFile.isFile()) {
	            throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
	        }

	        long fileLength = downloadFile.length();

	        // 헤더 설정
	        response.setContentType("application/octet-stream; charset=utf-8");
	        response.setContentLengthLong(fileLength);
	        response.setHeader("Content-Disposition",
	            "attachment; filename=\"" + java.net.URLEncoder.encode(fileNm, "utf-8") + "\";");

	        // 파일 전송
	        try (FileInputStream fis = new FileInputStream(downloadFile);
	             OutputStream out = response.getOutputStream()) {

	            byte[] buffer = new byte[8192];
	            int bytesRead;

	            while ((bytesRead = fis.read(buffer)) != -1) {
	                try {
	                    out.write(buffer, 0, bytesRead);

	                    // 주기적으로 flush
	                    if (bytesRead == buffer.length) {
	                        out.flush();
	                    }
	                } catch (IOException e) {
	                    LOGGER.warn("Client disconnected during download");
	                    break;
	                }
	            }

	            out.flush();

	        } catch (Exception e) {
	            LOGGER.error("File download error", e);
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
				} catch (BizException e) {
					//e.printStackTrace();
					throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
				}
			} else {
				throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
			}

		} catch (BizException e) {
			//e.printStackTrace();
			throw new FileDownloadException(e.getMessage());

		}finally {
	        // 원격에서 받은 임시 파일 정리
	        if (StringUtils.isNotEmpty(fullRemoteUrl) && downloadFile != null) {
	            try {
	              Files.deleteIfExists(downloadFile.toPath());
	            } catch (BizException ignored) {
	            	LOGGER.error(ignored.getMessage());
	            }
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
	        } catch (BizException e) {
	            //e.printStackTrace();
	            throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
	        }

	    } catch (BizException e) {
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

	                        	String temFileId="";
	            				if(fileInfoVo!=null &&  fileInfoVo.getFileId().length() >4) {
	            					 temFileId=fileInfoVo.getFileId().substring(fileInfoVo.getFileId().length()-4);
	            				}


	            				originalFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), temFileId).toFile();



	                        	//originalFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId()).toFile();
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

	            } catch (BizException e) {
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
	    } catch (BizException e) {
	        //e.printStackTrace();
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
	        } catch (BizException ignored) {

	        	LOGGER.error(ignored.getMessage());
	        }
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

							String temFileId="";
            				if(fileInfoVo!=null &&  fileInfoVo.getFileId().length() >4) {
            					 temFileId=fileInfoVo.getFileId().substring(fileInfoVo.getFileId().length()-4);
            				}

            				temFileId =  String.valueOf(Integer.parseInt(temFileId));
            				downloadFile = Paths.get(realUploadPath, StringUtils.defaultString(fileInfoVo.getFilePath()), temFileId).toFile();

							//downloadFile = Paths.get(realUploadPath, StringUtils.defaultString(fileInfoVo.getFilePath()), fileInfoVo.getFileId()).toFile();
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
			} catch (BizException e) {
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

							String temFileId="";
            				if(fileInfoVo!=null &&  fileInfoVo.getFileId().length() >4) {
            					 temFileId=fileInfoVo.getFileId().substring(fileInfoVo.getFileId().length()-4);
            				}

            				temFileId =  String.valueOf(Integer.parseInt(temFileId));
            				downloadFile = Paths.get(realUploadPath, StringUtils.defaultString(fileInfoVo.getFilePath()), temFileId).toFile();


							//downloadFile = Paths.get(realUploadPath, StringUtils.defaultString(fileInfoVo.getFilePath()), fileInfoVo.getFileId()).toFile();
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
			} catch (BizException e) {
				//e.printStackTrace();
				LOGGER.error(e.toString());
				throw new FileDownloadException(e.getMessage());

			}finally {
		        // 원격에서 받은 임시 파일 정리
		        if (StringUtils.isNotEmpty(fullRemoteUrl) && downloadFile != null) {
		            try {
		              Files.deleteIfExists(downloadFile.toPath());
		            } catch (BizException ignored) {

		            	LOGGER.error(ignored.getMessage());
		            }
		        }
		    }

			return result;

		}

		/***
		 * 한글 파일 양식 다운로드
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@RequestMapping(value = "/file/downloadTemplate/fileForm")
		public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {

			FtMap params = getFtMap(request);
			String fileName=params.getString("fileName");
			String contentType=params.getString("contentType");
			String type=params.getString("type");

			String path = hwpTemplatePath;
			if("hwp".equals(type)) {
				path = hwpTemplatePath;
			} else if("excel".equals(type)) {
				path = excelTemplatePath;
			}
			File downloadFile = Paths.get(path, fileName).toFile();
			//System.out.println("downloadFile::: "+ downloadFile);
			response.setContentType(contentType);
			response.setContentLengthLong(downloadFile.length());

			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + java.net.URLEncoder.encode(fileName, "utf-8") + "\";");

			try ( FileInputStream fis = new FileInputStream(downloadFile);) {
				OutputStream out = response.getOutputStream();
				CommonUtil.copy(fis, out, downloadFile.length());
			} catch (BizException e) {
				//e.printStackTrace();
				throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
			}

		}




		 // 업로드 한 파일로 변환 시작
	    @RequestMapping(value = "/file/synap/conv/{fileId}/{fileToken}", method = RequestMethod.GET)
	    public String requestPdfConvert(@PathVariable String fileId,@PathVariable String fileToken, HttpServletRequest request, HttpServletResponse response) throws Exception {

	    	FtMap params =new FtMap();
			params.put("fileId", fileId);
			//FileInfoVo fileInfoVo = fileUploadService.getFileInfo(params);

			File file = synapDownload( request,  response,  fileId, fileToken, "N");


	    	String fileName = file.getName();


	    	String inputFile=tempUploadPath+"/"+file.getName();


	        File xmlFile = new File(resultPath + fileName + ".xml");
	        if (xmlFile.isFile() == false) {
	            // 초기변환 시작
	        	SynnapConvertToHtml cvt = new SynnapConvertToHtml(moduleBasePath);
	            int cvt_ret = cvt.convertToHtmlPartial(inputFile, resultPath, fileName, 1,
	                convert_count);
	            if (cvt_ret != 0) {
	                // 오류 처리
	            }
	        }

	        // 스킨으로 리다이렉트(옵션)
	       // final String contextPath = "/result/"; // 스킨에서 변환 결과에 접근할 수 있는 경로
	        fileName = URLEncoder.encode(fileName, "UTF-8");
	        // 공백 문자 관련 처리
	        fileName = fileName.replaceAll("\\+", "%20");
	        final String retString = String.format("redirect:/skin/doc.html?fn=%s&rs=%s",
	            fileName, synapContextPath); // 스킨이 설정되어 있는 경로와 파라미터를 전송
	        return retString;

	    }
/*
	    // 스킨의 requestContext와 형식이 같아야 한다.
	    // XML URL은 다음과 같은 모양이 된다. ex) /example/result/convert_file_id.xml
	    @RequestMapping(value = "/file/synap/result/{fileName:.+}")
	    @ResponseBody
	    public ResponseEntity<FileSystemResource> getMetaFile(@PathVariable("fileName")
	        String fileName) throws IOException {


	        Path target = Paths.get(resultPath, fileName);

	        // 파일이 있으면 그대로 반환

	        HttpHeaders headers= new HttpHeaders();
	        headers.setContentType(MediaType.parseMediaType("application/xhtml+xml"));

	        if (Files.exists(target)) {
	            return ResponseEntity.ok().headers(headers).body(new FileSystemResource(target.toFile()));
	        }
	        throw new FileNotFoundException(fileName + " not found");
	    }

	    // 스킨의 requestContext와 형식이 같아야 한다.
	    // 이미지 URL은 다음과 같은 모양이 된다. ex) /example/result/convert_file_id.files/34.png
	    @RequestMapping(value = "/file/synap/result/{idPath:.+}/{fileName:.+}")
	    @ResponseBody
	    public ResponseEntity<FileSystemResource> getResultFile(@PathVariable("idPath") String idPath,
	        @PathVariable("fileName") String fileName) throws IOException {
	        String id = idPath;
	        if (idPath.contains(".")) {
	            id = idPath.substring(0, idPath.lastIndexOf("."));
	        }


	        Path target = Paths.get(resultPath, idPath, fileName);


	        // 파일이 있으면 그대로 반환
	        if (Files.exists(target)) {
	            return ResponseEntity.ok().body(new FileSystemResource(target.toFile()));
	        }

	        SynnapConvertToHtml cvt = new SynnapConvertToHtml(moduleBasePath);

	        // 부분 변환 시작 번호 구하기 1, 11, 21 ...
	        // 파일명에서 pageNum 분리( 23.png => 23 )
	        int pageNum;
	        if (fileName.contains(".")) {
	            pageNum = Integer.parseInt(fileName.split("\\.")[0]);
	        } else {
	            throw new FileNotFoundException(fileName + " not found");
	        }
	        pageNum = (pageNum / CONVERT_COUNT) * CONVERT_COUNT + 1; // pageNum을 시작 번호로 변경

	        String syncKey = id + pageNum;
	        synchronized (syncKey) {
	            // 파일이 있으면 그대로 반환 - sync 블럭 진입 시
	            // 이미 다른 스레드에서 변환된 파일이 있는지 확인
	            if (Files.exists(target)) {
	                return ResponseEntity.ok().body(new FileSystemResource(target.toFile()));
	            }

	            // 추가 변환
	            int outputValue = cvt.convertToHtmlPartial(tempUploadPath + id, resultPath, id,
	                pageNum, CONVERT_COUNT);
	            if (outputValue != 0) {
	                // 변환 에러. 에러 처리 코드를 추가합니다.
	                throw new FileNotFoundException(fileName + " not found");
	            }
	        }

	        HttpHeaders headers= new HttpHeaders();
	        headers.setContentType(MediaType.parseMediaType("application/xhtml+xml"));

	        return ResponseEntity.ok().headers(headers).body(new FileSystemResource(target.toFile()));
	    }
*/

		public File synapDownload(HttpServletRequest request, HttpServletResponse response, String fileId, String token, String thumbnailYn) throws Exception {


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


			FileInfoVo fileInfoVo = null;
			if(CommonUtil.nvl(fileId).equals("")) {
				String downloadFileInfo = params.getString("downloadFileInfo");
				Gson gson = new Gson();
				fileInfoVo = gson.fromJson(downloadFileInfo, FileInfoVo.class);
			}else {
				fileInfoVo= new FileInfoVo();
				fileInfoVo.setFileId(fileId);
			}



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

					LOGGER.debug("fileInfoVo.getFileId().length()......{}",fileInfoVo.getFileId().length());



					if(fileInfoVo.getFileId().length()==32) {

						if(CommonUtil.nvl(thumbnailYn).equals("Y")) {
							downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + "."+fileInfoVo.getFileExt()).toFile();
						}else {
							downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();
						}

					}else {


						String temFileId="";
        				if(fileInfoVo!=null &&  fileInfoVo.getFileId().length() >4) {
        					 temFileId=fileInfoVo.getFileId().substring(fileInfoVo.getFileId().length()-4);
        				}
        				temFileId =  String.valueOf(Integer.parseInt(temFileId));

        				downloadFile = Paths.get(realUploadPath, StringUtils.defaultString(fileInfoVo.getFilePath()), temFileId).toFile();


						//downloadFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId()).toFile();
					}

					LOGGER.debug("!filePstnSecd.equals(fileInfoVo.getFilePstnSecd())......{}",	!filePstnSecd.equals(fileInfoVo.getFilePstnSecd()));



					if(!filePstnSecd.equals(fileInfoVo.getFilePstnSecd())) {


						if(!downloadFile.exists()) {
							LOGGER.debug("다른서버에서 파일 조회....{}", downloadFile.toString());


							String jwtToken = null;

							if (token != null) {
								jwtToken = token;
							}


							// 1. JWT 가져오기
							String authHeader = request.getHeader("Authorization");

					        // Authorization 헤더 확인
					        if (authHeader != null && authHeader.startsWith("Bearer ")) {
					            jwtToken = authHeader.substring(7);
					        }

					        LOGGER.debug("jwtToken....{}", jwtToken);

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
						}else {
							//return downloadFile;
							File synapFileToMove = Paths.get(tempUploadPath, fileInfoVo.getFileId() + "."+fileInfoVo.getFileExt()).toFile();

							Files.copy(downloadFile.toPath(), synapFileToMove.toPath(), StandardCopyOption.REPLACE_EXISTING);

							return synapFileToMove;
						}

					}


				}

				if (downloadFile.isFile() && downloadFile.exists()) {

					File fileToMove = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();

					//평가   && !fileInfoVo.getTaskSecd().equals("06")
					if(!filePstnSecd.equals(fileInfoVo.getFilePstnSecd()) && !StringUtils.defaultString(fileInfoVo.getTemp(), "").equals("Y")) {


						LOGGER.debug("기존서버파일 활용....{}", fileToMove.toString());

						if(!fileToMove.exists()) {

							LOGGER.debug("다른서버에 파일저장....{}", fileToMove.toString());

							FileUtils.forceMkdir(fileToMove.getParentFile());
							Files.move(downloadFile.toPath(), fileToMove.toPath(), StandardCopyOption.REPLACE_EXISTING);


							downloadFile=fileToMove;
						}

					}




				} else {
					throw new FileDownloadException(ErrorCode.FILE_NOT_FOUND.getMessage());
				}


				File synapFileToMove = Paths.get(tempUploadPath, fileInfoVo.getFileId() + "."+fileInfoVo.getFileExt()).toFile();

				Files.copy(downloadFile.toPath(), synapFileToMove.toPath(), StandardCopyOption.REPLACE_EXISTING);

				return synapFileToMove;

			} catch (BizException e) {
				//e.printStackTrace();
				throw new FileDownloadException(e.getMessage());

			}

		}


		@GetMapping("/file/check-file")
	    public ResponseEntity<String> checkFile() {
	        try {
	            File htmlFile = new File("/GCLOUD/WebApp/deploy/odawas.war/result/10d42498b150491b897bc7d98012bc4d.hwp.view.html");
	            File xhtmlFile = new File("/GCLOUD/WebApp/deploy/odawas.war/result/10d42498b150491b897bc7d98012bc4d.hwp.view.xhtml");

	            String result = "HTML exists: " + htmlFile.exists() + ", readable: " + htmlFile.canRead() + "\n" +
	                          "XHTML exists: " + xhtmlFile.exists() + ", readable: " + xhtmlFile.canRead();

	            return ResponseEntity.ok(result);
	        } catch (BizException e) {
	            return ResponseEntity.ok("Error: " + e.getMessage());
	        }
	    }

}


