package org.cubewhy.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.cubewhy.api.entity.RestBean;
import org.cubewhy.api.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

import static org.cubewhy.api.BackendApplication.capeFolder;

@RestController
@RequestMapping("/capes")
public class CapeController {
    @Resource
    FileUtils utils;


    @GetMapping("{name}")
    public void getCape(@PathVariable String name, @NotNull HttpServletResponse response) throws IOException {
        // Check file
        File capeFile = new File(capeFolder + "/" + name);
        if (capeFile.exists()) {
            // send a download requests
            utils.sendImage(capeFile, response);
        } else {
            // redirect to official API
            response.sendRedirect("https://s-optifine.net/capes/" + name);
//            // Send 404
//            response.setContentType("application/json;charset=utf-8");
//            response.setStatus(404); // set status
//            // write body
//            response.getWriter().write(RestBean.failure(404, "Player not found").toJson());
        }
    }
}
