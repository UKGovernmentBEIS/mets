package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AnnexVIISection46 {
    CALCULATION_METHOD_MONITORING_PLAN("4.6. (a) Methods for determining calculation factors in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012","4.6.(a)"),
    LABORATORY_ANALYSES_SECTION_61("4.6. (b) Laboratory analyses in accordance with section 6.1 of  Annex VII (FAR)","4.6.(b)"),
    SIMPLIFIED_LABORATORY_ANALYSES_SECTION_62("4.6. (c) Simplified laboratory analyses in accordance with section 6.2 of Annex VII (FAR)","4.6.(c)"),
    CONSTANT_VALUES_STANDARD_SUPPLIER("4.6. (d) Constant values based on one of the following data sources: standard factors, literature values, values specified and guaranteed by the supplier","4.6.(d)"),
    CONSTANT_VALUES_SCIENTIFIC_EVIDENCE("4.6. (e) Constant values based on one of the following data sources: standard/stoichiometric factors, analysis-based values, other values based on scientific evidence","4.6.(e)");

    private final String description;
    private final String code;

    public static AnnexVIISection46 getByValue(String value) {
        return Arrays.stream(values())
                .filter(section -> section.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
