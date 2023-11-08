package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperationScope {

    UK_DOMESTIC("UK domestic"),
    UK_TO_EEA_COUNTRIES("UK to EEA countries");

    private String description;
}
