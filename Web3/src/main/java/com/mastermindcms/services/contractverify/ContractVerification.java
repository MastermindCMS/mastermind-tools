package com.mastermindcms.services.contractverify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.web3j.abi.DefaultFunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ContractVerification {
    private final RestTemplate restTemplate;

    public ContractVerification() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        this.restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }

    public ContractVerificationResponse verifyContract(ContractVerificationRequest request)
            throws ContractVerificationException {
        String sourceCode = fetchSourceCode(request.getContractCodeUri());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("apikey", request.getApiKey());
        formData.add("module", "contract");
        formData.add("action", "verifysourcecode");
        formData.add("sourceCode", sourceCode);
        formData.add("contractaddress", request.getContractAddress());
        formData.add("codeformat", "solidity-single-file");
        formData.add("contractname", extractContractName(sourceCode));

        String version = extractCompilerVersion(sourceCode);  // gets eg "0.8.20"
        String fullVersion = getFullCompilerVersion(version, request.getCompilerVersionsUrl()); // gets then "v0.8.20+commit.a1b79de6"
        formData.add("compilerversion", fullVersion);

        formData.add("optimizationUsed", "1");
        formData.add("runs", "200");
        formData.add("constructorArguements", encodeConstructorArguments(request.getContractName(),
                request.getContractSymbol(), request.getOwnerAddress(), Long.valueOf(request.getMaxNft()),
                request.getContractUri()));
        formData.add("evmversion", "paris");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    request.getApiUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            JsonNode result = new ObjectMapper().readTree(response.getBody());

            log.info("Requesting contract verification. Contract name {}, Address {}",
                    request.getContractName(), request.getContractAddress());
            String statusCode = result.get("status").asText();
            String verificationResult = result.get("result").asText();
            VerifyAPIRequestStatus requestStatus = VerifyAPIRequestStatus.fromStatusCode(statusCode);
            if (requestStatus == VerifyAPIRequestStatus.SUCCESS) {
                log.info("Contract verification successfully requested. Guid: {}", verificationResult);

                /*we decided not verify the status of the contractVerification, but just expect it to be executed
                somewhere in future
                boolean verified = checkVerificationStatus(request.getApiUrl(), request.getApiKey(), guid);
                */
                return new ContractVerificationResponse(true, "Contract verification requested successfully");
            }
            log.info("Contract verification request failed. Reason: {}", verificationResult);
            return new ContractVerificationResponse(false, "Verification verification request failed." +
                    "Reason: " + verificationResult);
        } catch (Exception e) {
            log.error("Contract verification failed", e);
            throw new ContractVerificationException("Contract verification failed", e);
        }
    }

    private boolean checkVerificationStatus(String apiUrl, String apiKey, String guid) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("apikey", apiKey);
        params.add("module", "contract");
        params.add("action", "checkverifystatus");
        params.add("guid", guid);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParams(params);

        Thread.sleep(5000); // Wait 5 seconds between checks

        ResponseEntity<String> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        JsonNode result = new ObjectMapper().readTree(response.getBody());

        String status = result.get("result").asText();
        log.info("Verification status: {}", status);

        if ("Pass - Verified".equals(status) || "Already Verified".equals(status)) {
            return true;
        }

        return false;
    }

    private String encodeConstructorArguments(
            String contractName,
            String contractSymbol,
            String ownerAddress,
            Long maxNft,
            String contractUri
    ) throws ContractVerificationException {
        try {
            DefaultFunctionEncoder encoder = new DefaultFunctionEncoder();

            List<Type> params = Arrays.asList(
                    new Utf8String(contractName),
                    new Utf8String(contractSymbol),
                    new Address(160, ownerAddress),  // Address needs bit length
                    new Uint256(BigInteger.valueOf(maxNft)),
                    new Utf8String(contractUri)
            );
            String encodedParams = encoder.encodeParameters(params);
            return encodedParams.startsWith("0x") ? encodedParams.substring(2) : encodedParams;

        } catch (Exception e) {
            throw new ContractVerificationException("Failed to encode constructor arguments", e);
        }
    }

    private String extractContractName(String sourceCode) throws ContractVerificationException {
        Pattern pattern = Pattern.compile("^\\s*contract (\\w+) is", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(sourceCode);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new ContractVerificationException("Could not find contract name in source code");
    }

    private String extractCompilerVersion(String sourceCode) throws ContractVerificationException {
        Pattern pattern = Pattern.compile("pragma solidity \\^?(\\d+\\.\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(sourceCode);
        if (matcher.find()) {
            return "v" + matcher.group(1);
        }
        throw new ContractVerificationException("Could not find compiler version in source code");
    }

    private String getFullCompilerVersion(String version, String compilerVersionsUrl) throws ContractVerificationException {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(compilerVersionsUrl, String.class);
            String html = response.getBody();

            if (html == null || html.isEmpty()) {
                throw new ContractVerificationException("Empty response received from: " + compilerVersionsUrl);
            }
            // Look for pattern like: v0.8.20+commit.a1b79de6 while excluding nightly versions
            Pattern pattern = Pattern.compile(version + "\\+commit\\.[a-f0-9]+(?!-nightly)");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                return matcher.group(); // Extracts the full version string
            }
            throw new ContractVerificationException("Could not find full compiler version for: " + version);
        } catch (Exception e) {
            throw new ContractVerificationException("Failed to get compiler version info", e);
        }
    }

    private String fetchSourceCode(String contractCodeURI) throws ContractVerificationException {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(contractCodeURI, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ContractVerificationException("Failed to fetch contract source code: " + response.getStatusCode());
            }
            return response.getBody();
        } catch (Exception e) {
            throw new ContractVerificationException("Error fetching contract source code", e);
        }
    }
}
