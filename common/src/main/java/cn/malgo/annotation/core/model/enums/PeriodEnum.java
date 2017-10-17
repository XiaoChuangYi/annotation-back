package cn.malgo.annotation.core.model.enums;

import java.util.Date;

import cn.malgo.annotation.common.util.DateUtils;

/**
 * Created by 张钟 on 2017/7/25.
 */
public enum PeriodEnum {

                        D("日"),

                        W("周"),

                        M("月"),

    ;

    private String message;

    PeriodEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static Date getStartDate(PeriodEnum periodEnum) {
        Date currentDate = new Date();
        switch (periodEnum) {
            case D:
                return DateUtils.getDayBegin(currentDate);
            case W:
                return DateUtils.getDayBegin(DateUtils.getWeekFirstDay(currentDate));
            case M:
                return DateUtils.getDayBegin(DateUtils.getMonthFirstDay(currentDate));
            default:
                return null;
        }
    }

    /**
     *结束日期,次日的0点
     * @param periodEnum
     * @return
     */
    public static Date getEndDate(PeriodEnum periodEnum) {
        Date currentDate = new Date();
        switch (periodEnum) {
            case D:
                return DateUtils.getDayBegin(DateUtils.addDays(currentDate,1));
            case W:
                return DateUtils.getDayBegin(DateUtils.addDays(DateUtils.getWeekLastDay(currentDate),1));
            case M:
                return DateUtils.getDayBegin(DateUtils.addDays(DateUtils.getMonthLastDay(currentDate),1));
            default:
                return null;
        }
    }

}
