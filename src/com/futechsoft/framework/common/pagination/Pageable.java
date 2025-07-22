package com.futechsoft.framework.common.pagination;

import java.util.Arrays;
import java.util.List;

import com.futechsoft.framework.util.CommonUtil;
import com.futechsoft.framework.util.FtMap;

public class Pageable {
	private int page = 1; // 기본값 1페이지
	private int pageSize = 10; // 기본 페이지 크기
	private long totalCount;
	private int lastPage;

	private String orderByString = null;

	public void setParam(FtMap param) {
		page = param.getInt("page");
		pageSize = param.getInt("pageSize");

		StringBuilder orderByClause = new StringBuilder();
	
		
		
		//List<String> allowedFields = Arrays.asList("ID", "NAME", "CREATED_AT");
		List<String> allowedDirections = Arrays.asList("ASC", "DESC");

		String sortField = param.getString("sortField");
		String sortDirection = param.getString("sortDirection");

		if (sortField != null && sortDirection != null &&
		    !sortField.trim().isEmpty() && !sortDirection.trim().isEmpty() &&
		    sortField.matches("^[A-Za-z_]{1,20}$")) {

		    String direction = sortDirection.toUpperCase();

		    if (allowedDirections.contains(direction)) {
		        String orderBy = "\"" + sortField + "\" " + direction;
		        param.put("orderBy", orderBy);
		    }
		}


	}

	public int getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	private boolean isPaged = true;

	public boolean isPaged() {
		return isPaged;
	}

	public void setPaged(boolean isPaged) {
		this.isPaged = isPaged;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
		paging();
	}

	// Getter / Setter
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getOffset() {
		return (page - 1) * pageSize;
	}

	private void paging() {
		lastPage = (int) Math.ceil((double) totalCount / pageSize);
	}

	public String getOrderByString() {
		return orderByString;
	}

	public void setOrderByString(String orderByString) {
		this.orderByString = orderByString;
	}

}
