package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PermanentCessationRequestPayload extends RequestPayload {

    private PermanentCessation permanentCessation;

    @Builder.Default
    private Map<UUID, String> permanentCessationAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> permanentCessationSectionsCompleted = new HashMap<>();

    private DecisionNotification decisionNotification;
}
