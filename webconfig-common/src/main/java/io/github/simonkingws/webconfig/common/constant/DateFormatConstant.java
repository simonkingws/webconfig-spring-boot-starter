package io.github.simonkingws.webconfig.common.constant;

/**
 * 日期常量
 *
 * @author: ws
 * @date: 2024/1/29 14:15
 */
public interface DateFormatConstant {

    String STANDARD_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    String STANDARD_DATE_HM = "yyyy-MM-dd HH:mm";
    String STANDARD_SIMPLE = "yyyy-MM-dd";
    String SLASH_DATE = "yyyy/MM/dd";
    String YYYYMMDD = "yyyyMMdd";
    String YEAR_MONTH = "yyyyMM";
    String[] FORMAT_PATTERNS = {STANDARD_SIMPLE, STANDARD_DATE_HM, STANDARD_SIMPLE, SLASH_DATE, YYYYMMDD, YEAR_MONTH};

    String FILL_START_SECOND = ":00";
    String FILL_START_DAY = "01";

    int STANDARD_DATE_TIME_LENGTH = 19;
    int STANDARD_DATE_HM_LENGTH = 16;
    int STANDARD_DATE_LENGTH = 10;
    int DATE_LENGTH = 8;
    int YM_LENGTH = 6;
}
