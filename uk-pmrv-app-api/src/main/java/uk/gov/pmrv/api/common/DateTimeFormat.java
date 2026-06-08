package uk.gov.pmrv.api.common;

import java.time.format.DateTimeFormatter;

public enum DateTimeFormat {

    DEFAULT_DATE_TIME("dd MMMM yyyy HH:mm:ss");

    private final String pattern;

    DateTimeFormat(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern(pattern);
    }
}