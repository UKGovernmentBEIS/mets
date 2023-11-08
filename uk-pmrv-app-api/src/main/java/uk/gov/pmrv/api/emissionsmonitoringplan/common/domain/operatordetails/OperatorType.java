package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperatorType {

    COMMERCIAL("Commercial"),
    NON_COMMERCIAL("Non commercial");

    private String description;

}
