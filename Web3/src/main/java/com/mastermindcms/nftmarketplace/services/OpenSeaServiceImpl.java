package com.mastermindcms.nftmarketplace.services;

import com.mastermindcms.nftmarketplace.config.OpenSeaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OpenSeaServiceImpl implements NFTMarketplaceService {

    private static final Logger log = LoggerFactory.getLogger(OpenSeaServiceImpl.class);

    @Autowired
    private OpenSeaConfiguration configuration;

    private final RestTemplate restTemplate;

    public OpenSeaServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    public String getApiKey() {
        return configuration.getApiKey();
    }

    public String getApiUrl(String chain) {
        return configuration.getApiUrl(chain);
    }

    @Override
    public boolean refreshMetadata(String contractAddress, String tokenId, String chain) {
        String url = String.format("%s/contract/%s/nfts/%s/refresh", getApiUrl(chain), contractAddress, tokenId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + getApiKey());

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info(String.format("Successfully queued metadata refresh for contract %s and token_id %s", contractAddress, tokenId));
                return true;
            } else {
                log.error(String.format("Failed to refresh metadata for token %s: %s", tokenId, response.getStatusCode()));
                return false;
            }
        } catch (Exception e) {
            log.error("Error refreshing metadata: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getCollectionUrl(String contractAddress, String chain) {
        String apiUrl = getApiUrl(chain);
        String collectionBaseUrl = configuration.getBaseCollectionUrl(chain);
        String url = String.format("%s/contract/%s", apiUrl, contractAddress);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Bearer " + getApiKey());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                String slug = (String) responseBody.get("collection");

                if (slug != null) {
                    return String.format("%s/collection/%s", collectionBaseUrl, slug);
                } else {
                    log.warn("Collection slug not found for contract address: " + contractAddress);
                }
            } else {
                log.error("Failed to retrieve collection information: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error fetching collection URI: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getTokenUrl(String contractAddress, String tokenId, String chain) {
        String baseTokenUrl = configuration.getBaseTokenUrl(chain);
        return String.format("%s/%s/%s", baseTokenUrl, contractAddress, tokenId);
    }


}
