package org.cubewhy.api.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cubewhy.api.entity.GameInfo;
import org.cubewhy.api.entity.RestBean;
import org.cubewhy.api.utils.FileUtils;
import org.cubewhy.api.utils.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static org.cubewhy.api.BackendApplication.*;

@RestController
@RequestMapping("/launcher")
public class LauncherController {
    @Resource
    TextUtils utils;
    @Resource
    FileUtils fileUtils;
    @Value("${api.redirectLaunch}")
    String redirectLaunchAPI;

    @GetMapping("metadata")
    public void metadata(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        // Get <configDir>/metadata.json
        String json = utils.readAll(fileUtils.getExternalFile(configPath + "/metadata.json"));
        response.getWriter().write(json);
    }

    @PostMapping("launch")
    public void launch(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException, ServletException {
        String infoJson = utils.readAll(request.getReader());
        GameInfo info = JSONObject.parseObject(infoJson, GameInfo.class);
        response.setContentType("application/json;charset=UTF-8");
        String json = null;
        // return download url
        // Find at <configDir>/<branch>/<version>-<module>.json
        InputStream stream = fileUtils.getExternalFile(branchesFolder + String.format("/%s/%s-%s.json", info.getBranch(), info.getVersion(), info.getModule()));
        if (stream != null) {
            json = utils.readAll(stream);
        } else if (info.getBranch().equals("master") || info.getBranch().equals("development")) {
            // return official branches
            // TODO get official branches
        }
        if (json == null) {
            response.setStatus(404);
            json = utils.formatError("Not found", "Branch " + info.getBranch() + " not found, if you're a developer, add this branch in config", "BRANCH_NOT_FOUND");
        }
        response.getWriter().write(json);
    }

    @GetMapping("launch")
    public void launch(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.failure(HttpURLConnection.HTTP_BAD_METHOD, "Post needed").toJson());
    }

    @PostMapping("reportLaunchStatus")
    public void reportLaunchStatus(@RequestBody String hwid, @RequestBody String data, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.success(data).toJson());
    }

    @GetMapping("download")
    public void download(@RequestParam("path") String name, HttpServletResponse response) throws IOException {
        name = name.split("\\?")[0]; // LunarClient Launcher will add "?"
        File file = new File(artifactsFolder, name);
        // send download
        fileUtils.sendDownload(file, response);
    }
}
