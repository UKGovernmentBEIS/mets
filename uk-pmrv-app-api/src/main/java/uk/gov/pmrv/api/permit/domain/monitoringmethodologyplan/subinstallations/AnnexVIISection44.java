package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AnnexVIISection44 {
    METHOD_MONITORING_PLAN("4.4.(a) Methods in accordance with the monitoring plan approved under Regulation (EU) No. 601/2012","4.4.(a)"),
    LEGAL_METROLOGICAL_CONTROL("4.4.(b) Readings of measuring instruments subject to national legal metrological control or measuring instruments compliant with the requirements of the Directive 2014/31/EU or Directive 2014/32/EU for direct determination of a data set","4.4.(b)"),
    OPERATOR_CONTROL_NOT_POINT_B("4.4.(c) Readings of measuring instruments under the operators control for direct determination of a data set not falling under point b","4.4.(c)"),
    NOT_OPERATOR_CONTROL_NOT_POINT_B("4.4.(d) Readings of measuring instruments not under the operators control for direct determination of a data set not falling under point b","4.4.(d)"),
    INDIRECT_DETERMINATION("4.4.(e) Readings of measuring instruments for indirect determination of a data set, provided that an appropriate correlation between the measurement and the data set in question is established in line with section 3.4 of this Annex","4.4.(e)"),
    OTHER_METHODS("4.4.(f) Other methods, in particular for historical data or where no other data source can be identified by the operator as available","4.4.(f)");

    private final String description;
    private final String code;

    public static AnnexVIISection44 getByValue(String value) {
        return Arrays.stream(values())
                .filter(section -> section.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
