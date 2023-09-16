package org.cubewhy.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.cubewhy.api.service.impl.AccountServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/")
public class CustomerController {
    @Resource
    AccountServiceImpl service;

    @GetMapping("bind-mc")
    public void bindMinecraft(HttpServletResponse response, @RequestParam String token) {
        // do auth and bind
        String uuid = "";
    }
}
