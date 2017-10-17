package cn.malgo.annotation.common.util.security;
/**
 * Copyright (c) 2013-2015 All Rights Reserved.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.malgo.annotation.common.util.log.LogUtil;
import cn.malgo.annotation.core.model.enums.BaseResultCodeEnum;
import cn.malgo.annotation.common.util.exception.BaseRuntimeException;
import org.apache.log4j.Logger;

/**
 * 获取文件MD5码
 *
 * @author shenjun
 * @version $Id: MD5Util.java, v 0.1 2015年11月30日 下午8:29:58 shenjun Exp $
 */
public class MD5Util {
    /**  logger*/
    private static final Logger    logger        = Logger.getLogger(MD5Util.class);

    protected static char          HEX_DIGITS[]  = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                                                     '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    protected static MessageDigest messagedigest = null;
    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            String msg = MD5Util.class.getName() + "初始化失败，MessageDigest不支持MD5Util。";
            System.err.println(msg);
            LogUtil.error(logger, nsaex, msg);
        }
    }

    public static String getFileMD5String(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            messagedigest.update(byteBuffer);
            return bufferToHex(messagedigest.digest());
        } catch (IOException e) {
            LogUtil.error(logger, e, "创建文件流失败");
            throw new BaseRuntimeException(BaseResultCodeEnum.SYSTEM_FAILURE, "创建文件流失败");
        } catch (Exception e) {
            LogUtil.error(logger, e, "获取文件MD5码失败");
            throw new BaseRuntimeException(BaseResultCodeEnum.SYSTEM_FAILURE, "获取文件MD5码失败");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e2) {
                    LogUtil.error(logger, e2, "关闭文件流失败");
                }
            }
        }
    }

    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = HEX_DIGITS[(bt & 0xf0) >> 4];
        char c1 = HEX_DIGITS[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    public static boolean checkPassword(String password, String md5PwdStr) {
        String s = getMD5String(password);
        return s.equals(md5PwdStr);
    }

    public static void main(String[] args) throws IOException {
        long begin = System.currentTimeMillis();

        //444324DBA61D09146A9223053F74C0ED
        File big = new File("e:/test/2015-11-24-3 - 副本.xls");

        //String md5 = getFileMD5String(big);

        String md5 = getMD5String("869636029833507");
        long end = System.currentTimeMillis();
        System.out.println("md5:" + md5 + " time:" + ((end - begin)) + "ms");
    }
}
