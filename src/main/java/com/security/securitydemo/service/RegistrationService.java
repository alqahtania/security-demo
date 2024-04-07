package com.security.securitydemo.service;

import com.security.securitydemo.dto.request.RegistrationFinishRequest;
import com.security.securitydemo.dto.request.RegistrationStartRequest;
import com.security.securitydemo.dto.response.RegistrationFinishResponse;
import com.security.securitydemo.dto.response.RegistrationStartResponse;
import com.yubico.webauthn.exception.RegistrationFailedException;

public interface RegistrationService {

    RegistrationStartResponse startRegistration(RegistrationStartRequest startRequest);

    RegistrationFinishResponse finishRegistration(
            RegistrationFinishRequest finishRequest) throws RegistrationFailedException;
}
