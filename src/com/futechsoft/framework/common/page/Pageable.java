package com.futechsoft.framework.common.page;

import java.io.Serializable;

public class Pageable implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pageNo = 1;
	private int offset;
	private int pageSize = 10;
	private long totalCount;
	private int beginPage;
	private int endPage;
	private int displayPage = 10;
	private int totalPage = 0;
	boolean prev;
	boolean next;
	boolean first;
	boolean last;

	private Sort sort;
	private boolean isPaged = true;
	private boolean isSorted = false;


	private int listNum = 0;

	//추가
	private int listNumber = 1;


	private int listPageSize = 10;

	public Pageable() {
	}

	public Pageable(boolean isPaged) {
		this.isPaged = isPaged;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public int getPageNo() {
		if (pageNo < 1 )pageNo = 1;
		return pageNo;
	}

	public boolean isPaged() {
		return isPaged;
	}

	public boolean isSorted() {
		return isSorted;
	}

	public void setPaged(boolean isPaged) {
		this.isPaged = isPaged;
	}

	public Pageable(int pageNo, int pageSize) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.isSorted = false;
	}

	public Pageable(int pageNo, int pageSize, int displayPage) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.displayPage = displayPage;
		this.isSorted = false;
	}

	public Pageable(int pageNo, int pageSize, Sort sort) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.sort = sort;
		this.isSorted = true;
	}

	public Pageable(int pageNo, int pageSize, int displayPage, Sort sort) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.displayPage = displayPage;
		this.isSorted = true;
		this.sort = sort;
	}

	public int getOffset() {
		this.offset = (getPageNo() - 1) * getPageSize();
		return offset;
	}

	public int getPageSize() {
		return pageSize;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
		paging();
	}

	public int getBeginPage() {
		return beginPage;
	}

	public void setBeginPage(int beginPage) {
		this.beginPage = beginPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public int getDisplayPage() {
		return displayPage;
	}

	public void setDisplayPage(int displayPage) {
		this.displayPage = displayPage;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.isSorted = true;
		this.sort = sort;
	}

	public boolean isPrev() {
		return prev;
	}

	public boolean isNext() {
		return next;
	}

	public int getListPageSize() {
		return listPageSize;
	}

	public void setListPageSize(int listPageSize) {
		this.listPageSize = listPageSize;
	}

	public int getListNum() {
		return listNum;
	}
	//추가
	public int getListNumber() {
		return listNumber;
	}

	private void paging() {

		endPage = ((int) Math.ceil(pageNo / (double) displayPage)) * displayPage;
		beginPage = endPage - (displayPage - 1);
		totalPage = (int) Math.ceil(totalCount / (double) pageSize);

		if (endPage > totalPage) {
			endPage = totalPage;
		}

		next = (endPage < totalPage) ? true : false;
		prev = (pageNo > displayPage) ? true : false;


		listNum =(int)totalCount - ( (pageNo-1)*pageSize );

		listNumber = (pageNo-1)*pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

}
