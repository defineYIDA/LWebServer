package com.lws.lwebserver.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author zl
 */
@Slf4j
public class PropertyUtil {
    private static Properties props;

    static {
        loadProps();
    }

    private synchronized static void loadProps() {
        log.info("开始加载properties文件内容.......");
        props = new Properties();
        InputStream in = null;
        try {
            //加载配置
            in = PropertyUtil.class.getClassLoader().getResourceAsStream("server.properties");
            props.load(in);
        } catch (FileNotFoundException e) {
            log.error(".properties文件未找到");
        } catch (IOException e) {
            log.error("出现IOException");
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                log.error(".properties文件流关闭出现异常");
            }
        }
        log.info(".properties文件内容：" + props);
    }

    public static String getProperty(String key) {
        if (null == props) {
            loadProps();
        }
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        if (null == props) {
            loadProps();
        }
        return props.getProperty(key, defaultValue);
    }
}
