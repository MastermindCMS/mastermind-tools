package com.mastermindcms.nftmarketplace.config;

import com.mastermindcms.config.BlockchainNetwork;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:opensea.properties")
@ConfigurationProperties(prefix = "opensea")
@Data
public class OpenSeaConfiguration {

    private String sepoliaArbitrumApiUrl;

    private String arbitrumApiUrl;

    private String sepoliaArbitrumTokenUrl;

    private String arbitrumTokenUrl;

    private String collectionUrlTest;

    private String collectionUrlProd;

    private String apiKey;

    public String getApiUrl(String chain) {
        BlockchainNetwork openSeaChain = BlockchainNetwork.fromString(chain);
        switch (openSeaChain) {
            case SEPOLIA_ARBITRUM:
                return sepoliaArbitrumApiUrl;
            case ARBITRUM:
                return arbitrumApiUrl;
            default:
                throw new IllegalArgumentException("Unsupported chain: " + chain);
        }
    }

    public String getBaseCollectionUrl(String chain) {
        BlockchainNetwork openSeaChain = BlockchainNetwork.fromString(chain);
        switch (openSeaChain) {
            case SEPOLIA_ARBITRUM:
                return collectionUrlTest;
            case ARBITRUM:
                return collectionUrlProd;
            default:
                throw new IllegalArgumentException("Unsupported chain: " + chain);
        }
    }

    public String getBaseTokenUrl(String chain) {
        BlockchainNetwork openSeaChain = BlockchainNetwork.fromString(chain);
        switch (openSeaChain) {
            case SEPOLIA_ARBITRUM:
                return sepoliaArbitrumTokenUrl;
            case ARBITRUM:
                return arbitrumTokenUrl;
            default:
                throw new IllegalArgumentException("Unsupported chain: " + chain);
        }
    }
}
