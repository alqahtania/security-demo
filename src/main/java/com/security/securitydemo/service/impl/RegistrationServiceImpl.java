package com.security.securitydemo.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.security.securitydemo.dto.request.RegistrationFinishRequest;
import com.security.securitydemo.dto.request.RegistrationStartRequest;
import com.security.securitydemo.dto.response.RegistrationFinishResponse;
import com.security.securitydemo.dto.response.RegistrationStartResponse;
import com.security.securitydemo.model.FidoCredential;
import com.security.securitydemo.model.UserAccount;
import com.security.securitydemo.service.RegistrationService;
import com.security.securitydemo.service.UserService;
import com.security.securitydemo.service.WebauthnRedisService;
import com.security.securitydemo.util.YubicoUtils;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;
    private final RelyingParty relyingParty;
    private final WebauthnRedisService redisService;

    @Override
    public RegistrationStartResponse startRegistration(RegistrationStartRequest startRequest) {
        UserAccount user =
                this.userService.createOrFindUser(startRequest.getFullName(), startRequest.getPhone());
        PublicKeyCredentialCreationOptions options = createPublicKeyCredentialCreationOptions(user);
        RegistrationStartResponse creationResponse = createRegistrationStartResponse(options);
        try {
            this.redisService.saveAssertionOptions(creationResponse.getFlowId().toString(), options.toJson());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cloud Not parse PublicKeyCredentialCreationOptions to JSON");
        }
        return creationResponse;
    }

    private PublicKeyCredentialCreationOptions createPublicKeyCredentialCreationOptions(
            UserAccount user) {
        var userIdentity =
                UserIdentity.builder()
                        .name(user.phone())
                        .displayName(user.displayName())
                        .id(YubicoUtils.toByteArray(user.id()))
                        .build();

        var authenticatorSelectionCriteria =
                AuthenticatorSelectionCriteria.builder()
                        .userVerification(UserVerificationRequirement.DISCOURAGED)
                        .build();

        var startRegistrationOptions =
                StartRegistrationOptions.builder()
                        .user(userIdentity)
                        .timeout(30_000)
                        .authenticatorSelection(authenticatorSelectionCriteria)
                        .build();

        PublicKeyCredentialCreationOptions options =
                this.relyingParty.startRegistration(startRegistrationOptions);

        return options;
    }

    private RegistrationStartResponse createRegistrationStartResponse(
            PublicKeyCredentialCreationOptions options) {
        RegistrationStartResponse startResponse = new RegistrationStartResponse();
        startResponse.setFlowId(UUID.randomUUID());
        startResponse.setCredentialCreationOptions(options);
        return startResponse;
    }


    @Override
    public RegistrationFinishResponse finishRegistration(RegistrationFinishRequest finishRequest) throws RegistrationFailedException {
        PublicKeyCredentialCreationOptions credentialCreationOptions;
        try {
            String creationOptionsJson = this.redisService.getAssertionOptions(finishRequest.getFlowId().toString());
            credentialCreationOptions = PublicKeyCredentialCreationOptions.fromJson(creationOptionsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cloud Not find the original request");
        }
        FinishRegistrationOptions options =
                FinishRegistrationOptions.builder()
                        .request(credentialCreationOptions)
                        .response(finishRequest.getCredential())
                        .build();
        RegistrationResult registrationResult = this.relyingParty.finishRegistration(options);

        var fidoCredential =
                new FidoCredential(
                        registrationResult.getKeyId().getId().getBase64Url(),
                        registrationResult.getKeyId().getType().name(),
                        YubicoUtils.toUUID(credentialCreationOptions.getUser().getId()),
                        registrationResult.getPublicKeyCose().getBase64Url());

        this.userService.addCredential(fidoCredential);

        RegistrationFinishResponse registrationFinishResponse = new RegistrationFinishResponse();
        registrationFinishResponse.setFlowId(finishRequest.getFlowId());
        registrationFinishResponse.setRegistrationComplete(true);

        return registrationFinishResponse;
    }
}
