package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class BDRS2RegulatorReviewReturnForAmendsTaskActionPayload extends RequestTaskActionPayload {

    @Builder.Default
    private Map<String, Boolean> bdrs2SectionsCompleted = new HashMap<>();
}
