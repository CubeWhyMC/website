package org.cubewhy.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cubewhy.api.service.impl.AccountServiceImpl;
import org.cubewhy.api.utils.FileUtils;
import org.cubewhy.api.utils.TextUtils;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

import static org.cubewhy.api.BackendApplication.capeFolder;

@RestController
@RequestMapping("/api/user/")
public class CustomerController {
    @Resource
    AccountServiceImpl service;
    @Resource
    FileUtils fileUtils;
    @Resource
    TextUtils textUtils;

    @GetMapping("bind-mc")
    public void bindMinecraft(HttpServletResponse response, @RequestParam String token) {
        // do auth and bind
        String uuid = "";
    }

    @PutMapping("upload-cape")
    public void uploadCape(HttpServletResponse response, HttpServletRequest request, @RequestBody String username) throws IOException {
        byte[] file = request.getInputStream().readAllBytes();
        // save file
        fileUtils.save(file, new File(capeFolder, username + ".png"));
    }
}
