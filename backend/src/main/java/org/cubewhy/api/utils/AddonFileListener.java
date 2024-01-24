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

    public static void addFile(File file) {
        JSONObject json = new JSONObject();
        ;
        json.put("name", file.getName());
        json.put("category", getAddonType(file));
        json.put("downloadLink", "/plugins/download?path=" + URLEncoder.encode(file.getPath().replace(addonFolder.getPath(), "").replace("\\", "/"), StandardCharsets.UTF_8).replace("%2F", "/"));
        try (JarFile jar = new JarFile(file)) {
            // try to get addon.meta.json
            InputStream stream = jar.getInputStream(jar.getEntry("addon.meta.json"));
            String meta = utils.readAll(stream);
            json.put("meta", JSONObject.parse(meta));
        } catch (Exception e) {
            // Too lazy...
            json.put("meta", null);
        }
        addons.add(json);
    }

    private static String getAddonType(@NotNull File file) {
        return switch (file.getParentFile().getName()) {
            case "mods" -> "weave";
            case "cn" -> "cn";
            case "fabric" -> "fabric";
            case "javaagents" -> "Agent";
            default ->
                // Unreachable
                    file.getParentFile().getName();
        };
    }

    private void walkDir(@NotNull File dir) throws IOException {
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
