package com.security.securitydemo.dto.response;

import com.yubico.webauthn.AssertionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginStartResponse {

  private String flowId;

  private AssertionRequest assertionRequest;

}
