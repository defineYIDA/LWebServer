package com.lws.lwebserver.core.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * Created by zl on 2019/03/01.
 */
public class XMLUtil {

    public static Document getDocument(InputStream in) {
        try {
            SAXReader reader = new SAXReader();
            return reader.read(in);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }
}
