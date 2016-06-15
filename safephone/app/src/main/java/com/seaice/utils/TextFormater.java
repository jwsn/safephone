package com.seaice.utils;

import java.text.DecimalFormat;

/**
 * Created by seaice on 2016/6/7.
 */
public class TextFormater {

    /**
     * 返回bytes的数据对应的文本
     *
     * @param size
     * @return
     */
    public static String getDataSize(long size) {
        DecimalFormat format = new DecimalFormat("###.00");
        if (size < 1024) {
            return size + "bytes";
        } else if (size < 1024 * 1024) {
            float kbSize = size / 1024f;
            return format.format(kbSize) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            float mbSize = size / 1024f / 1024f;
            return format.format(mbSize) + "MB";
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            float gbSize = size / 1024f / 1024f / 1024f;
            return format.format(gbSize) + "GB";
        } else {
            return "size error";
        }
    }

    /**
     * 返回kb的数据大小对应文本
     * @param size
     * @return
     */
    public static String getKbDataSize(long size) {
        return getDataSize(size * 1024);
    }
}
