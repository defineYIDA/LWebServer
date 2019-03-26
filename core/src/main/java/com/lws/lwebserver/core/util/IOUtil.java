package com.lws.lwebserver.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by zl on 2019/03/01.
 */
@Slf4j
public class IOUtil {

    public static byte[] getBytesFromFile(String fileName) throws IOException {
        InputStream in = IOUtil.class.getResourceAsStream(fileName);
        if (in == null) {
            log.info("Not Found File:{}",fileName);
            throw new FileNotFoundException();
        }
        log.info("正在读取文件:{}",fileName);
        return getBytesFromStream(in);
    }

    public static byte[] getBytesFromStream(InputStream in) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while((len = in.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        in.close();
        return outStream.toByteArray();
    }

}
