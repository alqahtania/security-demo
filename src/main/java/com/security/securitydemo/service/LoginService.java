package com.security.securitydemo.service;

import com.security.securitydemo.dto.request.LoginFinishRequest;
import com.security.securitydemo.dto.request.LoginStartRequest;
import com.security.securitydemo.dto.response.LoginStartResponse;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.exception.AssertionFailedException;

public interface LoginService {

    LoginStartResponse startLogin(LoginStartRequest loginStartRequest);
    AssertionResult finishLogin(LoginFinishRequest loginFinishRequest) throws AssertionFailedException;
}
