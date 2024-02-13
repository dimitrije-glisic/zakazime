package com.dglisic.zakazime.business.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImageType {
  PROFILE("profile"),
  GALLERY("gallery");

  private final String value;

}
