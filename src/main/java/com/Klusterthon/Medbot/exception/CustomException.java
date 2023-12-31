package com.Klusterthon.Medbot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * CustomException
 */
@Getter
public class CustomException extends RuntimeException {
  /**
   * For serialization: if any changes is made to this class, update the
   * serialversionID
   */
  private static final long serialVersionUID = 1L;

  private String message;
  private HttpStatus status;

  public CustomException(String message, HttpStatus status) {
    this.message = message;
    this.status = status;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

}
