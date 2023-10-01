package org.cubewhy.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cubewhy.api.entity.RestBean;
import org.cubewhy.api.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Arrays;

import static org.cubewhy.api.BackendApplication.addonFolder;
import static org.cubewhy.api.utils.AddonFileListener.addons;

@Controller
@Slf4j
@RequestMapping("/plugins")
public class PluginController {
    @Resource
    FileUtils utils;


    @GetMapping("info")
    public void info(@NotNull HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.success(addons).toJson());
    }

    @GetMapping("download/**")
    public void download(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String[] path = request.getRequestURI().split("/");
        String name = String.join("/", Arrays.copyOfRange(path, 3, path.length));
        File file = new File(addonFolder, name);
        if (file.exists()) {
            log.info("Plugin download request: " + name);
            utils.sendDownload(file, response);
        } else {
            log.error("Plugin not found: " + name);
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(404);
            response.getWriter().write(RestBean.failure(404, "Not found").toJson());
        }
    }
}
