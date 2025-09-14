package com.mastermindcms.services;

import com.mastermindcms.config.Web3Configuration;
import com.mastermindcms.services.contractverify.ContractVerification;
import com.mastermindcms.services.contractverify.ContractVerificationException;
import com.mastermindcms.services.contractverify.ContractVerificationRequest;
import com.mastermindcms.services.contractverify.ContractVerificationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Slf4j
@Service
public class Web3ServiceImpl implements Web3Service {

    @Autowired
    private Web3Configuration configuration;

    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public String getBlockchainNetwork() {
        return configuration.getBlockchainNetwork();
    }

    @Override
    public String getScanUrl(String contractAddress, String chain) {
        String baseTokenUrl = configuration.getScanUrl(chain);
        return String.format("%s/%s", baseTokenUrl, contractAddress);
    }

    @Override
    public String getApiUrl(String chain) {
        return configuration.getApiUrl(chain);
    }

    @Override
    public String getApiKey(String chain) {
        return configuration.getApiKey(chain);
    }

    @Override
    public String getCompilerVersionsUrl(String chain) {
        return configuration.getCompilerVersionsUrl(chain);
    }

    @Override
    public boolean verifyContract(LinkedHashMap<String, Object> requestMap) throws ContractVerificationException {
        ContractVerificationRequest request =  mapper.convertValue(requestMap, ContractVerificationRequest.class);
        request.setApiUrl(getApiUrl(request.getChainName()));
        request.setApiKey(getApiKey(request.getChainName()));
        request.setCompilerVersionsUrl(getCompilerVersionsUrl(request.getChainName()));

        ContractVerification contractVerification = new ContractVerification();
        ContractVerificationResponse cvResp =  contractVerification.verifyContract(request);
        return cvResp.isSuccess();
    }
}
