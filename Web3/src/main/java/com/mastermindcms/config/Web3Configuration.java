package com.mastermindcms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:web3.properties")
@ConfigurationProperties(prefix = "web3")
@Data
public class Web3Configuration {

    private String blockchainNetwork;

    private String sepoliaArbitrumScanUrl;

    private String arbitrumScanUrl;

    private String sepoliaArbitrumApiUrl;

    private String arbitrumApiUrl;

    private String arbiscanApiKey;

    private String arbiscanCompileVersionsUrl;

    public String getScanUrl(String chain) {
        BlockchainNetwork openSeaChain = BlockchainNetwork.fromString(chain);
        switch (openSeaChain) {
            case SEPOLIA_ARBITRUM:
                return sepoliaArbitrumScanUrl;
            case ARBITRUM:
                return arbitrumScanUrl;
            default:
                throw new IllegalArgumentException("Unsupported chain: " + chain);
        }
    }

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

    public String getApiKey(String chain) {
        BlockchainNetwork openSeaChain = BlockchainNetwork.fromString(chain);
        switch (openSeaChain) {
            case SEPOLIA_ARBITRUM:
            case ARBITRUM:
                return arbiscanApiKey;
            default:
                throw new IllegalArgumentException("Unsupported chain: " + chain);
        }
    }

    public String getCompilerVersionsUrl(String chain) {
        BlockchainNetwork openSeaChain = BlockchainNetwork.fromString(chain);
        switch (openSeaChain) {
            case SEPOLIA_ARBITRUM:
            case ARBITRUM:
                return arbiscanCompileVersionsUrl;
            default:
                throw new IllegalArgumentException("Unsupported chain: " + chain);
        }
    }
}
