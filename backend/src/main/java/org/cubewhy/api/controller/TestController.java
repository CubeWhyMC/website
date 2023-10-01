package org.cubewhy.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cubewhy.api.entity.RestBean;
import org.cubewhy.api.utils.FileUtils;
import org.cubewhy.api.utils.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Resource
    TextUtils utils;

    @PostMapping("post")
    public void testPost(@NotNull HttpServletResponse response, @RequestBody String p) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"p\":  \"" + p + "\"}");
    }

    @GetMapping("get")
    public void testGet(@NotNull HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.success().toJson());
    }

    @PutMapping("put")
    public void testPut(HttpServletResponse response, HttpServletRequest request) throws Exception {
        System.out.println("request.getContentType() = " + request.getContentType());
        System.out.println("request.getInputStream().readAllBytes().length = " +
                request.getInputStream().readAllBytes().length);
        System.out.println("request.getQueryString() = " + request.getQueryString());
        System.out.println("new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8) = " + new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String v = headerNames.nextElement();
            System.out.println(v + ": " + request.getHeader(v));
        }
    }
}
