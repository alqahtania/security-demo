package com.security.securitydemo.model;

import java.util.Set;
import java.util.UUID;

public record UserAccount(UUID id, String displayName, String phone, Set<FidoCredential> credentials) {}
