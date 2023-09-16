package org.cubewhy.api.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Component
public class TextUtils {
    @Value("${api.error}")
    String errorFormat;

    public String readAll(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String str;
        while (null != (str = reader.readLine())) {
            sb.append(str);
        }
        return sb.toString();
    }

    public String readAll(InputStream stream) throws IOException {
        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public String formatError(String title, String message, String code) {
        return errorFormat
                .replace("%title%", title)
                .replace("%msg%", message)
                .replace("%code%", code);
    }
}
