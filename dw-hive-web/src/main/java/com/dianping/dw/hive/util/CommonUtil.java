package com.dianping.dw.hive.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import com.dianping.dw.hive.security.Role;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.dw.hive.exception.JsonException;
import com.google.common.base.Strings;

/**
 * 常用工具类
 * 
 * @author yujie.yao
 * @author tao.meng 
 */
public class CommonUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CommonUtil.class);

    /**
     * 获取IP地址
     * 
     * @param sr 请求
     * @return String IP地址
     */
    public static String getIPAddress(HttpServletRequest sr) {
        String ipAddress = sr.getHeader("X-Forwarded-For");
        if (!Strings.isNullOrEmpty(ipAddress) && ipAddress.indexOf(",") != -1) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }
        if (Strings.isNullOrEmpty(ipAddress)) {
            ipAddress = sr.getHeader("X-Real-IP");
        }
        if (Strings.isNullOrEmpty(ipAddress)) {
            ipAddress = sr.getRemoteAddr();
        }
        return ipAddress;
    }

    /**
     * 对象JSON化
     * @param obj
     * @return String JSON
     */
    public static String toJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter ret = new StringWriter();
        try {
            mapper.writeValue(ret, obj);
        } catch (Exception ex) {
            LOG.error("JSON转换错误", ex);
            throw new JsonException("JSON转换错误", ex);
        }
        return ret.toString();
    }

    public static <T> T fromJson(String json, Class<T> className) {
        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//        mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
//        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
//        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        try {
            return mapper.readValue(json, className);
        } catch (Exception ex) {
            LOG.error("JSON转换错误", ex);
            throw new JsonException("JSON转换错误", ex);
        }
    }

    /**
     * 获取异常堆栈信息
     * 
     * @param ex
     * @return String 异常堆栈信息
     */
    public static String getStackTraceMsg(Exception ex) {
        StringBuilder sb = new StringBuilder(500);
        sb.append(ex);
        StackTraceElement[] trace = ex.getStackTrace();
        for (StackTraceElement element : trace) {
            sb.append("\n\tat " + element);
        }
        return sb.toString();
    }

    /**
     * 读取文件
     * 
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        Scanner sc = null;
        try {
            sc = new Scanner(new File(filePath));
            StringBuilder sb = new StringBuilder();
            String line;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (FileNotFoundException ex) {
//            LOG.error("文件未找到", ex);
            return "";
        } finally {
            if (null != sc) {
                sc.close();
            }
        }
    }

    /**
     *  删除临时文件
     *
     * @param filePath
     * @return boolean
     */
    public static boolean deleteTmpFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);

        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 获取当前的role
     *
     * @param username
     * @param roleList
     * @return Role
     */
    public static Role getCurrentRole(String username, List<Role> roleList) {
        Role curRole = null;
        if (null == username) {
            return curRole;
        }
        for (Role role : roleList) {
            if (username.equals(role.getUserName())) {
                curRole = role;
                break;
            }
        }
        return curRole;
    }
}
