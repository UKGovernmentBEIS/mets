package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationDoECorsiaApplicationSubmitRequestTaskPayload extends RequestTaskPayload {

    private AviationDoECorsia doe;

    private Boolean sectionCompleted;

    @Builder.Default
    private Map<UUID, String> doeAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getDoeAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return doe != null ? Collections.unmodifiableSet(doe.getSupportingDocuments()) : Collections.emptySet();
    }


}
