package com.mastermindcms.services;

import com.mastermindcms.services.contractverify.ContractVerificationException;

import java.util.LinkedHashMap;

public interface Web3Service {

    String getScanUrl(String contractAddress, String chain);

    String getApiUrl(String chain);

    String getApiKey(String chain);

    String getCompilerVersionsUrl(String chain);

    /**
     * Verifies a smart contract on the block explorer
     * @param requestMap Map of contract parameters
     * @return VerificationResponse containing the verification result
     */
    boolean verifyContract(
            LinkedHashMap<String, Object> requestMap
    ) throws ContractVerificationException;

}
