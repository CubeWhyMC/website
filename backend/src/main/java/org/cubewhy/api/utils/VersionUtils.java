package org.cubewhy.api.utils;

import com.alibaba.fastjson2.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class VersionUtils {
    public JSONObject createArtifact(String name, String sha1, String url, String differentialUrl, @NotNull Type type) {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("sha1", sha1);
        json.put("url", url);
        json.put("differentialUrl", (differentialUrl == null) ? url : differentialUrl);
        json.put("type", type.name());
        return json;
    }

    public JSONObject createArtifact(String name, String url, String sha1, Type type) {
        return createArtifact(name, sha1, url, null, type);
    }


    public JSONObject createLicenseJson(String file, String url, String sha1) {
        JSONObject json = new JSONObject();
        json.put("file", file);
        json.put("url", url);
        json.put("sha1", sha1);
        return json;
    }

    public enum Type {
        CLASS_PATH,
        EXTERNAL_FILE,
        NATIVES
    }
}
