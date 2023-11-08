package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceApplicationSubmitRequestTaskPayload extends RequestTaskPayload 
    implements NonComplianceRequestTaskClosable {

    @Builder.Default
    private List<RequestInfoDTO> availableRequests = new ArrayList<>();
    
    @NotNull
    private NonComplianceReason reason;
    
    private LocalDate nonComplianceDate;
    
    private LocalDate complianceDate;
    
    @Size(max = 10000)
    private String comments;

    @Builder.Default
    private Set<String> selectedRequests = new HashSet<>();

    @Valid
    @NotNull
    @JsonUnwrapped
    private NonCompliancePenalties nonCompliancePenalties;
    
    private NonComplianceCloseJustification closeJustification;
    
    @Builder.Default
    private Map<UUID, String> nonComplianceAttachments = new HashMap<>();

    private Boolean sectionCompleted;
    
    @Override
    public Map<UUID, String> getAttachments() {
        return this.getNonComplianceAttachments();
    }
    
    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return this.closeJustification != null ? this.closeJustification.getFiles() : Collections.emptySet();
    }
}
