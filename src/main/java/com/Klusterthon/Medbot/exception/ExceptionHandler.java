package com.Klusterthon.Medbot.exception;

import com.Klusterthon.Medbot.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import static com.Klusterthon.Medbot.exception.ResponseCodes.*;


@ControllerAdvice
@Slf4j
public class ExceptionHandler extends ResponseEntityExceptionHandler {
    public static final String LOGGER_STRING_GET = "url -> {} response -> {}";

    @org.springframework.web.bind.annotation.ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {

        String message = ex.getMessage();
        String code = ResponseCodes.INTERNAL_SERVER_ERROR.getCode();
        HttpStatus httpStatus = HttpStatus.EXPECTATION_FAILED;
        if(ex.getMostSpecificCause().toString().contains("page_name")){
            message = "Page Name Required and Must be Unique, Try a Different Page Name";
            httpStatus = HttpStatus.BAD_REQUEST;
            code = PAGE_ALREADY_EXIST.getCode();
        }

        ApiResponse res = ApiResponse.getFailedResponse(code,  message, null);
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, httpStatus);
    }


    @org.springframework.web.bind.annotation.ExceptionHandler(value = HttpClientErrorException.NotFound.class)
    public ResponseEntity<?> handleHttpClientErrorExceptionNotFound(HttpClientErrorException.NotFound ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getFailedResponse(RESOURCE_NOT_FOUND.getCode(),  ex.getMessage(), null);
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = CustomException.class)
    public ResponseEntity<?> handleHttpCustomException(CustomException ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getFailedResponse(BAD_GATEWAY.getMessage(),  ex.getMessage().replace("[","").replace("]",""), null);
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, ex.getStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = HttpClientErrorException.BadRequest.class)
    public ResponseEntity<?> handleHttpClientErrorException(HttpClientErrorException.BadRequest ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getFailedResponse(BAD_INPUT_PARAM.getCode(), ex.getMessage(), null);
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<?> handleHttpClientErrorExceptionUnAuthorized(HttpClientErrorException.Unauthorized ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getFailedResponse(UNAUTHORIZED.getCode(), ex.getMessage(), null);
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = HttpClientErrorException.class)
    public ResponseEntity<?> handleHttpClientErrorException(HttpClientErrorException ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getErrorResponse(BAD_INPUT_PARAM.getCode(), ex.getMessage());
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = HttpServerErrorException.GatewayTimeout.class)
    public ResponseEntity<?> handleHttpServerErrorExceptionGatewayTimeout(HttpServerErrorException.GatewayTimeout ex, HttpServletRequest request) {
//        String provider = String.valueOf(request.getAttribute("provider"));
        ApiResponse res = ApiResponse.getFailedResponse(GATEWAY_SERVICE_TIMEOUT.getCode(), ex.getMessage(), null);
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, HttpStatus.GATEWAY_TIMEOUT);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = HttpServerErrorException.BadGateway.class)
    public ResponseEntity<?> handleHttpServerErrorExceptionBadGateway(HttpServerErrorException.BadGateway ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getErrorResponse(BAD_GATEWAY.getMessage(),  ex.getMessage());
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, HttpStatus.BAD_GATEWAY);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = HttpServerErrorException.NotImplemented.class)
    public ResponseEntity<?> handleHttpServerErrorExceptionBadGateway(HttpServerErrorException.NotImplemented ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getErrorResponse(SERVICE_NOT_IMPLEMENTED.getCode(), ex.getMessage());
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, HttpStatus.NOT_IMPLEMENTED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<?> handleHttpServerErrorExceptionInternalServerError(HttpServerErrorException.InternalServerError ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getErrorResponse(ResponseCodes.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage());
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = ConnectException.class)
    public ResponseEntity<?> handleConnectException(ConnectException ex, HttpServletRequest request) {
        String provider = request.getAttribute("provider").toString();
        ApiResponse res = ApiResponse.getFailedResponse(GATEWAY_SERVICE_TIMEOUT.getCode(), ex.getMessage(), null);
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = IOException.class)
    public ResponseEntity<?> handleIOException(IOException ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getErrorResponse(ResponseCodes.INPUT_OUTPUT_ERROR.getCode(), ex.getMessage());
        log.error(LOGGER_STRING_GET,  request.getRequestURL() , res);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @org.springframework.web.bind.annotation.ExceptionHandler(value = NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getErrorResponse(RESOURCE_NOT_FOUND.getCode(), ex.getMessage());
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, HttpStatus.EXPECTATION_FAILED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        if(ex.getMessage().equalsIgnoreCase("Access is denied")) {
            ApiResponse res = ApiResponse.getErrorResponse(String.valueOf(HttpStatus.FORBIDDEN.value()), ex.getMessage());
            log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
            return new ResponseEntity<>(res, HttpStatus.FORBIDDEN);
        }
        ApiResponse res = ApiResponse.getErrorResponse(ResponseCodes.INTERNAL_SERVER_ERROR.getCode(),  ex.getMessage());
        log.error(LOGGER_STRING_GET,  request.getRequestURL(), res);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = UnknownHostException.class)
    public ResponseEntity<?> handleUnknownHostException(UnknownHostException ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getErrorResponse(SERVICE_UNAVAILABLE.getCode(), ex.getMessage());
        log.error(LOGGER_STRING_GET, request.getRequestURL() , res);
        return new ResponseEntity<>(res, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getFailedResponse(RESOURCE_NOT_FOUND.getCode(), ex.getMessage(), null);
        log.warn(LOGGER_STRING_GET, request.getRequestURL() , res);
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = ResourceAlreadyExistException.class)
    public ResponseEntity<?> handleResourceAlreadyExistException(ResourceAlreadyExistException ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getFailedResponse(RESOURCE_ALREADY_EXIST.getCode(), ex.getMessage(), null);
        log.warn(LOGGER_STRING_GET, request.getRequestURL() , res);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = UnauthException.class)
    public ResponseEntity<?> handleUnauthException(UnauthException ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getFailedResponse(UNAUTHORIZED.getCode(), ex.getMessage(), null);
        log.warn(LOGGER_STRING_GET, request.getRequestURL() , res);
        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<?> handlNullPointerException(NullPointerException ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getFailedResponse(INTERNAL_SERVER_ERROR.getCode(), ex.getMessage(), null);
        log.warn(LOGGER_STRING_GET, request.getRequestURL() , res);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        ApiResponse res = ApiResponse.getFailedResponse("AE90", ex.getMessage(), null);
        log.warn(LOGGER_STRING_GET, request.getRequestURL() , res);
        return new ResponseEntity<>(res, HttpStatus.EXPECTATION_FAILED);
    }

//    @Override
//    protected ResponseEntity<Object> handleHttpMessageNotReadable(
//            HttpMessageNotReadableException ex, HttpHeaders headers,
//            HttpStatus status, WebRequest request) {
//        ApiResponse res = ApiResponse.getFailedResponse(VALIDATION_ERROR.getCode(),  ex.getLocalizedMessage(), null);
//        log.warn(LOGGER_STRING_GET, null, res);
//        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
//    }
}
