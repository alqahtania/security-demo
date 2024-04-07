package com.security.securitydemo.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.security.securitydemo.dto.request.LoginFinishRequest;
import com.security.securitydemo.dto.request.LoginStartRequest;
import com.security.securitydemo.dto.response.LoginStartResponse;
import com.security.securitydemo.model.UserAccount;
import com.security.securitydemo.service.LoginService;
import com.security.securitydemo.service.UserService;
import com.security.securitydemo.service.WebauthnRedisService;
import com.yubico.webauthn.*;
import com.yubico.webauthn.exception.AssertionFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final RelyingParty relyingParty;
    private final UserService userService;
    private final WebauthnRedisService webauthnRedisService;

    @Override
    public LoginStartResponse startLogin(LoginStartRequest loginStartRequest) {
        // Find the user in the user database
        UserAccount user =
                this.userService
                        .findUserPhone(loginStartRequest.getPhone())
                        .orElseThrow(() -> new RuntimeException("Userid does not exist"));

        // make the assertion request to send to the client
        StartAssertionOptions options =
                StartAssertionOptions.builder()
                        .timeout(60_000)
                        .username(loginStartRequest.getPhone())
                        //     .userHandle(YubicoUtils.toByteArray(user.id()))
                        .build();
        AssertionRequest assertionRequest = this.relyingParty.startAssertion(options);

        LoginStartResponse loginStartResponse = new LoginStartResponse();
        loginStartResponse.setFlowId(UUID.randomUUID().toString());
        loginStartResponse.setAssertionRequest(assertionRequest);

        try {
            webauthnRedisService.saveAssertionOptions(loginStartResponse.getFlowId(), assertionRequest.toJson());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return loginStartResponse;
    }

    @Override
    public AssertionResult finishLogin(LoginFinishRequest loginFinishRequest) throws AssertionFailedException {

        var assertionRequestJson = webauthnRedisService.getAssertionOptions(loginFinishRequest.getFlowId());
        AssertionRequest assertionRequest = null;
        try {
            assertionRequest = AssertionRequest.fromJson(assertionRequestJson);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cloud not deserialize the assertion Request");
        }

        FinishAssertionOptions options =
                FinishAssertionOptions.builder()
                        .request(assertionRequest)
                        .response(loginFinishRequest.getCredential())
                        .build();

        AssertionResult assertionResult = this.relyingParty.finishAssertion(options);

        return assertionResult;
    }
}
