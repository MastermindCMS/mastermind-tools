package com.mastermindcms.nftmarketplace.services;

public interface NFTMarketplaceService {

    boolean refreshMetadata(String contractAddress, String tokenId, String chain);

    String getCollectionUrl(String contractAddress, String chain);

    String getTokenUrl(String contractAddress, String tokenId, String chain);
}
