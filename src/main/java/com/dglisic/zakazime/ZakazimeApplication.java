package com.dglisic.zakazime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ZakazimeApplication {

  public static void main(String[] args) {
    SpringApplication.run(ZakazimeApplication.class, args);
  }


  //a workaround for the CSRF token issue
  @PostMapping("/dummy-post")
  public void csrfPost() {
    // do nothing
  }

}
