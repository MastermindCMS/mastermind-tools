package com.mastermindcms.services.contractverify;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VerifyAPIRequestStatus {
    FAILURE("0"),
    SUCCESS("1");

    @Getter
    private final String statusCode;

    public static VerifyAPIRequestStatus fromStatusCode(String statusCode) {
        for (VerifyAPIRequestStatus status : values()) {
            if (status.statusCode.equals(statusCode)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + statusCode);
    }
}