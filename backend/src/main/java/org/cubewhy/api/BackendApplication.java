package org.cubewhy.api;

import org.cubewhy.api.utils.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class BackendApplication {

    public static final File configPath = new File(System.getProperty("configPath", System.getProperty("user.home") + "/.cubewhy/api/"));
    public static final File addonFolder = new File(configPath, "addons");
    public static final File branchesFolder = new File(configPath, "branches");
    public static final File liquidLunarFolder = new File(configPath, "liquid-lunar");
    public static final File capeFolder = new File(configPath, "capes");
    public static final File artifactsFolder = new File(configPath, "artifacts");
    public static final File applicationConfigFile = new File(configPath, "application.yml");
    public static void main(String[] args) {
        configPath.mkdirs();
        new FileUtils().extractFile("application.yml", applicationConfigFile);
        System.setProperty("spring.config.location", applicationConfigFile.getAbsolutePath()); // set config
        System.setProperty("file.encoding", "UTF-8"); // set encoding
        SpringApplication.run(BackendApplication.class, args);
    }

}
