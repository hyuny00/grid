package com.futechsoft.framework.common.page;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Pageable pageable;

	private List<T> list;

	public Page() {
		
	}
	
	public Page(Pageable pageable, List<T> list) {
		this.pageable = pageable;
		this.list = list;
	}
	
	public Page(Pageable pageable, List<T> list, long count) {
		this.pageable = pageable;
		this.list = list;

		pageable.setTotalCount(count);

	}
	

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}
