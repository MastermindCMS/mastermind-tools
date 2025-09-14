package com.mastermindcms.services.contractverify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractVerificationRequest {
    private String contractAddress;
    private String contractName;
    private String contractSymbol;
    private String ownerAddress;
    private String maxNft;
    private String contractUri;
    private String contractCodeUri;
    private String chainName;
    private String apiUrl;
    private String apiKey;
    private String compilerVersionsUrl;
}
