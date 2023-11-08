package uk.gov.pmrv.api.common;

import com.querydsl.core.types.Ops.DateTimeOps;
import com.querydsl.jpa.JPQLTemplates;

public class PostgresJpqlTemplates extends JPQLTemplates {

    public static final PostgresJpqlTemplates DEFAULT = new PostgresJpqlTemplates();

    public PostgresJpqlTemplates() {
        this(DEFAULT_ESCAPE);
        add(DateTimeOps.DIFF_DAYS, "(cast({1} as date) - cast({0} as date))");
    }

    public PostgresJpqlTemplates(char escape) {
        super(escape);
    }

}
