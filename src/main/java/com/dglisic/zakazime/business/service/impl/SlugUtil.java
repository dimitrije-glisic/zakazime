package com.dglisic.zakazime.business.service.impl;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SlugUtil {
  public static String fromTitle(String title) {
    return title.toLowerCase().replace(" ", "-");
  }
}
