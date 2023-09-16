package org.cubewhy.api.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.cubewhy.api.entity.RestBean;
import org.cubewhy.api.utils.FileUtils;
import org.cubewhy.api.utils.TextUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.cubewhy.api.BackendApplication.liquidLunarFolder;

@RestController
@RequestMapping("/api/liquid")
public class LiquidLunarController {
    @Resource
    TextUtils utils;
    @Resource
    FileUtils fileUtils;

    @GetMapping("metadata")
    public void servers(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        // Get <configDir>/liquid-lunar/metadata.json
        String json = utils.readAll(fileUtils.getExternalFile(liquidLunarFolder + "/api/metadata.json"));
        response.getWriter().write(RestBean.success(JSONObject.parse(json)).toJson());
    }
}
