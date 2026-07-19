package com.example.aicodereview.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileService {

    private final String UPLOAD_DIR = "uploads/";

    public FileService() {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public List<File> processUploadedFile(MultipartFile file) throws IOException {
        List<File> javaFiles = new ArrayList<>();
        String filename = file.getOriginalFilename();
        
        if (filename == null) return javaFiles;

        if (filename.endsWith(".zip")) {
            return extractZipAndFindJavaFiles(file);
        } else if (filename.endsWith(".java")) {
            File savedFile = saveFile(file);
            javaFiles.add(savedFile);
        }
        
        return javaFiles;
    }

    private File saveFile(MultipartFile file) throws IOException {
        Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
        Files.write(path, file.getBytes());
        return path.toFile();
    }

    private List<File> extractZipAndFindJavaFiles(MultipartFile zipFile) throws IOException {
        List<File> javaFiles = new ArrayList<>();
        byte[] buffer = new byte[1024];
        File destDir = new File(UPLOAD_DIR + System.currentTimeMillis());
        if (!destDir.exists()) destDir.mkdirs();

        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    if (newFile.getName().endsWith(".java")) {
                        javaFiles.add(newFile);
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
        return javaFiles;
    }

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }
}
