package com.Klusterthon.Medbot.dto.response;

import com.Klusterthon.Medbot.exception.ResponseCodes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ApiResponse {
    private String code;
    private String message;
    private Object data;
    @JsonIgnore
    private HttpStatus httpStatus;

    public static ApiResponse getSuccessfulResponse(Object data) {
        return ApiResponse.builder()
                .httpStatus(HttpStatus.OK)
                .code(ResponseCodes.REQUEST_SUCCESSFUL.getCode())
                .message(ResponseCodes.REQUEST_SUCCESSFUL.getMessage())
                .data(data)
                .build();
    }

    public static ApiResponse getSuccessfulResponse(String msg, Object data) {
        return ApiResponse.builder()
                .httpStatus(HttpStatus.OK)
                .code(ResponseCodes.REQUEST_SUCCESSFUL.getCode())
                .message(msg)
                .data(data)
                .build();
    }

    public static ApiResponse getSuccessfulResponse(String msg, Object data, HttpStatus httpStatus) {
        return ApiResponse.builder()
                .httpStatus(HttpStatus.valueOf(HttpStatus.OK.getReasonPhrase()))
                .code(ResponseCodes.REQUEST_SUCCESSFUL.getCode())
                .message(msg)
                .data(data)
                .build();
    }

    public static ApiResponse getFailedResponse(String code, String message, Object data) {
        return ApiResponse.builder()
                .httpStatus(HttpStatus.EXPECTATION_FAILED)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse getFailedResponse(String code, String message, Object data, HttpStatus httpStatus) {
        return ApiResponse.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse getErrorResponse(String code, String message) {
        return ApiResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static ApiResponse getConflictResponse(String code, String message) {
        return ApiResponse.builder()
                .code(code)
                .message(message)
                .build();
    }


}
