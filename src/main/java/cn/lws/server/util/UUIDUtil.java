package cn.lws.server.util;

import java.util.UUID;

/**
 * Created by zl on 2019/03/01.
 */
public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","").toUpperCase();
    }
}
