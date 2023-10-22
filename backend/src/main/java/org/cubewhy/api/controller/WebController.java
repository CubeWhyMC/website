package org.cubewhy.api.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cubewhy.api.entity.RestBean;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
@RequestMapping("/api/web")
public class WebController {
    @Value("${pay.unknown}")
    String targetUnknown;
    @Value("${pay.wechat}")
    String targetWeChat;
    @Value("${pay.qq}")
    String targetQQ;
    @Value("${pay.alipay}")
    String targetAlipay;
    @Value("${api.version}")
    String versionApi;

    @Resource
    private RestTemplate restTemplate;

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

    @GetMapping("latest")
    public void latestVersion(HttpServletRequest request, HttpServletResponse response) throws Exception {
        /*
        Formats:
        json *default*
        text
        javascript
        * */
        String format = request.getParameter("format");
        if (format == null) {
            format = "json"; // default: json
        }
        // Request API
        ResponseEntity<String> results = restTemplate.exchange(versionApi, HttpMethod.GET, null, String.class);
        JSONArray json = JSONArray.parse(results.getBody());

        JSONObject latestJson = json.getJSONObject(0); // latest
        String version = latestJson.getString("tag_name");


        switch (format) {
            case "json":
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write(RestBean.success(latestJson).toJson());
                break;
            case "text":
                response.setContentType("text/plain");
                response.getWriter().write(version);
                break;
            case "javascript":
            case "js":
                /*
                using via
                <script src="https://api.lunarclient.top/api/web/latest"></script>
                <script>
                    document.getElementById("id").innerHTML = latestVersion"
                </script>`
                * */
                response.setContentType("text/plain");
                response.getWriter().write(String.format("let latestVersion = \"%s\"", version));
                break;
        }
    }
}
