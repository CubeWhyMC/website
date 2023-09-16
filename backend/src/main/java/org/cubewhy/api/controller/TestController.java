package org.cubewhy.api.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.cubewhy.api.entity.RestBean;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/test")
public class TestController {
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
}
