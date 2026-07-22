package core.ai.healing;

import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

public final class PageSourceCompressor {

    private static final Logger logger =
            LoggerUtil.getLogger(PageSourceCompressor.class);

    private static final int MAX_CHARS = 8000;

    private static final Pattern REMOVE_ATTRS =
            Pattern.compile(
                    "\\s(?:bounds|package|checkable|checked|focusable|focused|"
                            + "scrollable|long-clickable|password|selected|index|"
                            + "rotation|screenX|screenY)=\"[^\"]*\""
            );

    private static final Pattern EMPTY_LAYOUT =
            Pattern.compile(
                    "<(?:android\\.widget\\.FrameLayout|android\\.view\\.View|"
                            + "android\\.widget\\.LinearLayout|android\\.widget\\.RelativeLayout|"
                            + "XCUIElementTypeOther|XCUIElementTypeWindow)"
                            + "[^>]*(?:text|content-desc|resource-id|name|label|value)=\"\""
                            + "[^/]*/>"
            );

    private static final Pattern EXCESS_WHITESPACE =
            Pattern.compile("\\s{2,}");

    private PageSourceCompressor() {
    }

    public static String compress(String pageSource) {

        if (pageSource == null || pageSource.isBlank()) {
            return "";
        }

        int before =
                pageSource.length();

        String result =
                pageSource;

        result =
                REMOVE_ATTRS.matcher(result)
                        .replaceAll("");

        result =
                EMPTY_LAYOUT.matcher(result)
                        .replaceAll("");

        result =
                EXCESS_WHITESPACE.matcher(result)
                        .replaceAll(" ");

        if (result.length() > MAX_CHARS) {
            result =
                    result.substring(0, MAX_CHARS)
                            + "\n<!-- [truncated] -->";
        }

        long reduction =
                Math.round(
                        100.0
                                * (before - result.length())
                                / before
                );

        logger.info(
                "[Compressor] Page source: {} -> {} chars ({}% reduction)",
                before,
                result.length(),
                reduction
        );

        return result;
    }
}
