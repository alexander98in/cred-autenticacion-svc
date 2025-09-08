package co.com.pragma.autenticacion.api.common;

import java.time.OffsetDateTime;

public class ApiResponse<T> {

    private int Code;
    private String message;
    private T data;
    private String path;
    private OffsetDateTime timestamp;

    public ApiResponse() {
        this.timestamp = OffsetDateTime.now();
    }

    public ApiResponse(int Code, String message, T data, String path) {
        this.Code = Code;
        this.message = message;
        this.data = data;
        this.path = path;
        this.timestamp = OffsetDateTime.now();
    }

    public static <T> ApiResponse<T> of(int status, String message, T data, String path) {
        return new ApiResponse<>(status, message, data, path);
    }

    public int getCode() { return Code; }
    public void setCode(int code) { this.Code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}
