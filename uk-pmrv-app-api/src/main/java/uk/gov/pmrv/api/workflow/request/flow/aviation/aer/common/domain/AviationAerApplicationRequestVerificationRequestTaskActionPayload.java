package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerApplicationRequestVerificationRequestTaskActionPayload extends RequestTaskActionPayload {

    /**
     * On change Verification Body the sections completed by Verifier should be empty.
     * In case of resending AER for verification without changing verification body the sections completed should not be changed.
     */
    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();
    
    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
}
