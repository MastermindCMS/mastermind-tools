package com.mastermindcms.modules.filestorage.helpers;

import com.mastermindcms.modules.filestorage.beans.MsmFileBean;
import com.mastermindcms.modules.filestorage.beans.devexpress.DePathInfoPart;
import com.mastermindcms.modules.filestorage.exceptions.EmptyPathInfoException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileManagerHelper {

    private FileManagerHelper() {
    }

    public static MsmFileBean toMsmFileBean(File file, String fileUploadRoot) {
        MsmFileBean msmFileBean = new MsmFileBean();
        msmFileBean.setName(file.getName());
        String path = Paths.get(fileUploadRoot)
                .relativize(file.toPath())
                .toString();
        msmFileBean.setPath(path);
        msmFileBean.setDateModified(new Date(file.lastModified()));
        msmFileBean.setIsDirectory(file.isDirectory());
        if (file.isDirectory()) {
            msmFileBean.setHasSubDirectories(Objects.requireNonNull(file.listFiles(File::isDirectory)).length > 0);
        }
        msmFileBean.setSize(file.length());
        msmFileBean.setKey(path);
        return msmFileBean;
    }

    public static void unzip(File fileZip, File destDir) throws IOException {
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) {
                File newDir = newFile(destDir, zipEntry);
                newDir.mkdirs();
            } else {
                File newFile = newFile(destDir, zipEntry);
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static void unzip(Path zipFile, Path destDirPath) throws IOException {
        FileManagerHelper.unzip(zipFile.toFile(), destDirPath.toFile());
    }

    public static void unzip(String fileZip, String destDirPath) throws IOException {
        FileManagerHelper.unzip(new File(fileZip), new File(destDirPath));
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        File fileParent = destFile.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        } else if (!fileParent.isDirectory()) {
            throw new IOException("File exists but directory expected: not a directory: " + fileParent.getName());
        }

        return destFile;
    }

    public static Path getPathFromPathInfoAllowEmpty(
            String root,
            List<DePathInfoPart> pathInfo,
            String... furtherPath
    ) {
        ArrayList<String> dirs = (pathInfo != null && pathInfo.size() > 0)
                ? new ArrayList<>(Arrays.asList(pathInfo.stream()
                .map(DePathInfoPart::getName)
                .toArray(String[]::new)))
                : new ArrayList<>();
        if (furtherPath != null && furtherPath.length > 0) {
            dirs.addAll(Arrays.asList(furtherPath));
        }
        return Paths.get(root, dirs.toArray(new String[dirs.size()]));
    }

    public static Path getPathFromPathInfo(
            String root,
            List<DePathInfoPart> pathInfo,
            String... furtherPath
    ) throws EmptyPathInfoException {
        if (pathInfo == null || pathInfo.size() == 0) {
            throw new EmptyPathInfoException();
        }
        ArrayList<String> dirs = new ArrayList<>(Arrays.asList(pathInfo.stream()
                .map(DePathInfoPart::getName)
                .toArray(String[]::new)));
        if (furtherPath != null && furtherPath.length > 0) {
            dirs.addAll(Arrays.asList(furtherPath));
        }
        return Paths.get(root, dirs.toArray(new String[dirs.size()]));
    }

    public static void delete(Path path) throws IOException  {
        File file = path.toFile();
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
    }

    public static void rename(Path path, String newName) throws IOException {
        Files.move(
                path,
                Paths.get(path.getParent().toString(), newName),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    public static void copy(Path sourcePath, Path destinationPath) throws IOException {
        File sourceFile = sourcePath.toFile();
        if (!sourceFile.exists()) {
            throw new IOException("File " + sourceFile.getAbsolutePath() + "does not exist!");
        }
        Path destinationFilePath = Paths.get(destinationPath.toString(), sourceFile.getName());
        if (sourceFile.isDirectory()) {
            FileUtils.copyDirectory(sourceFile, destinationFilePath.toFile());
        } else {
            Files.copy(sourcePath, destinationFilePath, REPLACE_EXISTING);
        }
    }

    public static void move(Path source, Path destination) throws IOException {
        Files.move(
                source,
                Paths.get(destination.toString(), source.getFileName().toString()),
                REPLACE_EXISTING);
    }

    public static byte[] downloadFile(Path path) throws IOException {
        InputStream fis = Files.newInputStream(path);
        return IOUtils.toByteArray(fis);
    }

    public static void uploadFile(Path path, InputStream fis) throws IOException {
        Files.copy(fis, path, REPLACE_EXISTING);
    }

    public static void saveContentToFile(String name, String path, String content) throws IOException{
        InputStream stream = new ByteArrayInputStream(content.getBytes());
        File destinationDir = new File(path);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }
        uploadFile(Paths.get(path, name), stream);
    }

    public static List<File> findRootFolders(Path rootPath) {
        try (Stream<Path> walk = Files.walk(rootPath,1)) {
            return walk.filter(Files::isDirectory)
                    .filter(p -> !p.equals(rootPath))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static List<File> findFiles(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }

        List<File> result;

        try (Stream<Path> walk = Files.walk(path)) {
            result = walk.map(Path::toFile)
                    .collect(Collectors.toList());
        }

        return result;
    }

    public static List<File> findFiles(Path path, String fileExtension)
            throws IOException {

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }

        List<File> result;

        try (Stream<Path> walk = Files.walk(path)) {
            result = walk
                    .filter(p -> !Files.isDirectory(p))
                    .map(Path::toFile)
                    .filter(file -> file.getName().endsWith(fileExtension))
                    .collect(Collectors.toList());
        }

        return result;
    }

    public static List<File> findByPath(Path path) {
        return Files.exists(path) ?
                Arrays.asList(Objects.requireNonNull(path.toFile().listFiles())) :
                new ArrayList<>();
    }

    public static List<File> findByPath(Path path, FileFilter filter) {
        return Files.exists(path) ?
                Arrays.asList(Objects.requireNonNull(path.toFile().listFiles(filter))) :
                new ArrayList<>();
    }

    public static void createDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    public static File createFile(Path path, byte[] bytes) throws IOException {
        if(bytes.length > 0) {
            Path result = Files.write(path, bytes);
            return result.toFile();
        } else {
            return null;
        }
    }

    public static Path setAttributeToFile(Path path, String name, byte[] val) throws IOException {
        return Files.setAttribute(path, name, val);
    }
}
