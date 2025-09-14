package com.mastermindcms.modules.filestorage.services;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface FileManagerService {


    /**
     * This method creates a object.
     *
     * @param path java object, which will be created
     */
    void createDirectory(Path path);

    /**
     * This method returns a files list.
     *
     * @param path to java object, with which the file will be found
     * @return files as list
     */
    List<File> findByPath(Path path);

    /**
     * This method returns a files list.
     *
     * @param path to java object, with which the file will be found
     * @param filter file filter object
     * @return files as list
     */
    List<File> findByPath(Path path, FileFilter filter);

    /**
     * This method returns a files list.
     *
     * @param path to java object, with which the file will be found
     * @return files as list
     */
    List<File> findFiles(Path path);

    /**
     * This method returns a files list.
     *
     * @param path to java object, with which the file will be found
     * @param fileExtension extension to filter files
     * @return files as list
     */
    List<File> findFiles(Path path, String fileExtension);

    /**
     * This method returns a files list.
     *
     * @param rootPath to files, with which the file will be found
     * @return files as list
     */
    List<File> findRootFolders(Path rootPath);

    /**
     * This method uploads a file.
     *
     * @param destination path to file, with which the file will be found
     * @param fis is a stream, with which the file will be uploaded
     */
    void uploadFile(Path destination, InputStream fis);

    /**
     * This method downloads a file.
     *
     * @param path path to file, with which the file will be found
     * @return data as byte array
     */
    byte[] downloadFile(Path path);

    /**
     * This method moves a file.
     *
     * @param source path to file, with which the file will be found
     * @param destination path to file, with which the file will be found
     */
    void move(Path source, Path destination);

    /**
     * This method copies a file.
     *
     * @param source path to file, with which the file will be found
     * @param destination path to file, with which the file will be found
     */
    void copy(Path source, Path destination);

    /**
     * This method updates a file name.
     *
     * @param file path to file, with which the file will be found
     * @param newName name value, which will be set file
     */
    void rename(Path file, String newName);

    /**
     * This method deletes a file.
     *
     * @param path path to file, with which the file will be found
     */
    void delete(Path path);

    /**
     * This method extracts a zip file.
     *
     * @param file path of file, with which the file will be uploaded
     * @param destination path to file, with which the file will be found
     */
    void extractZipFile(Path file, Path destination);

    /**
     * This method saves content in file.
     *
     * @param name is a value, with which the file will be found
     * @param path path to file, with which the file will be found
     * @param content content, which will be saved in file
     */
    void saveContentToFile(String name, String path, String content);

    /**
     * This method is creates a new file from byte array
     * @param path the path where the file will be saved
     * @param bytes the bytes array with content data for
     *             the file
     * @return created file based on input bytes array
     */
    File createFile(Path path, byte[] bytes);

    /**
     * This method is sets a new attribute to file from byte array
     * @param path the path where the file is located
     * @param name the name of the attribute
     * @param val the bytes array with content data as
     *             an attribute value
     * @return returns path of updated file
     */
    Path setAttributeToFile(Path path, String name, byte[] val);

}
