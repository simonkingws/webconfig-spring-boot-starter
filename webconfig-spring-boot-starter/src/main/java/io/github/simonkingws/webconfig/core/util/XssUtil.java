package io.github.simonkingws.webconfig.core.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * Xss攻击清除工具
 *
 * @author: ws
 * @date: 2024/1/29 15:16
 */
public class XssUtil {

    /**
     *  使用自带的白名单
     */
    private static final Safelist WHITE_LIST = Safelist.relaxed();
    static {
        // 富文本编辑时一些样式是使用 style 来进行实现的
        // 比如红色字体 style="color:red;"
        // 所以需要给所有标签添加 style 属性
        WHITE_LIST.addAttributes(":all", "style");
    }

    /**
     * 配置过滤化参数, 不对代码进行格式化
     */
    private static final Document.OutputSettings OUTPUT_SETTINGS = new Document.OutputSettings().prettyPrint(false);

    public static String clean(String unsafe) {
        return Jsoup.clean(unsafe, "", WHITE_LIST, OUTPUT_SETTINGS);
    }
}
