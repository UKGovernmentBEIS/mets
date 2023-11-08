package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AviationDreApplicationRequestTaskPayload extends RequestTaskPayload {

    private Boolean sectionCompleted;

    @Builder.Default
    private Map<UUID, String> dreAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getDreAttachments();
    }
}
