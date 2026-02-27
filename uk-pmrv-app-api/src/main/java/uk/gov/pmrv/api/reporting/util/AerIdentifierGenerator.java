package uk.gov.pmrv.api.reporting.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AerIdentifierGenerator {

    public String generate(Long accountId, int reportingYear) {
        return String.format("%s%05d-%d", "AEM", accountId, reportingYear);    }
}
