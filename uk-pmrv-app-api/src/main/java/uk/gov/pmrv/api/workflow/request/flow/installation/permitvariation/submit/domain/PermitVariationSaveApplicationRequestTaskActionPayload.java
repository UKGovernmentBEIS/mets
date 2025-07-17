package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

	private Permit permit;
    
    private PermitVariationDetails permitVariationDetails;
	
	private Boolean permitVariationDetailsCompleted;

    @Builder.Default
    private Map<String, List<Boolean>> permitSectionsCompleted = new HashMap<>();
    
    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
}
