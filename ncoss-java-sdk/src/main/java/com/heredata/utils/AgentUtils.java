package com.heredata.utils;

import java.io.InputStream;
import java.util.Properties;

import static com.heredata.utils.LogUtils.logException;

/**
 * <p>Title: VersionInfoUtils</p>
 * <p>Description: 用户代理工具类。版本信息存放在resource目录下的versioninfo.properties文件中</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 14:54
 */
public class AgentUtils {

    private static final String VERSION_INFO_FILE = "versioninfo.properties";
    private static final String USER_AGENT_PREFIX = "nchos-sdk";

    private static String version = null;

    private static String defaultUserAgent = null;

    public static String getVersion() {
        if (version == null) {
            initializeVersion();
        }
        return version;
    }

    /**
     * 获取默认用户代理
     * @return
     */
    public static String getDefaultUserAgent() {
        if (defaultUserAgent == null) {
            defaultUserAgent = USER_AGENT_PREFIX + "/" + getVersion() + "(" + System.getProperty("os.name") + "/"
                    + System.getProperty("os.version") + "/" + System.getProperty("os.arch") + ";"
                    + System.getProperty("java.version") + ")";
        }
        return defaultUserAgent;
    }

    /**
     * 修复漏洞3.2.2.1  资源没有安全释放  漏洞来源代码扫描报告-cmstoreos-sdk-java-1215-0b57751a.pdf
     */
    private static void initializeVersion() {
        InputStream inputStream = AgentUtils.class.getClassLoader().getResourceAsStream(VERSION_INFO_FILE);
        Properties versionInfoProperties = new Properties();
        try {
            if (inputStream == null) {
                throw new IllegalArgumentException(VERSION_INFO_FILE + " not found on classpath");
            }
            versionInfoProperties.load(inputStream);
            version = versionInfoProperties.getProperty("version");
        } catch (Exception e) {
            logException("Unable to load version information for the running SDK: ", e);
            version = "unknown-version";
        } finally {
            IOUtils.safeCloseStream(inputStream);
        }
    }
}
