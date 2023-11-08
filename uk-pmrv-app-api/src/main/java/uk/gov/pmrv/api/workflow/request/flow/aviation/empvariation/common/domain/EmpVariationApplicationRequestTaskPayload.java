package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class EmpVariationApplicationRequestTaskPayload extends RequestTaskPayload {

	@Builder.Default
    private Map<String, List<Boolean>> empSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> empAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getEmpAttachments();
    }
}
