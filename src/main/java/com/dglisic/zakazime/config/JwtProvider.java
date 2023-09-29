package com.dglisic.zakazime.config;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dglisic.zakazime.service.UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

import jakarta.annotation.PostConstruct;
import model.tables.records.AccountsRecord;

@Component
public class JwtProvider {
  private static final String ISSUER = "zakazime";
  private static final String EMAIL_CLAIM = "email";
  private static final int EXPIRATION_HOURS = 10;

  @Value("${jwt.secret-key}")
  private String secretKey;

  private final UserService userService;

  public JwtProvider(UserService userService) {
    this.userService = userService;
  }

  @PostConstruct
  public void init() {
    this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String generateToken(String email) {
    return JWT.create().
            withIssuer(ISSUER)
            .withClaim(EMAIL_CLAIM, email)
            .withIssuedAt(new Date(System.currentTimeMillis()))
//             token is valid for 10 hours
            .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * EXPIRATION_HOURS))
            .sign(HMAC256(secretKey));
  }

  public Authentication validateToken(String token) {
    DecodedJWT decodedJWT = JWT.require(HMAC256(secretKey)).build().verify(token);
    String email = decodedJWT.getClaim(EMAIL_CLAIM).asString();
    AccountsRecord userByEmail = userService.findUserByEmailOrElseThrow(email);
    return new UsernamePasswordAuthenticationToken(userByEmail, null, null);
  }

}
