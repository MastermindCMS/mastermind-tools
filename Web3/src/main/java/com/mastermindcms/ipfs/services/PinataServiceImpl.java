package com.mastermindcms.ipfs.services;

import com.mastermindcms.ipfs.config.PinataConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class PinataServiceImpl implements IPFSService {

    @Autowired
    private PinataConfiguration configuration;

    private final RestTemplate restTemplate;

    public PinataServiceImpl() {
        this.restTemplate = new RestTemplate();
        // Configure RestTemplate to support multipart/form-data
        this.restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        this.restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }

    public String getApiKey() {
        return configuration.getApiKey();
    }

    public String getSecretApiKey() {
        return configuration.getSecretApiKey();
    }

    public String getBaseApiUrl() {
        return configuration.getBaseApiUrl();
    }

    public String getIpfsGatewayUrl() {
        return configuration.getIpfsGatewayUrl();
    }

    public String getBaseUrl() {
        return configuration.getBaseUrl();
    }


    @Override
    public String uploadFile(String content) throws Exception {
        String url = getBaseApiUrl() + "/pinning/pinFileToIPFS";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("pinata_api_key", getApiKey());
        headers.set("pinata_secret_api_key", getSecretApiKey());

        // Create the multipart body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "file.txt";
            }
        });

        // Wrap the body in the HttpEntity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send POST request to Pinata
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        String cid = extractCID(response.getBody());
        log.info("Generated IPFS CID: " + cid);
        return cid;
    }

    @Override
    public boolean deleteFile(String cid) throws Exception {
        String url = getBaseApiUrl() + "/pinning/unpin/" + cid;

        HttpHeaders headers = new HttpHeaders();
        headers.set("pinata_api_key", getApiKey());
        headers.set("pinata_secret_api_key", getSecretApiKey());

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                String.class
        );

        boolean isDeleted = response.getStatusCode().is2xxSuccessful();
        if (isDeleted) {
            log.info("CID {} deleted successfully.", cid);
        } else {
            log.error("Failed to delete CID {}. Status code: {}", cid, response.getStatusCode());
        }
        return isDeleted;
    }

    @Override
    public String readFile(String cid) throws Exception {
        String url = getIpfsGatewayUrl() + "/" + cid;

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new Exception("Failed to retrieve file with CID " + cid + ". Response: " + response.getBody());
        }
    }

    @Override
    public String getUrl(String cid) throws Exception {
        return getBaseUrl() + "/" + cid;
    }

    // Helper method to extract CID from Pinata API response
    private String extractCID(String responseBody) {
        String cid = null;
        if (responseBody != null && responseBody.contains("IpfsHash")) {
            int startIndex = responseBody.indexOf("IpfsHash") + 11;
            int endIndex = responseBody.indexOf("\"", startIndex);
            cid = responseBody.substring(startIndex, endIndex);
        }
        return cid;
    }
}
