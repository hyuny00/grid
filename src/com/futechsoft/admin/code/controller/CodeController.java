package com.futechsoft.admin.code.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.futechsoft.admin.code.service.CodeService;
import com.futechsoft.admin.code.vo.Code;
import com.futechsoft.framework.common.controller.AbstractController;
import com.futechsoft.framework.util.FtMap;
import com.futechsoft.framework.util.SecurityUtil;

@Controller
public class CodeController extends AbstractController {

	@Resource(name = "code.service.CodeService")
	private CodeService codeService;

	@RequestMapping("/admin/code/codeManager")
	public String menuManager(HttpServletRequest request) throws Exception {
		return "admin_tiles:admin/code/codeManager";
	}

	@ResponseBody
	@RequestMapping("/admin/code/getCodeList")
	public List<FtMap> getMenuListr(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		if (StringUtils.isEmpty(params.getString("cdSeq")) || params.getString("cdSeq").equals("#")) {
			params.put("cdSeq", "0");
		}
		List<Code> codeList = codeService.getList(params);

		List<FtMap> codeListMap = new ArrayList<FtMap>();

		FtMap map = null;
		for (Code code : codeList) {
			map = new FtMap();

			map.put("id", String.valueOf(code.getCdSeq()));
			map.put("text", StringUtils.defaultString(code.getUseYn()).equals("N") ? code.getDtlCdNm() + "(X)" : code.getDtlCdNm());

			map.put("dtlCdNm", code.getDtlCdNm());
			map.put("upCdSeq", code.getUpCdSeq());
			map.put("dtlCd", code.getDtlCd());
			map.put("upDtlCdNm", code.getUpDtlCdNm());
			map.put("shotDtlCdNm", code.getShotDtlCdNm());
			map.put("cdDiv", String.valueOf(code.getCdDiv()));
			map.put("upCdDiv", code.getUpCdDiv());
			map.put("useYn", code.getUseYn());
			map.put("rmk", code.getRmk());
			map.put("children", code.getSubCdCnt() > 0 ? true : false);
			map.put("type", code.getSubCdCnt() > 0 ? "noLast" : "last");

			codeListMap.add(map);
		}

		return codeListMap;
	}

	@ResponseBody
	@RequestMapping("/admin/code/updateCodeOrd")
	public FtMap updateMenuOrd(HttpServletRequest request, @RequestParam(value = "code_seqs[]") String[] code_seqs) throws Exception {

		codeService.saveCodeOrd(code_seqs);
		FtMap params = new FtMap();
		params.put("isSuccess", true);
		return params;

	}

	@ResponseBody
	@RequestMapping("/admin/code/saveCode")
	public FtMap saveMenu(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);
		params.put("userNo", SecurityUtil.getUserNo());

		codeService.save(params);

		params.put("isSuccess", true);
		return params;

	}

	@ResponseBody
	@RequestMapping("/admin/code/deleteCode")
	public FtMap deleteMenu(HttpServletRequest request) throws Exception {

		FtMap params = getFtMap(request);

		codeService.delete(params);
		params.put("isSuccess", true);

		return params;

	}
}
