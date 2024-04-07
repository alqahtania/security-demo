package com.security.securitydemo.dto.request;

import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationFinishRequest {

    private UUID flowId;

    private PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs>
            credential;
}
