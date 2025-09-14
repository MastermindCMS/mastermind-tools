package com.mastermindcms.modules.filestorage.beans.devexpress;

public class DeResponseBody<T> {

    private boolean success;
    private String errorId;
    private T result;

    public DeResponseBody() {
    }

    public DeResponseBody(T result) {
        this.success = true;
        this.errorId = null;
        this.result = result;
    }

    public DeResponseBody(boolean success, String errorId, T result) {
        this.success = success;
        this.errorId = errorId;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
    
    public DeResponseBody<T> success(boolean success) {
        this.success = success;
        return this;
    }

    public DeResponseBody<T> errorId(String errorId) {
        this.errorId = errorId;
        return this;
    }

    public DeResponseBody<T> result(T result) {
        this.result = result;
        return this;
    }

}
