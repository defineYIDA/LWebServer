package cn.lws.server.util;

import org.junit.Test;

/**
 * Created by zl on 2019/03/01.
 */
public class MimeTypeUtilTest {
    @Test
    public void getMineTypes() throws Exception {
        MimeTypeUtil.getTypes("/static/img/cat.jpeg");
    }

}