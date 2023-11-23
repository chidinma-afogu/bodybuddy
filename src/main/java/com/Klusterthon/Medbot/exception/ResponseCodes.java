package com.Klusterthon.Medbot.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum ResponseCodes {
    REQUEST_SUCCESSFUL("AI00", "Request Successful."),
    RESOURCE_NOT_FOUND("AE01", "Resource not found."),
    RESOURCE_ALREADY_EXIST("AE02", "Resource already exist."),
    BAD_INPUT_PARAM("AE03", "Bad input params."),
    GATEWAY_SERVICE_TIMEOUT("AE04", "Gateway Timeout, please try again."),
    SERVICE_UNAVAILABLE("AE05", "This service is currently unavailable."),
    INPUT_OUTPUT_ERROR("AE06", "Input output error."),
    BAD_GATEWAY("AE07", "Bad gateway."),
    SERVICE_NOT_IMPLEMENTED("AE08", "This service has not been implemented."),
    INTERNAL_SERVER_ERROR("AE09", "Internal server error."),
    UNAUTHENTICATED("AE12", "User can not be authenticated."),
    UNAUTHORIZED("AE13", "User is not authorized to perform this action."),
    ERROR_REFRESHING_TOKEN("AE14", "Error refreshing token."),
    VALIDATION_ERROR("AE15", "Validation error."),
    PASSWORD_MISMATCH("AE16", "Invalid old password."),
    NEWPASSWORD_EQUALS_OLDPASSWORD("AE17", "New password cannot be your previous password."),
    INVALID_SECURITY_ANSWER("AE18", "Invalid security answer."),
    TOKEN_WAS_NOT_RECOGNISED("AE19", "Token was not recognised"),
    INVALID_REFRESH_TOKEN("AE20", "Invalid refresh token"),
    USER_NOT_FOUND("AE21", "User not found"),
    ACCOUNT_HAS_BEEN_LOCKED("AE22", "User account has been locked"),
    ACCOUNT_HAS_BEEN_UNLOCKED("AE23", "User account has been unlocked"),
    BAD_CREDENTIALS("AE24", "Bad credentials"),
    FILE_UPLOAD_ERROR("AE25", "Error uploading file"),
    PAGE_ALREADY_EXIST("AE26", "Page Name already exist."),
    EXPIRED_LINK("AE27", "This confirmation link has expired. Please sign up again."),
    USER_EMAIL_ALREADY_EXIST("AE28", "User with this email already exist"),
    EXPIRED_OTP("AE29", "OTP has expired."),
    PASSWORD_PREVIOUSLY_BEEN_USED("AE30", "Password has previously been used.");

    private String code;
    private String message;
}