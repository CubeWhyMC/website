package org.cubewhy.api.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static org.cubewhy.api.BackendApplication.addonFolder;

public class AddonFileListener extends FileAlterationListenerAdaptor {
    public static final JSONArray addons = new JSONArray();

    public AddonFileListener() {
        walkDir(addonFolder);
    }

    private void walkDir(File dir) {
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

    public static void addFile(File file) {
        JSONObject json = new JSONObject();
        json.put("name", file.getName());
        json.put("type", getAddonType(file));
        json.put("path", "/market/download?path=" + file.getPath().replace(addonFolder.getPath(), "").replace("\\", "/"));
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

    /**
     * File created Event.
     *
     * @param file The file created (ignored)
     */
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
