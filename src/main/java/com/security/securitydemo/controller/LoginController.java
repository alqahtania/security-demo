package com.security.securitydemo.controller;

import com.security.securitydemo.dto.request.LoginFinishRequest;
import com.security.securitydemo.dto.request.LoginStartRequest;
import com.security.securitydemo.dto.response.LoginStartResponse;
import com.security.securitydemo.service.LoginService;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.exception.AssertionFailedException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/v1/webauthn/login")
@RequiredArgsConstructor
public class LoginController {

    private final String START_LOGIN_REQUEST = "start_login_request";
    private final LoginService loginService;
    @ResponseBody
    @PostMapping("/start")
    LoginStartResponse loginStart(
            @RequestBody LoginStartRequest request, HttpSession session) {
        var response = this.loginService.startLogin(request);
        session.setAttribute(START_LOGIN_REQUEST, response.getAssertionRequest());
        return response;
    }

    @ResponseBody
    @PostMapping("/finish")
    AssertionResult loginFinish(@RequestBody LoginFinishRequest request, HttpSession session)
            throws AssertionFailedException {
        var assertionRequest = (AssertionRequest) session.getAttribute(START_LOGIN_REQUEST);
        if (assertionRequest == null) {
            throw new RuntimeException("Cloud Not find the original request");
        }

        var result = this.loginService.finishLogin(request);
        if (result.isSuccess()) {
            session.setAttribute(AssertionRequest.class.getName(), result);
        }
        return result;
    }
}
