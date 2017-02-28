package com.example.mobilesafe;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sing on 13-12-23.
 */

public class UpdateInfoParser {
    /**
     * 解析一个utf-8格式的xml输入流，返回一个UpdateInfo对象
     * XmlPullParser解析bug参考解决：http://blog.csdn.net/nxh_love/article/details/7109762
     *
     * @param is
     * @return UpdateInfo对象
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static UpdateInfo getUpdateInfo(InputStream is) throws XmlPullParserException, IOException {
        UpdateInfo info = new UpdateInfo();

        //获取一个pull解析的实例
        XmlPullParser parser = Xml.newPullParser();

        //解析xml
        parser.setInput(is, "UTF-8");
        parser.nextTag();
        int type = parser.getEventType();
        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String tag = parser.getName();
            String text = parser.nextText();
            if (parser.getEventType() != XmlPullParser.END_TAG) {
                parser.nextTag();
            }
            if (tag.equals("ver")) {
                info.setVersion(text);
            } else if (tag.equals("desc")) {
                info.setDesc(text);
            } else if (tag.equals("apkurl")) {
                info.setApkurl(text);
            }
        }

        return info;
    }
}
