package com.futechsoft.framework.common.page;

import java.io.Serializable;

public class Sort implements Serializable{

	private String direction = "";

	public Sort() {

	}

	public Sort setSort(String sortName) {
		direction = sortName + " asc";
		return this;
	}

	public Sort setAsc(String sortName) {
		direction = sortName + " asc";
		return this;
	}

	public Sort setDesc(String sortName) {
		direction = sortName + " desc";
		return this;
	}

	public String getDirection() {

		return direction;
	}

}
