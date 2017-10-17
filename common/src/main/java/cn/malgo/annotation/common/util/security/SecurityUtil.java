package cn.malgo.annotation.common.util.security;

import java.text.MessageFormat;

import cn.malgo.annotation.common.util.log.LogUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author tianlu
 * @version $Id: SecurityUtil.java, v 0.1 2014-3-11 上午9:49:56 tianlu Exp $
 */
public class SecurityUtil {

    private static final Logger logger = Logger.getLogger(SecurityUtil.class);

    public static String cryptAES(String str) {
        try {
            if (StringUtils.length(str) > 30) {
                return str;
            }
            str = AesCryptoHelper.encrypt(AesCryptoHelper.SEED, str);
        } catch (Exception e) {
            LogUtil.error(logger, e, MessageFormat.format("信息AES加密失败, text={0}", str));
        }
        return str;
    }

    public static String decryptAES(String str) {
        try {
            if (StringUtils.length(str) <= 30) {
                return str;
            }
            str = AesCryptoHelper.decrypt(AesCryptoHelper.SEED, str);
        } catch (Exception e) {
            LogUtil.error(logger, e, MessageFormat.format("信息AES解密失败, text={0}", str));
        }
        return str;
    }

    public static void main(String[] args) {
        String key = SecurityUtil.cryptAES("370782198807152877");
        System.out.println(key);
    }

}
