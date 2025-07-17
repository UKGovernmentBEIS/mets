package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NerSaveAuthorityResponseRequestTaskActionPayload extends RequestTaskActionPayload {

    private LocalDate submittedToAuthorityDate;
    
    private AuthorityResponse authorityResponse;

    @Builder.Default
    private Map<String, Boolean> nerSectionsCompleted = new HashMap<>();
}
