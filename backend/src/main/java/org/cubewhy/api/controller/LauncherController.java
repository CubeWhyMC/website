package org.cubewhy.api.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cubewhy.api.entity.CrashReport;
import org.cubewhy.api.entity.GameInfo;
import org.cubewhy.api.entity.RestBean;
import org.cubewhy.api.utils.FileUtils;
import org.cubewhy.api.utils.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.cubewhy.api.BackendApplication.*;

@RestController
@RequestMapping("/launcher")
@Slf4j
public class LauncherController {
    @Resource
    TextUtils utils;
    @Resource
    FileUtils fileUtils;

    @Value("${api.viewCrashReport}")
    String crashReportViewURL;

    @GetMapping("metadata")
    public void metadata(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        // Get <configDir>/metadata.json
        String json = utils.readAll(fileUtils.getExternalFile(configPath + "/metadata.json"));
        response.getWriter().write(json);
    }

    @PostMapping("launch")
    public void launch(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException, ServletException {
        String infoJson = utils.readAll(request.getReader());
        GameInfo info = JSONObject.parseObject(infoJson, GameInfo.class);
        response.setContentType("application/json;charset=UTF-8");
        String json = null;
        // return download url
        // Find at <configDir>/<branch>/<version>-<module>.json
        InputStream stream = fileUtils.getExternalFile(branchesFolder + String.format("/%s/%s-%s.json", info.getBranch(), info.getVersion(), info.getModule()));
        if (stream != null) {
            json = utils.readAll(stream);
        }

        if (json == null) {
            response.setStatus(404);
            json = utils.formatError("Not found", "Branch " + info.getBranch() + " not found, if you're a developer, add this branch in config", "BRANCH_NOT_FOUND");
        }
        response.getWriter().write(json);
    }

    @GetMapping("launch")
    public void launch(@NotNull HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.failure(HttpURLConnection.HTTP_BAD_METHOD, "Post needed").toJson());
    }

    @PostMapping("reportLaunchStatus")
    public void reportLaunchStatus(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(RestBean.success().toJson());
    }

    @GetMapping("download")
    public void download(@RequestParam("path") String name, HttpServletResponse response) throws IOException {
        name = name.split("\\?")[0]; // LunarClient Launcher will add "?"
        File file = new File(artifactsFolder, name);
        // send download
        fileUtils.sendDownload(file, response);
    }

    @PostMapping("uploadCrashReport")
    public void uploadCrashReport(HttpServletResponse response, @RequestBody CrashReport body) throws IOException {
        UUID id = UUID.randomUUID();
        log.info("Receive crash report, id = " + id);
        JSONObject json = new JSONObject();
        json.put("id", id.toString());
        json.put("message", "success");
        json.put("url", crashReportViewURL + "/?id=" + id);
        // dump the report
        try (FileWriter writer = new FileWriter(new File(crashReportFolder, id + ".json"))) {
            JSONObject result = new JSONObject();
            result.put("trace", body.getTrace());
            result.put("type", body.getType());
            result.put("launch-script", body.getLaunchScript());
            writer.write(result.toJSONString());
        }

        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(RestBean.success(json).toJson());
    }

    @GetMapping("getCrashReport")
    public void getCrashReport(HttpServletResponse response, @RequestParam(required = false) String id) throws IOException {
        if (id == null) {
            // return a list of all crash reports
            File[] files = crashReportFolder.listFiles();
            response.setContentType("application/json; charset=UTF-8");
            if (files == null) {
                response.setStatus(501);
                response.getWriter().write(RestBean.failure(501, "Not Implemented").toJson());
                return;
            }
            JSONArray list = new JSONArray();
            for (File file : files) {
                String crashID = file.getName().split("\\.")[0];
                list.add(crashID);
            }
            response.getWriter().write(RestBean.success(list).toJson());
        } else {
            File report = new File(crashReportFolder, id + ".json");
            response.setContentType("application/json; charset=UTF-8");
            if (!report.exists()) {
                response.setStatus(404);
                response.getWriter().write(RestBean.failure(404, "Not found").toJson());
                return;
            }
            String json = org.apache.commons.io.FileUtils.readFileToString(report, StandardCharsets.UTF_8);
            response.getWriter().write(RestBean.success(JSONObject.parse(json)).toJson());
        }
    }
}
