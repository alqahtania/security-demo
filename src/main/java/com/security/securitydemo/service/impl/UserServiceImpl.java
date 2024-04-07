package com.security.securitydemo.service.impl;

import com.security.securitydemo.entity.FidoCredentialEntity;
import com.security.securitydemo.entity.UserAccountEntity;
import com.security.securitydemo.model.FidoCredential;
import com.security.securitydemo.model.UserAccount;
import com.security.securitydemo.repository.UserAccountRepository;
import com.security.securitydemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAccount createOrFindUser(String displayName, String phone) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName can't be blank");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("phone can't be blank");
        }
        UserAccountEntity userAccountEntity =
                this.userAccountRepository
                        .findByPhone(phone)
                        .orElseGet(
                                () -> {
                                    UserAccountEntity result = new UserAccountEntity();
                                    result.setPhone(phone);
                                    result.setFullName(displayName);
                                    return this.userAccountRepository.save(result);
                                });

        return new UserAccount(
                userAccountEntity.getId(),
                userAccountEntity.getFullName(),
                userAccountEntity.getPhone(),
                Set.of());
    }

    @Override
    public Optional<UserAccount> findUserById(UUID userId) {
        return this.userAccountRepository.findById(userId).map(UserServiceImpl::toUserAccount);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<UserAccount> findUserPhone(String phone) {
        return this.userAccountRepository.findByPhone(phone).map(UserServiceImpl::toUserAccount);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCredential(FidoCredential fidoCredential) {
        FidoCredentialEntity fidoCredentialEntity = new FidoCredentialEntity();
        fidoCredentialEntity.setUserId(fidoCredential.userid());
        fidoCredentialEntity.setType(fidoCredential.keyType());
        fidoCredentialEntity.setPublicKeyCose(fidoCredential.publicKeyCose());
        fidoCredentialEntity.setId(fidoCredential.keyId());

        UserAccountEntity account =
                this.userAccountRepository
                        .findById(fidoCredential.userid())
                        .orElseThrow(
                                () -> new RuntimeException("can't add a credential to a user that does not exist"));
        account.getCredentials().add(fidoCredentialEntity);
    }

    @Override
    public Optional<FidoCredential> findCredentialById(String credentialId) {
        return Optional.empty();
    }

    private static UserAccount toUserAccount(UserAccountEntity accountEntity) {

        Set<FidoCredential> credentials =
                accountEntity.getCredentials().stream()
                        .map(
                                credential ->
                                        new FidoCredential(
                                                credential.getId(), credential.getType(), accountEntity.getId(), credential.getPublicKeyCose()))
                        .collect(Collectors.toSet());

        return new UserAccount(
                accountEntity.getId(), accountEntity.getFullName(), accountEntity.getPhone(), credentials);
    }
}
