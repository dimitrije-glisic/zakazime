package com.dglisic.zakazime.controller;

import com.dglisic.zakazime.service.ApplicationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalRestExceptionHandler {

  @ExceptionHandler(ApplicationException.class)
  @ResponseBody
  public ResponseEntity<ErrorDTO> handleApplicationException(ApplicationException ex) {
    return ResponseEntity.status(ex.getResponseStatus()).body(new ErrorDTO(ex.getMessage()));
  }

}