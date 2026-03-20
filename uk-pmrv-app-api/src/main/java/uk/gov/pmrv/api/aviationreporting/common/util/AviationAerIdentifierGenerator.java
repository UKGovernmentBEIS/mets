package uk.gov.pmrv.api.aviationreporting.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AviationAerIdentifierGenerator {

    public String generate(Long accountId, int reportingYear) {
        return String.format("%s%05d-%d", "AEM", accountId, reportingYear);
    }
}
