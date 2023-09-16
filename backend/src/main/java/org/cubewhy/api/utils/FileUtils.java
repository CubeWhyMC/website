package org.cubewhy.api.utils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.cubewhy.api.entity.RestBean;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class FileUtils {
    public InputStream getFile(String pathToFile) {
        return getClass().getResourceAsStream("/" + pathToFile);
    }

    public InputStream getExternalFile(String pathToFile) {
        return this.getExternalFile(new File(pathToFile));
    }

    @SneakyThrows
    public InputStream getExternalFile(File file) {
        if (file.isFile()) {
            return new FileInputStream(file);
        } else {
            return null;
        }
    }

    @SneakyThrows
    public void extractFile(String filePath, @NotNull File toPath) {
        InputStream stream = getFile(filePath);
        byte[] bytes = stream.readAllBytes();
        // do create file
        if (toPath.createNewFile()) {
            try (OutputStream out = new FileOutputStream((toPath))) {
                out.write(bytes); // dump
            }
        }
    }

    public void extractFile(String filePath, String toPath) {
        this.extractFile(filePath, new File(toPath));
    }

    /**
     * Send a download request
     *
     * @param stream       Input stream
     * @param response     http response
     * @param downloadName name shown in browser
     */
    public void sendDownload(InputStream stream, HttpServletResponse response, String downloadName) throws IOException {
        try (stream) {
            // read bytes
            byte[] bytes = stream.readAllBytes();
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(downloadName, StandardCharsets.UTF_8));
            response.addHeader("Content-Length", String.valueOf(bytes.length));
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(bytes);
            outputStream.flush();
        }
    }

    /**
     * Send a download request
     *
     * @param file         Target file
     * @param response     Http response
     * @param downloadName Name shown in browser
     */
    public void sendDownload(File file, HttpServletResponse response, String downloadName) throws IOException {
        try {
            this.sendDownload(this.getExternalFile(file), response, downloadName);
        }  catch (FileNotFoundException e) {
            response.setContentType("application/json");
            response.setStatus(404); // set status
            response.getWriter().write(RestBean.failure(404, "File not found").toJson());
        }
    }

    /**
     * Send a download request
     *
     * @param file     Target file
     * @param response Http response
     */
    public void sendDownload(File file, HttpServletResponse response) throws IOException {
        this.sendDownload(file, response, file.getName());
    }
    
    public void sendImage(InputStream stream, HttpServletResponse response, String type) throws IOException {
        try (stream) {
            byte[] bytes = stream.readAllBytes();
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType(type);
            outputStream.write(bytes);
            outputStream.flush();
        }
    }

    public void sendImage(File file, HttpServletResponse response, String type) throws IOException {
        try {
            this.sendImage(this.getExternalFile(file), response, type);
        } catch (FileNotFoundException error) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(RestBean.failure(404, "Image not found").toJson());
        }
    }

    public void sendImage(File file, HttpServletResponse response) throws IOException {
        String[] fileSplit = file.getName().split("\\.");
        this.sendImage(file, response, "image/" + fileSplit[fileSplit.length - 1]);
    }
}
