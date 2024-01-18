package com.dglisic.zakazime.business.service.impl;

import com.dglisic.zakazime.business.service.ImageStorage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class ImageStorageImpl implements ImageStorage {
  private final static String IMAGE_DIRECTORY_ROOT = "C:\\Users\\dglisic\\personal-projects\\storage\\images\\";

  @Override
  public void storeImage(final String url, final MultipartFile file) throws IOException {
    final Path relativePath = Paths.get(url);
    final Path fullPath = Paths.get(IMAGE_DIRECTORY_ROOT).resolve(relativePath);
    Files.createDirectories(fullPath.getParent());
    Files.write(fullPath, file.getBytes(), StandardOpenOption.CREATE);
    log.debug("Stored image at relative path: " + relativePath);
  }

  @Override
  public byte[] getImage(final String url) throws IOException {
    final Path relativePath = Paths.get(url);
    final Path fullPath = Paths.get(IMAGE_DIRECTORY_ROOT).resolve(relativePath);
    return Files.readAllBytes(fullPath);
  }
}
