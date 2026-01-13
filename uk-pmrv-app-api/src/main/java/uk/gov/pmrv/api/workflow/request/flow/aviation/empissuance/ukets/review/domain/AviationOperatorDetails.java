package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationOperatorDetails {

    private String emitterId;
    private String emissionsPlanId;
    private String operatorName;
    private LocalDate firstKnownAviationActivity;
    private String regulator;

}
