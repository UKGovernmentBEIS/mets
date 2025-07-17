package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain;


import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.HashMap;
import java.util.Map;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload extends RequestTaskPayload {


    private AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting;

    @Builder.Default
    private Map<String, Boolean> aviationAerCorsiaAnnualOffsettingSectionsCompleted = new HashMap<>();

    private DecisionNotification decisionNotification;
}
