package com.hgl.hudada.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

/**
 * ClassName: NewNetUtils
 * Package: com.hgl.hudada.utils
 * Description:
 *
 * @Author HGL
 * @Create: 2025/6/16 9:10
 * @Descieption: 网络工具类 - 获取客户端真实IP地址
 */
public class NewNetUtils {

    private static final Logger log = LoggerFactory.getLogger(NewNetUtils.class);

    // 常见用于传递客户端IP的HTTP头字段
    private static final String HEADER_X_FORWARDED_FOR = "x-forwarded-for";
    private static final String HEADER_PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String HEADER_WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

    /**
     * 获取客户端真实IP地址
     *
     * @param request HttpServletRequest
     * @return 客户端IP字符串
     */
    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String ip = null;

        // 依次尝试从常见Header中获取客户端IP
        ip = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (isValidIp(ip)) {
            return extractFirstIp(ip);
        }

        ip = request.getHeader(HEADER_PROXY_CLIENT_IP);
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getHeader(HEADER_WL_PROXY_CLIENT_IP);
        if (isValidIp(ip)) {
            return ip;
        }

        // 最后回退到远程地址
        ip = request.getRemoteAddr();
        if ("127.0.0.1".equals(ip) || "::1".equals(ip)) {
            // 如果是本机访问，尝试获取真实IP
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                ip = localHost.getHostAddress();
            } catch (Exception e) {
                log.error("获取本地IP失败", e);
            }
        }

        return ip != null ? ip : "127.0.0.1";
    }

    /**
     * 判断IP是否有效
     */
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }

    /**
     * 提取第一个IP（适用于多个代理的情况）
     */
    private static String extractFirstIp(String ip) {
        int commaIndex = ip.indexOf(',');
        if (commaIndex > 0) {
            return ip.substring(0, commaIndex).trim();
        }
        return ip;
    }

}
