package com.mastermindcms.ipfs.services;

import com.mastermindcms.ipfs.config.Web3StorageConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Web3StorageServiceImpl implements IPFSService {

    @Autowired
    private Web3StorageConfiguration configuration;

    public String getApiKey() {
        return configuration.getApiKey();
    }

    public String getProof() {
        return configuration.getProof();
    }

    @Override
    public String uploadFile(String content) throws Exception {
        return null; //implemented only in JS
    }

    @Override
    public boolean deleteFile(String cid) throws Exception {
        return false; //not implemented
    }

    @Override
    public String readFile(String cid) throws Exception {
        return null; //not implemented
    }

    @Override
    public String getUrl(String cid) throws Exception {
        return null;
    }
}