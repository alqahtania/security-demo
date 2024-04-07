package com.security.securitydemo.dto.request;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginFinishRequest {

    private String flowId;

    private PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs>
            credential;
}
