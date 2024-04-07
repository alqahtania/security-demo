package com.security.securitydemo.controller;

import com.security.securitydemo.dto.request.RegistrationFinishRequest;
import com.security.securitydemo.dto.request.RegistrationStartRequest;
import com.security.securitydemo.dto.response.RegistrationFinishResponse;
import com.security.securitydemo.dto.response.RegistrationStartResponse;
import com.security.securitydemo.service.RegistrationService;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/v1/webauthn/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;



    @ResponseBody
    @PostMapping("/start")
    RegistrationStartResponse startRegistration(
            @RequestBody RegistrationStartRequest request) {
        return this.registrationService.startRegistration(request);
    }

    @ResponseBody
    @PostMapping("/finish")
    RegistrationFinishResponse finishRegistration(
            @RequestBody RegistrationFinishRequest request)
            throws RegistrationFailedException {
        return this.registrationService.finishRegistration(
                request);
    }
}
