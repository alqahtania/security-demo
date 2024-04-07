package com.security.securitydemo.dto.response;

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationStartResponse implements Serializable {
    private UUID flowId;
    private PublicKeyCredentialCreationOptions credentialCreationOptions;
}
