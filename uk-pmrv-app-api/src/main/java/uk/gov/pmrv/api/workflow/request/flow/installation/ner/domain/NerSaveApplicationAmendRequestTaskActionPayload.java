package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NerSaveApplicationAmendRequestTaskActionPayload extends NerSaveApplicationRequestTaskActionPayload {

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
}
