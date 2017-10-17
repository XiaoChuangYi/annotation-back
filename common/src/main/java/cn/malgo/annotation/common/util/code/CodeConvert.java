package cn.malgo.annotation.common.util.code;

import cn.malgo.annotation.common.util.log.LogUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张钟 on 2017/8/28.
 */
public class CodeConvert {

    private final static Logger log                      = Logger.getLogger(CodeConvert.class);


    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }


    public static List<String> toGBK(String source) {
        List<String> result = new ArrayList<>();
        if(StringUtils.isBlank(source)){
            return result;
        }
        byte[] bytes = new byte[0];
        try {
            bytes = source.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            LogUtil.error(log,e,"转码异常!");
        }
        for(byte b : bytes) {
            result.add(Integer.toHexString((b & 0xff)).toUpperCase());
        }
        return result;
    }
    public static List<String> toGBK(int blankLength) {
        List<String> result = new ArrayList<>();
        String source="";
        for(int i =0;i<blankLength;i++){
            source = source + " ";
        }
        byte[] bytes = new byte[0];
        try {
            bytes = source.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            LogUtil.error(log,e,"转码异常!");
        }
        for(byte b : bytes) {
            result.add(Integer.toHexString((b & 0xff)).toUpperCase());
        }
        return result;
    }
}
