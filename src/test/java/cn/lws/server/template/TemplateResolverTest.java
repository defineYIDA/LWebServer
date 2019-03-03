package cn.lws.server.template;

import cn.lws.server.constant.CharsetProperties;
import cn.lws.server.util.IOUtil;
import org.junit.Test;

/**
 * Created by zl on 2019/03/01.
 */
public class TemplateResolverTest {
    @Test
    public void resolve() throws Exception {
        byte[] rawBody = IOUtil.getBytesFromFile("/views/success.html");
        String body = new String(rawBody, CharsetProperties.UTF_8_CHARSET);
        TemplateResolver.resolve(body,null);
    }

}