package com.seaice.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 读取流工具
 * Created by seaice on 2016/3/3.
 */
public class StreamUtil {

    /**
     * 从指定输入流中获取数组，并返回String
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String readFromSteam(InputStream inputStream) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;

        while((len = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }

        inputStream.close();
        out.close();

        return out.toString();
    }
}
