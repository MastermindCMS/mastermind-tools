package com.mastermindcms.config;

public enum BlockchainNetwork {
    SEPOLIA_ARBITRUM("sepoliaArbitrum"),
    ARBITRUM("arbitrum");

    private final String chainName;

    BlockchainNetwork(String chainName) {
        this.chainName = chainName;
    }

    public String getChainName() {
        return chainName;
    }

    public static BlockchainNetwork fromString(String chain) {
        for (BlockchainNetwork openSeaChain : BlockchainNetwork.values()) {
            if (openSeaChain.chainName.equalsIgnoreCase(chain)) {
                return openSeaChain;
            }
        }
        throw new IllegalArgumentException("Unsupported chain: " + chain);
    }
}
