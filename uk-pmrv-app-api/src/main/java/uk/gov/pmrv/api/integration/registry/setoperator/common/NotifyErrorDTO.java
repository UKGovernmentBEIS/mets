package uk.gov.pmrv.api.integration.registry.setoperator.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyErrorDTO {

    private String correlationId;
    private SetOperatorIdEventOutcome outcome;
    private SetOperatorIdResponseEvent event;
    private String service;


}
