package org.cubewhy.api.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarFile;

import static org.cubewhy.api.BackendApplication.addonFolder;

public class AddonFileListener extends FileAlterationListenerAdaptor {
    public static final JSONArray addons = new JSONArray();
    static TextUtils utils = new TextUtils();

    public AddonFileListener() throws IOException {
        walkDir(addonFolder);
    }

    public static void addFile(File file) throws IOException {
        JSONObject json = new JSONObject();
        ;
        try (JarFile jar = new JarFile(file)) {
            // try to get lunarcn.meta.json
            try {
                InputStream stream = jar.getInputStream(jar.getEntry("lunarcn.meta.json"));
                String meta = utils.readAll(stream);
                json.put("meta", meta);
            } catch (Exception e) {
                // Too lazy...
            }
        }
        json.put("name", file.getName());
        json.put("category", getAddonType(file));
        json.put("downloadLink", "/plugins/download?path=" + URLEncoder.encode(file.getPath().replace(addonFolder.getPath(), "").replace("\\", "/"), StandardCharsets.UTF_8).replace("%2F", "/"));
        addons.add(json);
    }

    private static String getAddonType(File file) {
        return switch (file.getParentFile().getName()) {
            case "mods" -> "Mod";
            case "javaagents" -> "Agent";
            default ->
                // Unreachable
                    "Unknown";
        };
    }

    private void walkDir(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    addFile(file);
                } else if (file.isDirectory()) {
                    walkDir(file);
                }
            }
        }
    }

    /**
     * File created Event.
     *
     * @param file The file created (ignored)
     */
    @SneakyThrows
    @Override
    public void onFileCreate(File file) {
        if (file.getName().endsWith(".jar")) {
            addFile(file);
        }
    }

    /**
     * File deleted Event.
     *
     * @param file The file deleted (ignored)
     */
    @Override
    public void onFileDelete(@NotNull File file) {
        if (file.getName().endsWith(".jar")) {
            addons.remove(file.getName()); // remove
        }
    }
}
