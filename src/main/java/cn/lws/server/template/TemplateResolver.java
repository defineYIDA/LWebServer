package cn.lws.server.template;

import cn.lws.server.enumeration.ModelScope;
import cn.lws.server.exception.TemplateResolveException;
import cn.lws.server.request.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zl on 2019/03/01.
 */
@Slf4j
public class TemplateResolver {
    public static final Pattern regex = Pattern.compile("\\$\\{(.*?)}");
    
    public static String resolve(String content, Request request) throws TemplateResolveException {
        Matcher matcher = regex.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            log.info("{}", matcher.group(1));
            String placeHolder = matcher.group(1);
            if (placeHolder.indexOf('.') == -1) {
                throw new TemplateResolveException();
            }
            ModelScope scope = ModelScope
                    .valueOf(
                            placeHolder.substring(0, placeHolder.indexOf('.'))
                                    .replace("Scope", "")
                                    .toUpperCase());
            String key = placeHolder.substring(placeHolder.indexOf('.') + 1);
            if (scope == null) {
                throw new TemplateResolveException();
            }
            Object value = null;
            switch (scope) {
                case REQUEST:
                    value = request.getAttribute(key);
                    break;
                case SESSION:
                    value = request.getSession().getAttribute(key);
                    break;
                case APPLICATION:
                    value = request.getServletContext().getAttribute(key);
                    break;
                default:
                    break;
            }
            log.info("value:{}",value);
            if (value == null) {
                matcher.appendReplacement(sb, "");
            } else {
                //把group(1)得到的数据，替换为value
                matcher.appendReplacement(sb, value.toString());
            }
        }
        return sb.toString();
    }
}
