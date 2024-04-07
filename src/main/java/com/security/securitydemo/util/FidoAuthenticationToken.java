package com.security.securitydemo.util;

import com.security.securitydemo.dto.request.LoginFinishRequest;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class FidoAuthenticationToken extends AbstractAuthenticationToken {
  private final String username;
  private final LoginFinishRequest loginFinishRequest;

  public FidoAuthenticationToken(String username, LoginFinishRequest loginFinishRequest) {
    super(null);
    this.username = username;
    this.loginFinishRequest = loginFinishRequest;
  }

  @Override
  public Object getCredentials() {
    return loginFinishRequest;
  }

  @Override
  public Object getPrincipal() {
    return username;
  }
}
