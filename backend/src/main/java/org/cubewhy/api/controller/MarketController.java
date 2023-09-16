package org.cubewhy.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.cubewhy.api.entity.RestBean;
import org.cubewhy.api.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;

import static org.cubewhy.api.BackendApplication.addonFolder;
import static org.cubewhy.api.utils.AddonFileListener.addons;

@Controller
@RequestMapping("/market")
public class MarketController {
    @Resource
    FileUtils utils;


    @GetMapping("info")
    public void info(@NotNull HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.success(addons).toJson());
    }

    @GetMapping("download")
    public void download(@RequestParam("path") String name, HttpServletResponse response) throws IOException {
        File file = new File(addonFolder, name);
        utils.sendDownload(file, response);
    }
}
