package com.security.securitydemo.service;


import com.security.securitydemo.model.FidoCredential;
import com.security.securitydemo.model.UserAccount;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserAccount createOrFindUser(String displayName, String phone);
    Optional<UserAccount> findUserById(UUID userId);

    Optional<UserAccount> findUserPhone(String phone);

    void addCredential(FidoCredential fidoCredential);

    Optional<FidoCredential> findCredentialById(String credentialId);
}
