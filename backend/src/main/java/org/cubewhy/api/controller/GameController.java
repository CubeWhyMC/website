package org.cubewhy.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.cubewhy.api.utils.FileUtils;
import org.cubewhy.api.utils.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.cubewhy.api.BackendApplication.configPath;

@RestController
@RequestMapping("/game")
public class GameController {

    @Resource
    TextUtils utils;
    @Resource
    FileUtils fileUtils;

    @GetMapping("metadata")
    public void metadata(@NotNull HttpServletResponse response) throws IOException {
        String json = utils.readAll(fileUtils.getExternalFile(configPath + "/api/game-metadata.json"));
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }
}
