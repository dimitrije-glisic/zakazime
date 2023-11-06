package com.dglisic.zakazime.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalRestExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);

  @ExceptionHandler(ApplicationException.class)
  @ResponseBody
  public ResponseEntity<ErrorDTO> handleApplicationException(ApplicationException ex) {
    logger.warn("Application exception occurred: {}", ex.getMessage());
    return ResponseEntity.status(ex.getResponseStatus()).body(new ErrorDTO(ex.getMessage()));
  }

}
