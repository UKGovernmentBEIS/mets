package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowances;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermanentCessationSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    @Valid
    private PermanentCessation permanentCessation;

    private Map<String, Boolean> permanentCessationSectionsCompleted = new HashMap<>();

    private Map<UUID, String> permanentCessationAttachments = new HashMap<>();

}
