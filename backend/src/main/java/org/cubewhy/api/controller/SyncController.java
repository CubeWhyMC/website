package org.cubewhy.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/sync")
public class SyncController {
    @GetMapping("/missing")
    public void missing() {
        // TODO complete api
    }
}
