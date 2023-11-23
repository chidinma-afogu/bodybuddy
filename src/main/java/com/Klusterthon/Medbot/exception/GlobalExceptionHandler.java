package com.Klusterthon.Medbot.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{

    @ExceptionHandler(value = GlobalException.class)
    private ResponseEntity<Object> handleGlobalExceptions(GlobalException exception) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", exception.getMessage());
        body.put("timestamp", exception.getTimestamp());
        body.put("status",exception.getStatus());
        return new ResponseEntity(body,exception.getStatus());
    }
}
