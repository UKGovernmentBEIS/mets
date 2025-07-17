package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AnnexVIISection45 {
    LEGAL_METROLOGICAL_CONTROL_READING("4.5. (a) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU","4.5.(a)"),
    OPERATOR_CONTROL_DIRECT_READING_NOT_A("4.5. (b) Readings of measuring instruments under the operator's control for direct determination of a data set not falling under point a","4.5.(b)"),
    NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A("4.5. (c) Readings of measuring instruments not under the operator's control for direct determination of a data set not falling under point a","4.5.(c)"),
    INDIRECT_DETERMINATION_READING("4.5. (d) Readings of measuring instruments for indirect determination of a data set, provided that an appropriate correlation between the measurement and the data set in question is established in line with section 3.4 of Annex VII (FAR)","4.5.(d)"),
    PROXY_CALCULATION_METHOD("4.5. (e) Calculation of a proxy for the determining net amounts of measurable heat in accordance with Method 3 of section 7.2 in Annex VII (FAR)","4.5.(e)"),
    OTHER_METHODS("4.5. (f) Other methods, in particular for historical data or where no other data source can be identified by the operator as available","4.5.(f)");

    private final String description;
    private final String code;

    public static AnnexVIISection45 getByValue(String value) {
        return Arrays.stream(values())
                .filter(section -> section.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
