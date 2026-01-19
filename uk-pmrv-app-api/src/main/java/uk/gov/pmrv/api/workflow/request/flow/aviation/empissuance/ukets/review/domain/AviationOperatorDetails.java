package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationOperatorDetails {

    private String emitterId;
    private String emissionsPlanId;
    private String operatorName;
    private Integer firstYearOfReportingObligation;
    private String regulator;

}
