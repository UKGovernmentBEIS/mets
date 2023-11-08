package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    private NonComplianceReason reason;

    @Builder.Default
    private Set<String> selectedRequests = new HashSet<>();

    private LocalDate nonComplianceDate;

    private LocalDate complianceDate;

    @Size(max = 10000)
    private String comments;

    @JsonUnwrapped
    private NonCompliancePenalties nonCompliancePenalties;
    
    private Boolean sectionCompleted;
}
