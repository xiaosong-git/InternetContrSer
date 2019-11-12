package com.uppermac.entity.responseModel;

import java.util.List;

public class GetStaffScheduleDataResponseModel {
		private int pageNum;
	    private int pageSize;
	    private String totalPage;
	    private String total;
	    private List<GetStaffScheduleVisitorResponseModel> rows;

	    public int getPageNum() {
	        return pageNum;
	    }

	    public void setPageNum(int pageNum) {
	        this.pageNum = pageNum;
	    }

	    public int getPageSize() {
	        return pageSize;
	    }

	    public void setPageSize(int pageSize) {
	        this.pageSize = pageSize;
	    }

	    public String getTotalPage() {
	        return totalPage;
	    }

	    public void setTotalPage(String totalPage) {
	        this.totalPage = totalPage;
	    }

	    public String getTotal() {
	        return total;
	    }

	    public void setTotal(String total) {
	        this.total = total;
	    }

		public List<GetStaffScheduleVisitorResponseModel> getRows() {
			return rows;
		}

		public void setRows(List<GetStaffScheduleVisitorResponseModel> rows) {
			this.rows = rows;
		}

	  
}
