package io.github.simonkingws.webconfig.common.formatter;

import io.github.simonkingws.webconfig.common.constant.DateFormatConstant;
import io.github.simonkingws.webconfig.common.constant.SymbolConstant;
import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 自定义Format，映射LocalDateTime
 * 只能处理formdata数据，JOSN数据无法通过该Formatter处理
 *
 * @author: ws
 * @date: 2024/1/29 14:38
 */
public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {

    /**
     * 日期格式化
     */
    private String pattern;

    public LocalDateTimeFormatter() {
    }

    public LocalDateTimeFormatter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public LocalDateTime parse(String text, Locale locale) {
        LocalDateTime localDateTime = null;
        if (StringUtils.hasLength(text)) {
            switch (text.length()){
                case DateFormatConstant.STANDARD_DATE_TIME_LENGTH:
                    localDateTime = LocalDateTime.parse(text, DateTimeFormatter.ofPattern(DateFormatConstant.STANDARD_DATE_TIME));
                    break;
                case DateFormatConstant.STANDARD_DATE_HM_LENGTH:
                    localDateTime = LocalDateTime.parse(text + DateFormatConstant.FILL_START_SECOND, DateTimeFormatter.ofPattern(DateFormatConstant.STANDARD_DATE_HM));
                    break;
                case DateFormatConstant.STANDARD_DATE_LENGTH:
                    if (text.contains(SymbolConstant.CROSSBAR)) {
                        localDateTime = LocalDate.parse(text, DateTimeFormatter.ofPattern(DateFormatConstant.STANDARD_SIMPLE)).atStartOfDay();
                    }else if (text.contains(SymbolConstant.SLASH)) {
                        localDateTime = LocalDate.parse(text, DateTimeFormatter.ofPattern(DateFormatConstant.SLASH_DATE)).atStartOfDay();
                    }
                    break;
                case DateFormatConstant.DATE_LENGTH:
                    localDateTime = LocalDate.parse(text, DateTimeFormatter.ofPattern(DateFormatConstant.YYYYMMDD)).atStartOfDay();
                    break;
                case DateFormatConstant.YM_LENGTH:
                    localDateTime = LocalDate.parse(text + DateFormatConstant.FILL_START_DAY, DateTimeFormatter.ofPattern(DateFormatConstant.YEAR_MONTH)).atStartOfDay();
                    break;
            }
        }

        return localDateTime;
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        if (StringUtils.hasLength(this.pattern)) {
            return DateTimeFormatter.ofPattern(this.pattern).format(object);
        }
        return DateTimeFormatter.ofPattern(DateFormatConstant.STANDARD_DATE_TIME).format(object);
    }
}
