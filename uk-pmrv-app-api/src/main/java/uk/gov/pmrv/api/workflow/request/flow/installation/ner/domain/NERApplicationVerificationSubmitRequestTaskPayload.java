package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class NERApplicationVerificationSubmitRequestTaskPayload extends NERApplicationRequestTaskPayload {


    private NERVerificationReport verificationReport;

    @Builder.Default
    private Map<UUID, String> verificationAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachmentsToDelete() {
        return Map.of();
    }

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        if (!ObjectUtils.isEmpty(verificationReport)) {
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
