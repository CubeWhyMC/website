package org.cubewhy.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/web")
public class WebApiController {
    @Value("${pay.unknown}")
    String targetUnknown;
    @Value("${pay.wechat}")
    String targetWeChat;
    @Value("${pay.qq}")
    String targetQQ;
    @Value("${pay.alipay}")
    String targetAlipay;

    @GetMapping("pay")
    public void pay(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        String agent = request.getHeader("User-Agent");
        String target = targetUnknown;
        if (agent.contains("WeChat")) {
            // wei-xin
            target = targetWeChat;
        } else if (agent.contains("QQ")) {
            // QQ
            target = targetQQ;
        } else if (agent.contains("Alipay")) {
            // Alipay
            target = targetAlipay;
        }
        response.sendRedirect(target);
    }
}
