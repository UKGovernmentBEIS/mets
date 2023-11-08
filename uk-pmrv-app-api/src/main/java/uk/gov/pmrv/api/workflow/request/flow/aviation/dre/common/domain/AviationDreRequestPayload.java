package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AviationDreRequestPayload extends RequestPayload {

    private Year reportingYear;

    private AerInitiatorRequest initiatorRequest;

    private Boolean sectionCompleted;

    @Builder.Default
    private Map<UUID, String> dreAttachments = new HashMap<>();
}
