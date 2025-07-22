
package com.futechsoft.framework.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeTagUtil extends BodyTagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(CodeTagUtil.class);
	
	private static final long serialVersionUID = 1L;

	private List<HashMap<String, String>> codeList;

	public void setCodeList(ArrayList<HashMap<String, String>> codeList) {
		this.codeList = codeList;
	}

	public int doAfterBody() {

		JspWriter out = null;

		try {
			BodyContent bc = getBodyContent();

			String code = bc.getString().trim();
			String value = "";

			HashMap<String, String> map = null;
			for (int i = 0; i < codeList.size(); i++) {
				map = codeList.get(i);

				if (map.containsKey(code)) {
					value = map.get("code");
					break;
				}
			}

			out = bc.getEnclosingWriter();
			if (value.length() > 0) {
				out.println(value);
			}

		} catch (IOException ignored) {
//			ignored.printStackTrace();
			LOGGER.error(ignored.toString());
		}
		return SKIP_BODY;
	}
}
