package com.futechsoft.framework.common.pagination;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.futechsoft.framework.util.FtMap;

public class Pageable {
    private int page = 1;  // 기본값 1페이지
    private int pageSize = 10; // 기본 페이지 크기
	private long totalCount;
	private int lastPage;
	
	
	private String orderByString = "";
	
	public void setParam(FtMap param) {
        page=param.getInt("page");
        pageSize=param.getInt("pageSize");
        
        
    	List<Map<String, String>> sorters = (List<Map<String, String>>) param.get("sorters");
    	
    	
    	System.out.println("sorters............"+sorters);
    	

		// → MyBatis 쿼리에서 사용할 정렬 문자열 생성
		StringBuilder orderByClause = new StringBuilder();
		if (sorters != null) {
			/*
			for (Map<String, String> sorter : sorters) {
				String field = sorter.get("field");
				String dir = sorter.get("dir");
				if (field != null && dir != null) {
					if (orderByClause.length() > 0)
						orderByClause.append(", ");
					orderByClause.append(field).append(" ").append(dir);
				}
			}*/
			
			List<String> whitelist = Arrays.asList("ID", "NO", "YEAR", "LIST", "WRITE", "FILE", "COUNT");

			for (Map<String, String> sorter : sorters) {
			    String field = sorter.get("field");
			    String dir = sorter.get("dir");
			    
			    if (field != null && dir != null) {
				    if (whitelist.contains(field.toUpperCase()) && ("asc".equals(dir) || "desc".equals(dir))) {
				        if (orderByClause.length() > 0) orderByClause.append(", ");
				        orderByClause.append("\"").append(field).append("\" ").append(dir);  
				    }
			    }
			}
			
		}
		
		
		
		
		orderByString=orderByClause.toString();
		
		param.put("orderBy", orderByString);
		
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
    	lastPage= (int) Math.ceil((double) totalCount / pageSize);
	}


	public String getOrderByString() {
		return orderByString;
	}


	public void setOrderByString(String orderByString) {
		this.orderByString = orderByString;
	}
    
    
}
