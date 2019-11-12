package com.uppermac.data;

/**
 * 返回类型枚举
 */
public enum ResponseStatus {
    /**
     * 成功返回
     */
    paramsError(10001, "failer"),

	/**
	 * 成功返回
	 */
    orderNoFailed(20001, "获取orderNo失败"),

    /**
     * 成功返回
     */
    commonFailed(30001, "未找到"),
    /**
     * 业务错误
     */
    businessFailed(40001, "业务错误"),
    /**
     * 系统异常
     */
    systemError(50001, "系统异常"),
    /**
     * 成功返回
     */
    success(200, "success");

    /**
     * 接口名称
     */
    private int code;

    /**
     * 接口返回数据时JSON反序列化用到的自定义反射类
     */
    private String message;

    /**
     * 接口信息枚举类构造函数
     *
     * @param code   返回码
     * @param message 返回信息
     */
    ResponseStatus(
            int code, String message)
    {
        this.code = code;
        this.message = message;
    }

    public int getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }
}
