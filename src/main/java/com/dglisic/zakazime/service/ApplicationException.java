package com.dglisic.zakazime.service;

import org.springframework.http.HttpStatus;

public class ApplicationException extends RuntimeException {
  private final HttpStatus responseStatus;

  public ApplicationException(String message, HttpStatus status) {
    super(message);
    this.responseStatus = status;
  }

  public HttpStatus getResponseStatus() {
    return responseStatus;
  }

}
