package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AviationAerApplicationRequestTaskPayload extends RequestTaskPayload {

    private Year reportingYear;

    private ServiceContactDetails serviceContactDetails;
    
    private Boolean reportingRequired;

    private AviationAerReportingObligationDetails reportingObligationDetails;

    @Builder.Default
    private List<AviationAerMonitoringPlanVersion> aerMonitoringPlanVersions = new ArrayList<>();

    @Builder.Default
    private Map<String, List<Boolean>> aerSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> aerAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();

    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        final Set<UUID> attachments = new HashSet<>();
        if (reportingObligationDetails != null && !ObjectUtils.isEmpty(reportingObligationDetails.getSupportingDocuments())) {
            attachments.addAll(reportingObligationDetails.getSupportingDocuments());
        }
        return Collections.unmodifiableSet(attachments);
    }
}
