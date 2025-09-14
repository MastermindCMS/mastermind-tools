package com.mastermindcms.services.contractverify;

public class ContractVerificationException extends Exception {
    public ContractVerificationException(String message) {
        super(message);
    }

    public ContractVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
