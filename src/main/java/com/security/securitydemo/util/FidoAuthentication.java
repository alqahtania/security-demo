package com.security.securitydemo.util;

import com.security.securitydemo.dto.request.LoginFinishRequest;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Set;

@Getter
public class FidoAuthentication extends AbstractAuthenticationToken {
  private final String username;
  private final LoginFinishRequest loginFinishRequest;
  private final String assertionResultJson;

  public FidoAuthentication(
      FidoAuthenticationToken fidoAuthenticationToken, String assertionResultJson) {
    super(Set.of());
    this.username = fidoAuthenticationToken.getUsername();
    this.loginFinishRequest = fidoAuthenticationToken.getLoginFinishRequest();
    this.assertionResultJson = assertionResultJson;
    this.setAuthenticated(true);
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
