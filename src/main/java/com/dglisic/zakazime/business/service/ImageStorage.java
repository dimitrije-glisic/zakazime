package com.dglisic.zakazime.business.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {
  void storeImage(final @NotBlank String url, @NotNull final MultipartFile file) throws IOException;
  byte[] getImage(final @NotNull String url) throws IOException;
}
