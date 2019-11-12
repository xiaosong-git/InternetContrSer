package com.uppermac.entity.responseModel.qRCodeModel;

/**
 * Created by Administrator on 2018/11/29 0029.
 */

public class QRCodeCommonMessage {
	
	 /**
     *	 二维码特殊标识符
     */
    private String identifier;
    
    /**
     * 	二维码总页数
     */
    private String totalPages;


    /**
     * 	二维码类型
     */
    private String bitMapType;

    private String currentPage;

    private String viewTime;
    
    private String content;

    public String getBitMapType() {
        return bitMapType;
    }
    
    public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setBitMapType(String bitMapType) {
        this.bitMapType = bitMapType;
    }

    public String getViewTime() {
        return viewTime;
    }

    public void setViewTime(String viewTime) {
        this.viewTime = viewTime;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }


    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

	@Override
	public String toString() {
		return "QRCodeCommonMessage [identifier=" + identifier + ", totalPages=" + totalPages + ", bitMapType="
				+ bitMapType + ", currentPage=" + currentPage + ", viewTime=" + viewTime + ", content=" + content + "]";
	}

	
    
}
