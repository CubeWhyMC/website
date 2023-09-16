package org.cubewhy.api;

import jakarta.annotation.Resource;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.cubewhy.api.utils.AddonFileListener;
import org.cubewhy.api.utils.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static org.cubewhy.api.BackendApplication.*;

@Component
public class StartupRunner implements CommandLineRunner {
    @Resource
    FileUtils utils;
    FileAlterationMonitor monitor = new FileAlterationMonitor(10000L);

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) throws Exception {
        // do init configs
        configPath.mkdirs();
        addonFolder.mkdirs();
        branchesFolder.mkdirs();
        liquidLunarFolder.mkdirs();
        capeFolder.mkdirs();
        artifactsFolder.mkdirs();
        utils.extractFile("api/metadata.json", configPath + "/metadata.json");
        utils.extractFile("api/game-metadata.json", configPath + "/game-metadata.json");
        utils.extractFile("api/liquid-metadata.json", liquidLunarFolder + "/metadata.json");
        // addon dir
        FileAlterationObserver observer = new FileAlterationObserver(addonFolder);
        monitor.addObserver(observer);
        observer.addListener(new AddonFileListener());
        monitor.start();
    }
}
