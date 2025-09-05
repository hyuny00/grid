package com.futechsoft.framework.exception;


public class AjaxResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;

    // 기본 생성자
    public AjaxResponse() {}

    // 전체 필드 생성자 (정적 메서드에서 사용)
    public AjaxResponse(boolean success, String message, T data, String errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    // Getter 메서드들
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // Setter 메서드들
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    // 정적 팩토리 메서드들
    public static <T> AjaxResponse<T> success(T data) {
        return new AjaxResponse<>(true, "Success", data, null);
    }

    public static <T> AjaxResponse<T> success(String message, T data) {
        return new AjaxResponse<>(true, message, data, null);
    }

    public static <T> AjaxResponse<T> error(String message, String errorCode) {
        return new AjaxResponse<>(false, message, null, errorCode);
    }
}