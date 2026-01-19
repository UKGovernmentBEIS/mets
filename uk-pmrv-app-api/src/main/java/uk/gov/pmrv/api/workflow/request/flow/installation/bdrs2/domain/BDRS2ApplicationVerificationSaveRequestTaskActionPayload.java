package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
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
public class BDRS2ApplicationVerificationSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    @JsonUnwrapped
    private BDRS2VerificationData verificationData;

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();
}
