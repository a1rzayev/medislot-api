package com.medislot.medislot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String correlationId;
    private List<String> errors;

    public ErrorResponse() {
        this.timestamp = OffsetDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(int status, String error, String message, String path, List<String> errors) {
        this(status, error, message, path);
        this.errors = errors;
    }

    public ErrorResponse(int status, String error, String message, String path, String correlationId) {
        this(status, error, message, path);
        this.correlationId = correlationId;
    }

    public ErrorResponse(int status, String error, String message, String path, String correlationId, List<String> errors) {
        this(status, error, message, path, correlationId);
        this.errors = errors;
    }

    // Backward compatibility constructors
    public ErrorResponse(String message) {
        this();
        this.message = message;
    }

    public ErrorResponse(String message, String error) {
        this();
        this.message = message;
        this.error = error;
    }

    public ErrorResponse(String message, List<String> errors) {
        this();
        this.message = message;
        this.errors = errors;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
