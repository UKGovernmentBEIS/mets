package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceNoticeOfIntentSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private UUID noticeOfIntent;

    @Size(max = 10000)
    private String comments;

    private Boolean noticeOfIntentCompleted;
}
