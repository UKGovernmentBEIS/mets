package uk.gov.pmrv.api.common.utils;

import org.springframework.util.StringUtils;

import java.util.Locale;

public final class SearchTermUtils {

    private static final char ESCAPE_CHAR = '\\';

    private SearchTermUtils() {
    }

    public static String toSearchPattern(String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            return null;
        }
        String escaped = escapeLikeWildcards(searchTerm.toLowerCase(Locale.ROOT));
        return "%" + escaped + "%";
    }

    private static String escapeLikeWildcards(String input) {
        return input
                .replace(String.valueOf(ESCAPE_CHAR), ESCAPE_CHAR + "" + ESCAPE_CHAR)
                .replace("%", ESCAPE_CHAR + "%")
                .replace("_", ESCAPE_CHAR + "_");
    }
}