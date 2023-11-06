package com.dglisic.zakazime.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {
  private final HttpStatus responseStatus;

  public ApplicationException(String message, HttpStatus status) {
    super(message);
    this.responseStatus = status;
  }

}
