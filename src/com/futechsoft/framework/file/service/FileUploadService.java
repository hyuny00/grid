package com.futechsoft.framework.file.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.futechsoft.framework.exception.ErrorCode;
import com.futechsoft.framework.exception.FileUploadException;
import com.futechsoft.framework.exception.ZipParsingException;
import com.futechsoft.framework.file.mapper.FileMapper;
import com.futechsoft.framework.file.vo.FileInfoVo;
import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.ConvertUtil;
import com.futechsoft.framework.util.FileUtil;
import com.futechsoft.framework.util.FtMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import egovframework.rte.fdl.logging.util.EgovResourceReleaser;

//@PropertySource("classpath:globals.properties")

@Service("framework.file.service.FileUploadService")
public class FileUploadService extends EgovAbstractServiceImpl {


	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadService.class);


	@Resource(name = "framework.file.mapper.FileMapper")
	private FileMapper mapper;

	//@Autowired
	//PropertiesConfiguration propertiesConfiguration;
	
	
	@Value("${file.uploadPath.temp}")
	private String tempUploadPath;
	
	@Value("${file.uploadPath}")
	private String realUploadPath;
	
	
	@Value("${file.uploadPath.temp.zip}")
	private String tempZipPath;
	
	
	public String getTempZipPath() {
		return tempZipPath;
	}
	
	public String getRealUploadPath() {
		return realUploadPath;
	}

	/**
	 * 파일을 임시경로에 업로드
	 *
	 * @param multipartFile
	 * @param fileInfoVo
	 * @return
	 * @throws FileUploadException
	 */
	public FileInfoVo upload(MultipartFile multipartFile, FileInfoVo fileInfoVo) throws FileUploadException {

	

		FileInfoVo fileObject = null;
		try {
			File f = new File(tempUploadPath);

			if (!f.exists()) f.mkdirs();

			fileObject = writeFile(multipartFile, fileInfoVo);
		} catch (Exception e) {
//			e.printStackTrace();
			LOGGER.error(e.toString());
			throw new FileUploadException(ErrorCode.FILE_UPLOAD_ERROR, e);
		}
		return fileObject;
	}

	/**
	 * 파일을 저장한다
	 * @param multipartFile
	 * @param fileInfoVo
	 * @return
	 * @throws Exception
	 */
	private FileInfoVo writeFile(MultipartFile multipartFile, FileInfoVo fileInfoVo) throws Exception {



		long totalChunks = fileInfoVo.getTotalChunks();
		long chunkIndex = fileInfoVo.getChunkIndex();

		String fileId = "";
		if (!fileInfoVo.getFileId().equals("")) {
			fileId = fileInfoVo.getFileId();
		} else {
			fileId = FileUtil.getRandomId();
		}

		String fileName = fileInfoVo.getFileNm();

		// 파일 정보
		String originFilename = fileName;

		String extName = "";
		if (originFilename.lastIndexOf(".") != -1) {
			extName = FileUtil.getFileExt(originFilename);
		}

		byte[] data = multipartFile.getBytes();

		File uploadFile = Paths.get(tempUploadPath,  fileId + ".TEMP").toFile();

		FileOutputStream fos = new FileOutputStream(uploadFile, true);
		FileCopyUtils.copy(data, fos);

		fileInfoVo.setUploadComplete("N");
		fileInfoVo.setFileId(fileId);
		fileInfoVo.setFileNm(fileName);
		fileInfoVo.setTemp("Y");

		if (totalChunks == chunkIndex) {

			fileId = fileId.substring(fileId.lastIndexOf("\\") + 1);

			if (uploadFile.length() != fileInfoVo.getFileSize()) {
				throw new IOException();
			}

			fileInfoVo.setFileSize(uploadFile.length());
			fileInfoVo.setFileExt(extName);
			fileInfoVo.setFileId(fileId);
			fileInfoVo.setTempFilePath(Paths.get(tempUploadPath, fileId + ".TEMP").toString());
			fileInfoVo.setFileNm(fileName);
			fileInfoVo.setUploadComplete("Y");
			fileInfoVo.setTemp("Y");

		}

		EgovResourceReleaser.close(fos);

		return fileInfoVo;
	}



	/**
	 * 파일을 저장한다
	 * @param paramMap
	 * @param saveFilePath
	 * @throws Exception
	 */
	@Transactional
	public void saveFile(FtMap paramMap, String saveFilePath) throws Exception {
		save( paramMap,  saveFilePath,  "");
	}

	/**
	 * 파일을 저장한다
	 * @param paramMap
	 * @param saveFilePath
	 * @param findDocId
	 * @return 파일아이디
	 * @throws Exception
	 */
	@Transactional
	public String saveFile(FtMap paramMap, String saveFilePath, String findDocId) throws Exception {
		List<String> tempDocIdList = save( paramMap,  saveFilePath, findDocId);

		return  (tempDocIdList!=null && tempDocIdList.size()==1)? tempDocIdList.get(0) : "";
	}

	/**
	 * 파일을 저장한다
	 * @param paramMap
	 * @param saveFilePath
	 * @param findDocId
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public List<String> saveFiles(FtMap paramMap, String saveFilePath, String findDocId) throws Exception {
		return  save( paramMap,  saveFilePath, findDocId);
	}
	
	

	/**
	 * 파일정보를 DB에 인서트한다
	 * @param param
	 * @throws Exception
	 */
	
	/*
	@Transactional
	public void saveFile(FtMap param) throws Exception {


		List<FtMap> columnList = mapper.getColumnList(TableInfo.FILE_TABLE);
		InsertParam insertParam = new InsertParam(TableInfo.FILE_TABLE, param, columnList, "file_id");
		mapper.insert(insertParam);
	}
   */
	
	
	/**
	 * 파일을 저장한다
	 * @param paramMap
	 * @param saveFilePath
	 * @param findDocId
	 * @return
	 * @throws Exception
	 */
	private List<String> save(FtMap paramMap, String saveFilePath, String findDocId) throws Exception {

		List<String> tempDocIdList = new ArrayList<String>();

		List<String> delFileIdList = new ArrayList<String>();
		List<String> addFileIdList = new ArrayList<String>();

		try {

			for (String value : paramMap.getStringArray("fileInfoList")) {
				
				


				if (StringUtils.isEmpty(value)) continue;

				JsonObject jsonObject = JsonParser.parseString(value).getAsJsonObject();
				String refDocId = jsonObject.get("refDocId").getAsString();

				if (!findDocId.equals("")) {
					if (!refDocId.equals(findDocId))  continue;
				}


				if (refDocId.equals("NONE")) continue;

				String docId = jsonObject.get("docId").getAsString();

				if(StringUtils.isEmpty(docId)) {
					docId = FileUtil.getRandomId();
					paramMap.put("isNewDocId", "true");
				}
				if (findDocId.equals("")) {
					paramMap.put(refDocId, docId);
				}

				saveProcess(jsonObject, paramMap, saveFilePath, findDocId, docId, delFileIdList, addFileIdList);
				tempDocIdList.add(docId);
			}

			paramMap.put("delFileIdList", delFileIdList);
			paramMap.put("addFileIdList", addFileIdList);


		} catch (IOException e) {
			throw new FileUploadException(ErrorCode.FILE_SAVE_ERROR, e);
		}

		return  tempDocIdList;
	}


	/**
	 * 파일을 저장한다
	 * @param jsonObject
	 * @param paramMap
	 * @param saveFilePath
	 * @param findDocId
	 * @param docId
	 * @param delFileIdList
	 * @param addFileIdList
	 * @throws Exception
	 */
	private void saveProcess(JsonObject jsonObject, FtMap paramMap, String saveFilePath, String findDocId, String docId, List<String>  delFileIdList, List<String> addFileIdList) throws Exception {
		

		if(!saveFilePath.equals("")) {
			saveFilePath =   Paths.get(saveFilePath,  FileUtil.getSaveFilePath()).toString();
		}else {
			saveFilePath = Paths.get(FileUtil.getSaveFilePath()).toString();
		}

		Gson gson = new Gson();

		FileInfoVo[] fileInfoVos = gson.fromJson(jsonObject.get("fileInfo").getAsJsonArray(), FileInfoVo[].class);


		String refDocId = gson.fromJson(jsonObject.get("refDocId").getAsString(), String.class);

		if (refDocId.equals("NONE")) return;

		if (!findDocId.equals("")) {
			if (!refDocId.equals(findDocId)) return;
		}

	
		int fileOrd = 0;
		
		/*
		if(paramMap.getBoolean("isNewDocId")) {
			FtMap fileGroupParam = new FtMap();
			fileGroupParam.put("docId",docId);
			fileGroupParam.put("taskSecd", paramMap.getString("taskSecd"));
			fileGroupParam.put("userId", paramMap.getString("userId"));
			mapper.insertFileGroup(fileGroupParam);
		}*/
		
		FtMap fileGroupParam = new FtMap();
		fileGroupParam.put("docId",docId);
		String docIdCheck = mapper.selectDocId(fileGroupParam);
		if(CommonUtil.nvl(docIdCheck).equals("")) {
			fileGroupParam.put("docId",docId);
			fileGroupParam.put("taskSecd", paramMap.getString("taskSecd"));
			fileGroupParam.put("userId", paramMap.getString("userId"));
			mapper.insertFileGroup(fileGroupParam);
		}
		
		
		 List<String> zipFileIdList = new ArrayList<String>();
		 
		for (FileInfoVo fileInfoVo : fileInfoVos) {

			fileInfoVo.setFileOrd(fileOrd++);


			if (StringUtils.defaultString(fileInfoVo.getTemp()).equals("Y")) {
				File file = Paths.get(tempUploadPath, fileInfoVo.getFileId() + ".TEMP").toFile();
				File fileToMove = Paths.get(realUploadPath, saveFilePath, fileInfoVo.getFileId() + ".FILE").toFile();
 

				FileUtils.moveFile(file, fileToMove);
				
				if(CommonUtil.nvl(fileInfoVo.getIsOnlyZip()).equals("Y")) {
					zipFileIdList.add(fileInfoVo.getFileId());
				}


				fileInfoVo.setDocId(docId);
				fileInfoVo.setFilePath(saveFilePath);
				fileInfoVo.setTblColNm(FtMap.getSnakeCase(refDocId));

/*
				if(paramMap.getString("tblNm").equals("")) {
					LOGGER.debug("테이블 명을 입력하지 않았습니다.");
					throw new Exception();
				}
*/
				fileInfoVo.setTblNm(paramMap.getString("tblNm"));

				if(paramMap.getString("openYn").equals("N")) {
					fileInfoVo.setFileAuth("rwr---");
				}else {
					fileInfoVo.setFileAuth("rwr-r-");
				}

				Map<String, Object> map = ConvertUtil.beanToMap(fileInfoVo);
				FtMap param = new FtMap();
				param.setFtMap(map);
				param.put("userId", paramMap.getString("userId"));
				param.put("taskSecd", paramMap.getString("taskSecd"));
				
			
				mapper.insertFileInfo(param);

				if(StringUtils.defaultString(fileInfoVo.getThumbnailYn()).equals("Y")) {

					FileUtil.createThumnail(realUploadPath, paramMap.getInt("thumnailWidth"), paramMap.getInt("thumnailHeight"), fileInfoVo);

					String ext= FileUtil.getFileExt(fileInfoVo.getFileNm()) ;
					File thumnail = Paths.get(realUploadPath, saveFilePath, "thumbnail",  fileInfoVo.getFileId() + "."+ext).toFile();

					String thumnailPath= Paths.get(saveFilePath,"thumbnail").toString();

					long fileSize=thumnail.length();

					fileInfoVo.setFilePath(thumnailPath);
					fileInfoVo.setFileSize(fileSize);

					mapper.updateThumbNnailFileInfo(fileInfoVo);

				}

				addFileIdList.add(fileInfoVo.getFileId());

			} else {
				FtMap param = new FtMap();
				param.put("fileId", fileInfoVo.getFileId());
				param.put("fileOrd", fileInfoVo.getFileOrd());
				mapper.updateFileOrd(param);
			}

			//실제파일삭제START
			 if(!(StringUtils.defaultString(fileInfoVo.getTemp()).equals("Y")) && fileInfoVo.getDelYn().equals("Y")) {

					//실제파일삭제START
					 /*
					 	paramMap.put("fileId", fileInfoVo.getFileId());
					 	fileInfoVo =getFileInfo(paramMap);

					 	File delFile = Paths.get(realUploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId() + ".FILE").toFile();

						FtMap params = new FtMap();
						params.put("file_id", fileInfoVo.getFileId());
						deleteFile(params);

						boolean check=delFile.delete();

						LOGGER.debug(fileInfoVo.getFileNm() +": 파일삭제..."+check);
					*/
					delFileIdList.add(fileInfoVo.getFileId());
			 }

		}
		
		paramMap.put("zipFileIdList", zipFileIdList);
	}

	/**
	 * 파일을 삭제한다
	 * @param params
	 * @throws Exception
	 */
	public void deleteFile(FtMap params) throws Exception {
		//mapper.deleteFile(params);
		mapper.updateFileDtDelYn(params);
	}

	/**
	 * 압축파일을 생성한다
	 * @param src
	 * @param target
	 * @throws ZipParsingException
	 */
	public void createZip(String src, String target) throws ZipParsingException {
		Path srcDir = Paths.get(src);

		File targetDir = new File(target).getParentFile();
		boolean check = false;

		if (!targetDir.exists() && targetDir.toString().indexOf(src) != -1) {
			check = targetDir.mkdirs();
			if (!check) throw new ZipParsingException("디렉토리 생성 실패");
		}

		File zipFileName = Paths.get(target).toFile();

		try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFileName))) {

			DirectoryStream<Path> dirStream = Files.newDirectoryStream(srcDir);
			for (Path path : dirStream) {
				addToZipFile(path, zipStream);
			}
			dirStream.close();

		} catch (IOException | ZipParsingException e) {
			throw new ZipParsingException(e.getMessage());
		}
	}

	/**
	 * 압축파일을 생성한다
	 * @param fileInfoVos
	 * @param target
	 * @throws Exception
	 */
	public void createZip(FileInfoVo[] fileInfoVos, String target) throws Exception {
		

		File targetDir = new File(target).getParentFile();
		boolean check = false;

		if (!targetDir.exists()) {
			check = targetDir.mkdirs();
			if (!check) throw new ZipParsingException("디렉토리 생성 실패");
		}

		File zipFileName = Paths.get(target).toFile();

		try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFileName))) {

			for (FileInfoVo fileInfoVo : fileInfoVos) {

				//파일다운로드 권한
				boolean fileDownloadCheck =FileUtil.hasFileDownloadAuth(fileInfoVo);
				if(fileDownloadCheck) {

					File file = Paths.get(realUploadPath,StringUtils.defaultString( fileInfoVo.getFilePath()), fileInfoVo.getFileId() + ".FILE").toFile();

					addToZipFile(file, fileInfoVo.getFileNm(), zipStream);
				}

			}

		} catch (IOException | ZipParsingException e) {
//			e.printStackTrace();
			LOGGER.error(e.toString());
			throw new ZipParsingException(e.getMessage());
		}
	}

	/**
	 * 압축할 파일을 추가한다
	 * @param file
	 * @param zipStream
	 * @throws ZipParsingException
	 */
	private void addToZipFile(Path file, ZipOutputStream zipStream) throws ZipParsingException {

		if (file.toFile().isDirectory()) return;
		String inputFileName = file.toFile().getPath();

		try (FileInputStream inputStream = new FileInputStream(inputFileName)) {

			ZipEntry entry = new ZipEntry(file.toFile().getName());
			entry.setCreationTime(FileTime.fromMillis(file.toFile().lastModified()));
			entry.setComment("");
			zipStream.putNextEntry(entry);

			byte[] readBuffer = new byte[2048];
			int amountRead;

			while ((amountRead = inputStream.read(readBuffer)) > 0) {
				zipStream.write(readBuffer, 0, amountRead);
			}

		} catch (IOException e) {
			throw new ZipParsingException("Unable to process " + inputFileName, e);
		}
	}

	/**
	 * 압축할 파일을 추가한다
	 * @param file
	 * @param fileNm
	 * @param zipStream
	 * @throws ZipParsingException
	 */
	private void addToZipFile(File file, String fileNm, ZipOutputStream zipStream) throws ZipParsingException {

		if (file.isDirectory())
			return;

		try (FileInputStream inputStream = new FileInputStream(file)) {

			ZipEntry entry = new ZipEntry(fileNm);

			// entry.setCreationTime(FileTime.fromMillis(file.lastModified()));
			entry.setComment("");
			zipStream.putNextEntry(entry);

			byte[] readBuffer = new byte[2048];
			int amountRead;

			while ((amountRead = inputStream.read(readBuffer)) > 0) {
				zipStream.write(readBuffer, 0, amountRead);
			}

		} catch (IOException e) {
			throw new ZipParsingException("Unable to process " + fileNm, e);
		}
	}

	/**
	 * 파일목록을 구한다
	 * @param params
	 * @return 파일목록
	 * @throws Exception
	 */
	public List<FtMap> selectFileList(FtMap params) throws Exception {
		return mapper.selectFileList(params);
	}

	/**
	 * 파일정보를 조회한다
	 * @param params
	 * @return 파일정보
	 * @throws Exception
	 */
	public FileInfoVo getFileInfo(FtMap params) throws Exception {

		return mapper.selectFileInfo(params);
	}
	
	public FileInfoVo getFileInfo(String fileId) throws Exception {
		FtMap params = new  FtMap();
		params.put("fileId", fileId);
		return mapper.selectFileInfo(params);
	}


	/**
	 * 파일 BLOB정보를 조회한다
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public FtMap selectFileDataInfo(FtMap params) throws Exception{

		return mapper.selectFileDataInfo(params);
	}


	/**
	 * 파일정보를 병합한다
	 * @param tempDocIdList
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public String newMergeDocId(List<String> tempDocIdList) throws Exception {

		String mergeDocId = "";
		if(tempDocIdList!= null && tempDocIdList.size()>0) {
			mergeDocId=tempDocIdList.get(0);
		}

		if(StringUtils.defaultString(mergeDocId).equals("")) {
			mergeDocId = FileUtil.getRandomId();
		}

		for(String tempDocId: tempDocIdList) {

			FtMap params = new FtMap();
			params.put("mergeDocId", mergeDocId);
			params.put("tempDocId", tempDocId);

			mapper.updateMergeDocId(params);
		}
		return mergeDocId;
	}


	/**
	 * 임시파일정보를 조회한다
	 * @param paramMap
	 * @param findDocId
	 * @return
	 * @throws Exception
	 */
	public FileInfoVo[] findTempFileInfo(FtMap paramMap,String findDocId) throws Exception {


		FileInfoVo[] fileInfoVos=null;

		try {

			for (String value : paramMap.getStringArray("fileInfoList")) {


				if (StringUtils.isEmpty(value)) continue;

				JsonObject jsonObject = JsonParser.parseString(value).getAsJsonObject();
				String refDocId = jsonObject.get("refDocId").getAsString();

				if (!findDocId.equals("")) {
					if (!refDocId.equals(findDocId))  continue;
				}


				if (refDocId.equals("NONE")) continue;

				Gson gson = new Gson();
				fileInfoVos = gson.fromJson(jsonObject.get("fileInfo").getAsJsonArray(), FileInfoVo[].class);
			}

		} catch (Exception e) {
			throw new FileUploadException(ErrorCode.FILE_NOT_FOUND, e);
		}

		return  fileInfoVos;
	}

	public String nvlFileDoc(FtMap params) throws Exception {

		List<FtMap> chkFileList = selectFileList(params);

		if(chkFileList.size() > 0) {
			return params.getString("docId");
		}else {
			return "";
		}


	}



	/**
	 * 엑셀업로드 파일을 저장한다
	 * @param fileInfoVo
	 * @param saveFilePath
	 * @param docId
	 * @param tblNm
	 * @param refDocId
	 * @throws Exception
	 */
	public void saveExcelUploadFile(FileInfoVo fileInfoVo, String saveFilePath,  String docId,  String tblNm,  String refDocId) throws Exception {

	

		if(!saveFilePath.equals("")) {
			saveFilePath =   Paths.get(saveFilePath,  FileUtil.getSaveFilePath()).toString();
		}else {
			saveFilePath = Paths.get(FileUtil.getSaveFilePath()).toString();
		}



		int fileOrd = 0;

			fileInfoVo.setFileOrd(fileOrd++);


			if (StringUtils.defaultString(fileInfoVo.getTemp()).equals("Y")) {

				File file = Paths.get(tempUploadPath, fileInfoVo.getFileId() + ".TEMP").toFile();
				File fileToMove = Paths.get(realUploadPath, saveFilePath, fileInfoVo.getFileId() + ".FILE").toFile();


				FileUtils.moveFile(file, fileToMove);


				fileInfoVo.setDocId(docId);
				fileInfoVo.setFilePath(saveFilePath);
				fileInfoVo.setTblColNm(FtMap.getSnakeCase(refDocId));


				if(tblNm.equals("")) {
					LOGGER.debug("테이블 명을 입력하지 않았습니다.");
					throw new Exception();
				}

				fileInfoVo.setTblNm(tblNm);


				fileInfoVo.setFileAuth("rwr-r-");


				Map<String, Object> map = ConvertUtil.beanToMap(fileInfoVo);
				FtMap param = new FtMap();
				param.setFtMap(map);
 
				
				
				//삭제후 저장
				param.put("docId", docId);
				mapper.deleteDoc(param);


				mapper.insertFileInfo(param);

			}



		}


	/**
	 * 파일정보를 구한다
	 * @param params
	 * @return 파일목록
	 * @throws Exception
	 */
	public FtMap selectFile(FtMap params) throws Exception {
		return mapper.selectFile(params);
	}
	
	
	public void deleteFileGroup(String docId) throws Exception {
		mapper.updateFileGroupDelYn(docId);
		mapper.updateFileGroupDtDelYn(docId);
	}
}
