package com.Klusterthon.Medbot.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GlobalException extends RuntimeException {
    private String message;
    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    public GlobalException(HttpStatus status, String msg)
    {
        super(msg);
        this.message = msg;
        this.status = status;
        timestamp = LocalDateTime.now();

    }


}
