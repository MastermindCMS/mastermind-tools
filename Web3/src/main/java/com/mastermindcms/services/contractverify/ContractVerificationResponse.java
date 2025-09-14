package com.mastermindcms.services.contractverify;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response object for contract verification
 */
@Getter
@AllArgsConstructor
public class ContractVerificationResponse {
    private final boolean success;
    private final String message;
}
