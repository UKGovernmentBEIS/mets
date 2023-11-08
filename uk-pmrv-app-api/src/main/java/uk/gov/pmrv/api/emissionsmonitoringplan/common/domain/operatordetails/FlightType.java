package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlightType {

    SCHEDULED("Scheduled"),
    NON_SCHEDULED("Non scheduled");

    private String description;
}
