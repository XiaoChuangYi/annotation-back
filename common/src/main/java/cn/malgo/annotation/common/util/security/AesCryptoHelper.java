package cn.malgo.annotation.common.util.security;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;

/**
 * Aes加密辅助类
 *
 * @author osmund
 *
 * @version $Id: AesCryptoHelper.java, v 0.1 2013-7-15 下午2:27:20 WJL Exp $
 */
public class AesCryptoHelper {

    // 客户端统一的 key，
    public static final String SEED       = "MALGO569823";


    /**
     * 获取密钥串
     *
     * @param seed
     * @return
     * @throws Exception
     */
    public static String getRawKey(String seed) throws Exception {
        return toHex(getRawKey(seed.getBytes()));

    }

    /**
     * 加密
     *
     * @param seed
     * @param cleartext
     * @return
     * @throws Exception
     */
    public static String encrypt(String seed, String cleartext) throws Exception {
        if (StringUtils.isBlank(cleartext)) {
            return "";
        }
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    /**
     * 解密
     *
     * @param seed
     * @param encrypted
     * @return
     * @throws Exception
     */
    public static String decrypt(String seed, String encrypted) throws Exception {
        if (StringUtils.isBlank(encrypted)) {
            return "";
        }
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    /**
     * 加密
     *
     * @param seed
     * @param cleartext
     * @return
     * @throws Exception
     */
    public static String encryptBase64(String seed, String cleartext) throws Exception {
        if (StringUtils.isBlank(cleartext)) {
            return "";
        }
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return BASE64.encryptBASE64(result);
    }

    /**
     * 加密
     *
     * @param rawkey
     * @param cleartext
     * @return
     * @throws Exception
     */
    public static String encryptBase64RawKey(String rawkey, String cleartext) throws Exception {
        if (StringUtils.isBlank(cleartext)) {
            return "";
        }
        byte[] rawKey = toByte(rawkey);
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return BASE64.encryptBASE64(result);
    }

    /**
     * 解密
     *
     * @param raw
     * @param encrypted
     * @return
     * @throws Exception
     */
    public static String decryptBase64(String seed, String encrypted) throws Exception {
        if (StringUtils.isBlank(encrypted)) {
            return "";
        }
        byte[] rawKey = getRawKey(seed.getBytes());
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(BASE64.decryptBASE64(encrypted));
        return new String(decrypted);
    }

    /**
     * 解密
     *
     * @param raw
     * @param encrypted
     * @return
     * @throws Exception
     */
    public static String decryptBase64RawKey(String rawkey, String encrypted) throws Exception {
        if (StringUtils.isBlank(encrypted)) {
            return "";
        }
        byte[] rawKey = toByte(rawkey);
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(BASE64.decryptBASE64(encrypted));
        return new String(decrypted);
    }

    /**
     * 计算密钥
     *
     * IOS888888 = new byte[]
     * {-60,-63,-83,-106,-30,29,-28,105,113,80,-97,72,15,122
     * ,4,-83,107,-75,-65,-14,54,-64,38,72,10,-99,-8,94,106,-122,76,24}
     *
     * ANDROID88888 = new byte[]
     * {54,-63,-116,71,-23,-99,-31,65,-22,-92,-48,47,-60
     * ,-75,-68,-88,-28,-76,-73,12,49,-77,44,-113,-70,-43,-99,8,-42,-83,-26,11}
     *
     * YYLC888888 = new byte[]
     * {94,-12,-36,-37,-43,15,32,-59,59,27,-123,38,79,-109
     * ,88,65,-46,20,-89,-13,119,40,69,-96,-11,5,82,-74,-113,110,-37,-127}
     * byte[] raw = skey.getEncoded(); StringBuffer bf = new StringBuffer();
     * bf.append("new byte[] {"); for (byte b : raw) { bf.append(b);
     * bf.append(","); } bf.append("}"); System.out.println(bf.toString());
     *
     * @param seed
     * @return
     * @throws Exception
     */
    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(256, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();

        return skey.getEncoded();
        /*
         * return new byte[] { 0x08, 0x08, 0x04, 0x0b, 0x02, 0x0f, 0x0b, 0x0c,
         * 0x01, 0x03, 0x09, 0x07, 0x0c, 0x03, 0x07, 0x0a, 0x04, 0x0f, 0x06,
         * 0x0f, 0x0e, 0x09, 0x05, 0x01, 0x0a, 0x0a, 0x01, 0x09, 0x06, 0x07,
         * 0x09, 0x0d };
         */
    }

    /**
     * 加密
     *
     * @param raw
     * @param clear
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    /**
     * 解密
     *
     * @param raw
     * @param encrypted
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {

        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);

        for (int i = 0; i < buf.length; i++) {

            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    public static void main(String[] args) {
        try {
            //            String originalText = "pointcore";
            //            String encryptingCode = AesCryptoHelper.encrypt(AesCryptoHelper.SEED, originalText);
            //            System.out.println(encryptingCode);
            //            String originalText1 = AesCryptoHelper.decrypt(AesCryptoHelper.SEED, encryptingCode);
            //            System.out.println(originalText1);

            //            String originalText2 = "8201306220000389";
            //            String encryptingCode2 = AesCryptoHelper.encryptBase64("YYLC20130701", originalText2);
            //            System.out.println(encryptingCode2);
            //            String originalText3 = AesCryptoHelper.decryptBase64("YYLC20130701", encryptingCode2);
            //            System.out.println(originalText3);
            //            System.out.println(AesCryptoHelper.getRawKey("YYLC888888"));
            /*JSONObject jsonObject = new JSONObject();
            jsonObject.put("os","0");
            jsonObject.put("imei","869636029833507");
            jsonObject.put("mac","B4:EF:FA:05:0D:62");
            String str=  JSONObject.toJSONString(jsonObject);
            System.out.println(AesCryptoHelper.encryptBase64RawKey(AesCryptoHelper.LOGIN_SEED, str));*/
            //            String originalText4 = AesCryptoHelper.decrypt(AesCryptoHelper.SEED, "06A8485A03A5B9BB2FAFEDF3A7701478CBC6A9DDBD7B5D884449B4046DDF406D");
            //            System.out.println(originalText4);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
