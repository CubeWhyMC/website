package org.cubewhy.api.files;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class InvokeCount {
    @Getter
    private JSONObject config;
    public final File file;

    public InvokeCount(File file) {
        this.file = file;
        this.load();
    }

    public void addCount(String apiName) {
        if (!config.containsKey(apiName)) {
            config.put(apiName, 0);
        }
        config.put(apiName, config.getIntValue(apiName) + 1);
        save();
    }

    public int getCount(String apiName) {
        return config.getIntValue(apiName, 0);
    }

    public InvokeCount save() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.file));
            bufferedWriter.write(config.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public InvokeCount load() {
        FileReader fileReader;
        boolean successful = false;

        while (!successful) {
            try {
                fileReader = new FileReader(this.file, StandardCharsets.UTF_8);
                config = JSON.parseObject(fileReader);
                if (config == null) {
                    config = new JSONObject();
                }
                successful = true;
                fileReader.close();
            } catch (FileNotFoundException e) {

                try {
                    if (!this.file.getParentFile().exists()) {
                        this.file.getParentFile().mkdirs();
                    }
                    this.file.createNewFile();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }
}
