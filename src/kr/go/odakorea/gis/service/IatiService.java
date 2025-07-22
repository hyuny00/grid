package kr.go.odakorea.gis.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//C:/Users/Wakanda/Documents/iati/iati_act_KR-GOV-051_2023.xml
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.futechsoft.framework.file.service.FileUploadService;
import com.futechsoft.framework.file.vo.FileInfoVo;
import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;

import kr.go.odakorea.gis.mapper.IatiMapper;

@Service("gis.service.IatiService")
public class IatiService {
	
	@Resource(name = "framework.file.service.FileUploadService")
	FileUploadService fileUploadService;
	
	
	@Value("${file.uploadPath.lati}")
	private String latiUploadPath;
	
	

	@Resource(name = "gis.mapper.IatiMapper")
	private IatiMapper iatiMapper;
	
	
	//@Transactional
	public void saveIatiInfo(FtMap params) throws Exception {
		params.put("userId", SecurityUtil.getUserId());

		//업무구분코드에 맞게 세팅
		params.put("taskSecd", "0000");
	
		//파일저장 및 경로 세팅(한글경로 불가). AAAA디렉토리/yyyymm 폴더 아래 파일 저장
		String attcDocId=fileUploadService.saveFile(params, "iati","attcDocId");
		params.put("docId", attcDocId);

	    List<FtMap> fileList= fileUploadService.selectFileList(params);
	    
	 
	    
	    if(fileList!=null && fileList.size()==1) {
	    	
	    	FileInfoVo fileInfoVo = fileUploadService.getFileInfo(fileList.get(0).getString("fileId"));
	    	
	    	String uploadPath= fileUploadService.getRealUploadPath();
	    	
	    	 Path source = Paths.get(uploadPath, fileInfoVo.getFilePath(), fileInfoVo.getFileId()+".FILE");
	         Path target = Paths.get(latiUploadPath,fileInfoVo.getFileId()+".xml");
	         
	         if (!Files.exists(target)) {
	             Files.createDirectories(target);
	         }
	         
	         Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
	    	
	    	
	    	 List<FtMap> resultList = parseIatiXml(target.toString());
	    	 
	    	 for(FtMap map : resultList) {
		        	System.out.println(">>>>>>>>>"+map);
		      }
	    	 
	    	 for(FtMap param :  resultList) {
	    		 
	    		 
	    		 param.put("userId", SecurityUtil.getUserId());
	    		 
	    		 FtMap map = iatiMapper.getRgnNo(param);
	    		 
	    		 try {
		    		 if(map ==  null) {
		    			 iatiMapper.inserNtnRgn(param);
		    		 } else if( CommonUtil.nvl(map.getString("bizRgnPstnLotVl")).equals("") ||  CommonUtil.nvl(map.getString("bizRgnPstnLatVl")).equals("")) {
			    		iatiMapper.updateNtnRgn(param);
		    		 }
	    		 }catch(Exception e) {
	    			 e.printStackTrace();
	    		 }
	    		
	    		 
	    		 
	    	 }
	    	
	    }
		
	}
	
	public static void main(String[] args) throws Exception {
		IatiService a= new IatiService();
	      List<FtMap> list = a.parseIatiXml("C:/Users/Wakanda/Documents/iati/iati_act_KR-GOV-051_2023.xml");
	      
	      for(FtMap map : list) {
	        	System.out.println(map);
	      }
	}
	
	public  List<FtMap> parseIatiXml(String filePath) throws Exception {
        List<FtMap> list = new ArrayList<>();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        parser.parse(new File(filePath), new DefaultHandler() {

            boolean inActivity = false;
            boolean inIdentifier = false;
            boolean inLocation = false;
            boolean inName = false;
            boolean inNarrative = false;
            boolean inPos = false;
            boolean inRecipientCountry = false;

            String currentIdentifier = "";
            String currentNarrative = "";
            String currentPos = "";
            String currentCountryCode = "";
            String currentCountryName = "";

            FtMap param = null;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                switch (qName) {
                    case "iati-activity":
                        inActivity = true;
                        currentIdentifier = "";
                        break;
                    case "iati-identifier":
                        if (inActivity) inIdentifier = true;
                        break;
                    case "location":
                        if (inActivity) {
                            inLocation = true;
                            param = new FtMap();  // 새 객체 생성
                        }
                        break;
                    case "name":
                        if (inLocation) inName = true;
                        break;
                    case "narrative":
                        if (inName || inRecipientCountry) {
                            inNarrative = true;
                            currentNarrative = "";
                        }
                        break;
                    case "pos":
                        if (inLocation) {
                            inPos = true;
                            currentPos = "";
                        }
                        break;
                    case "recipient-country":
                        inRecipientCountry = true;
                        currentCountryCode = attributes.getValue("code");
                        currentCountryName = "";
                        break;
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) {
                String text = new String(ch, start, length).trim();
                if (inIdentifier && !text.isEmpty()) {
                    currentIdentifier += text;
                }
                if (inNarrative && !text.isEmpty()) {
                    currentNarrative += text;
                }
                if (inPos && !text.isEmpty()) {
                    currentPos += text;
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) {
                switch (qName) {
                    case "iati-identifier":
                        inIdentifier = false;
                        break;
                    case "narrative":
                        if (inName && inLocation) {
                            // 지역명 끝남
                        } else if (inRecipientCountry) {
                            currentCountryName = currentNarrative;
                        }
                        inNarrative = false;
                        break;
                    case "pos":
                        inPos = false;
                        break;
                    case "name":
                        inName = false;
                        break;
                    case "location":
                        inLocation = false;

                        if (param != null) {
                            param.put("rgnEngNm", currentNarrative);
                            param.put("iatiIdentifier", currentIdentifier);

                            String[] parts = currentPos.split("\\s+");
                            if (parts.length == 2) {
                                double latitude = Double.parseDouble(parts[0]);
                                double longitude = Double.parseDouble(parts[1]);

                                param.put("bizRgnPstnLatVl", latitude);
                                param.put("bizRgnPstnLotVl", longitude);
                            }

                            param.put("ntnCd", currentCountryCode);
                            param.put("ntnNm", currentCountryName);

                            list.add(param);
                        }
                        break;
                    case "recipient-country":
                        inRecipientCountry = false;
                        currentCountryName = currentNarrative;

                        System.out.println("국가 코드: " + currentCountryCode);
                        System.out.println("국가명: " + currentCountryName);
                        System.out.println("===========================");
                        break;
                    case "iati-activity":
                        inActivity = false;
                        break;
                }
            }

            @Override
            public void endDocument() {
                System.out.println("전체 XML 파싱 완료");
            }
        });

        return list;
    }

}
