package com.mastermindcms.ipfs.services;

public interface IPFSService {

    String uploadFile(String content) throws Exception;

    boolean deleteFile(String cid) throws Exception;

    String readFile(String cid) throws Exception;

    String getUrl(String cid) throws Exception;
}
