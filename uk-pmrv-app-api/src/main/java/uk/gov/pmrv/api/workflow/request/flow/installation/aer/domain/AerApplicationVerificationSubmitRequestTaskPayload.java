package uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AerApplicationVerificationSubmitRequestTaskPayload extends AerApplicationRequestTaskPayload {
    private AerVerificationReport verificationReport;

    @Builder.Default
    private Map<UUID, String> verificationAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachmentsToDelete() {
          return Map.of();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        if (verificationReport != null) {
            return Stream.of(super.getReferencedAttachmentIds(), verificationReport.getVerificationReportAttachments())
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        }
        return super.getReferencedAttachmentIds();
    }

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), getVerificationAttachments())
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
