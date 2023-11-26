package org.cubewhy.api.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Connector;
import org.cubewhy.api.entity.RestBean;
import org.cubewhy.api.utils.TextUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;

@RestController
@RequestMapping("/skin")
public class SkinController {
    @Value("${api.skin}")
    String skinApi; // must ends with "/"

    @Resource
    TextUtils utils;
    @Resource
    ClientHttpRequestFactory httpRequestFactory;

    @GetMapping("/raw/{uuid}")
    public void raw(HttpServletRequest request, HttpServletResponse response, @PathVariable String uuid) throws Exception {
        // do request
        ClientHttpRequest req1 = httpRequestFactory.createRequest(URI.create(skinApi + uuid), HttpMethod.GET);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> v = request.getHeaders(headerName);
            List<String> arr = new ArrayList<>();
            while (v.hasMoreElements()) {
                arr.add(v.nextElement());
            }
            req1.getHeaders().addAll(headerName, arr);
        }
        try (ClientHttpResponse res = req1.execute()) {
            int statusCode = res.getStatusCode().value();
            response.setStatus(statusCode);
            String rawJson = utils.readAll(res.getBody());
            JSONObject json = JSONObject.parse(rawJson);
            if (statusCode == 200) {
                // unpack
                String rawBase64 = json.getJSONArray("properties").getJSONObject(0).getString("value");
                // do decode
                JSONObject decoded = JSONObject.parse(new String(Base64.getDecoder().decode(rawBase64), StandardCharsets.UTF_8));
                String targetUrl = decoded.getJSONObject("textures").getJSONObject("SKIN").getString("url");
                // send request
                ClientHttpRequest req2 = httpRequestFactory.createRequest(URI.create(targetUrl), HttpMethod.GET);
                try (ClientHttpResponse res1 = req2.execute()) {
                    StreamUtils.copy(res1.getBody(), response.getOutputStream());
                }
            } else {
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write(RestBean.failure(statusCode, "Not a valid UUID or not found").toJson());
            }
        }
    }
}
