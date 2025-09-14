package com.mastermindcms.modules.filestorage.services;

import com.mastermindcms.modules.filestorage.helpers.FileManagerHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

@Service
public class FileManagerServiceImpl implements FileManagerService {

    private final static Logger logger = LoggerFactory.getLogger(FileManagerServiceImpl.class);

    private final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public void createDirectory(Path path) {
        try {
            FileManagerHelper.createDirectory(path);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public List<File> findByPath(Path path) {
        return FileManagerHelper.findByPath(path);
    }

    @Override
    public List<File> findByPath(Path path, FileFilter filter) {
        return FileManagerHelper.findByPath(path,filter);
    }

    @Override
    public List<File> findFiles(Path path) {
        try {
            return FileManagerHelper.findFiles(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<File> findFiles(Path path, String fileExtension) {
        try {
            return FileManagerHelper.findFiles(path,fileExtension);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<File> findRootFolders(Path rootPath) {
        return FileManagerHelper.findRootFolders(rootPath);
    }

    @Override
    public void uploadFile(Path path, InputStream fis) {
        try {
            FileManagerHelper.uploadFile(path,fis);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public byte[] downloadFile(Path path) {
        byte[] result = new byte[0];
        try {
            result = FileManagerHelper.downloadFile(path);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public void move(Path source, Path destination) {
        try {
            FileManagerHelper.move(source,destination);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void copy(Path sourcePath, Path destinationPath) {
        try {
            FileManagerHelper.copy(sourcePath,destinationPath);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void rename(Path path, String newName) {
        try {
            FileManagerHelper.rename(path,newName);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void delete(Path path) {
        try {
            FileManagerHelper.delete(path);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void extractZipFile(Path zipFile, Path destination) {
        try {
            FileManagerHelper.unzip(zipFile, destination);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void saveContentToFile(String name, String path, String content) {
        try {
            FileManagerHelper.saveContentToFile(name, path, content);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public File createFile(Path path, byte[] bytes) {
        try {
            return FileManagerHelper.createFile(path, bytes);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public Path setAttributeToFile(Path path, String name, byte[] val) {
        try {
            return FileManagerHelper.setAttributeToFile(path, name, val);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

}
