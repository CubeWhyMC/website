package org.cubewhy.api.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import org.cubewhy.api.entity.RestBean;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Base64;

import static org.cubewhy.api.BackendApplication.addonFolder;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {
    @PutMapping("upload")
    public void upload(@RequestBody String base64, String filePath, @NotNull HttpServletResponse response) throws IOException {
        File file = new File(addonFolder, filePath);
        PrintWriter writer = response.getWriter();
        if (file.exists()) {
            writer.write(RestBean.failure(409, "File exists").toJson());
            return;
        }
        byte[] bytes = Base64.getDecoder().decode(base64);
        // dump file
        try (OutputStream stream = Files.newOutputStream(file.toPath())) {
            stream.write(bytes);
        }
        JSONObject json = new JSONObject();
        json.put("path", "/plugins/download" + file.getPath().replace(addonFolder.getPath(), ""));
        writer.write(RestBean.success(json).toJson()); // Ok
    }
}
