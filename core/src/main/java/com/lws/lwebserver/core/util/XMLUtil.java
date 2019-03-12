package com.lws.lwebserver.core.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * Created by zl on 2019/03/01.
 */
public class XMLUtil {
    
    public static Document getDocument(String xmlURL) {
        try {
            SAXReader reader = new SAXReader();
            return reader.read(xmlURL);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }
}
