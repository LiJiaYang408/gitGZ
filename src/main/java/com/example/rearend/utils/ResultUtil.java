package com.example.rearend.utils;


/**
 * 通用的API响应结果封装
 */
public class ResultUtil<T> {
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 具体数据
     */
    private T data;

    /**
     * 构造方法
     */
    public ResultUtil() {
    }

    public ResultUtil(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取到的数据
     */
    public static <T> ResultUtil<T> success(T data) {
        return new ResultUtil<>(200, "操作成功", data);
    }

    /**
     * 成功返回结果
     */
    public static <T> ResultUtil<T> success() {
        return new ResultUtil<>(200, "操作成功", null);
    }

    /**
     * 失败返回结果
     *
     * @param message 失败消息
     */
    public static <T> ResultUtil<T> error(String message) {
        return new ResultUtil<>(500, message, null);
    }

    /**
     * 失败返回结果
     *
     * @param code 错误码
     * @param message 失败消息
     */
    public static <T> ResultUtil<T> error(Integer code, String message) {
        return new ResultUtil<>(code, message, null);
    }

    /**
     * 获取状态码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置状态码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置数据
     */
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultVo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}