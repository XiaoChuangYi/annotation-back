package cn.malgo.annotation.test;

import cn.malgo.common.security.SecurityUtil;
import org.junit.Test;


/**
 * @author 张钟
 * @date 2017/11/2
 */
public class SecurityUtilTest {

    @Test
    public void test(){
        String text = "氨";
        String newText = SecurityUtil.cryptAESBase64(text);
        System.out.println(newText);


    }

    @Test
    public void testDcrypt(){
        String text = "dMgu/YJAHDVZS8mkEpVHDtQPjn9H3TfbKQksDqjNLjE=";
        String result = SecurityUtil.decryptAESBase64(text);
        System.out.println(result);
    }

}
