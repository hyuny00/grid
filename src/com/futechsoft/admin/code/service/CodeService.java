package com.futechsoft.admin.code.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futechsoft.admin.code.mapper.CodeMapper;
import com.futechsoft.admin.code.vo.Code;
import com.futechsoft.framework.util.FtMap;

@Service("code.service.CodeService")
public class CodeService {

	@Resource(name = "code.mapper.CodeMapper")
	private CodeMapper mapper;

	public List<Code> getList(FtMap params) throws Exception {

		return mapper.getCodeList(params);
	}

	public void save(FtMap params) throws Exception {

		if (params.getString("mode").equals("new")) {
			mapper.insertCode(params);
		} else {
			mapper.updateCode(params);
		}

	}

	@Transactional
	public void saveCodeOrd(String[] cdSeq) throws Exception {

		FtMap params = new FtMap();
		for (int i = 0; i < cdSeq.length; i++) {
			params.put("cdSeq", cdSeq[i]);
			params.put("srtOrd", i);
			mapper.updateSrtOrd(params);
		}

	}

	@Transactional
	public void delete(FtMap params) throws Exception {
		params.put("useYn", "N");
		mapper.updateUseYn(params);

	}
}
