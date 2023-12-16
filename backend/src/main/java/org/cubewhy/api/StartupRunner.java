package org.cubewhy.api;

import jakarta.annotation.Resource;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.cubewhy.api.utils.AddonFileListener;
import org.cubewhy.api.utils.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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
        crashReportFolder.mkdirs();
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

        // disable ssl cer verify
        ignoreSsl();
    }

    static class MITM implements TrustManager, X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }
    }


    private static void ignoreSsl() throws Exception {
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                return true;
            }
        };
        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new MITM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
}
