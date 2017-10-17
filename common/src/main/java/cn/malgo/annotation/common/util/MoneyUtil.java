package cn.malgo.annotation.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by 张钟 on 2017/7/10.
 */
public class MoneyUtil {

    /**
     * 保留两位小数
     * @return
     */
    public static String formatBigDecimal(BigDecimal money) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(money);

    }
}
