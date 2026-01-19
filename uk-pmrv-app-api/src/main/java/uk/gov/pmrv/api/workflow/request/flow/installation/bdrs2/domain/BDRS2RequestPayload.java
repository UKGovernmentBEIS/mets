package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class BDRS2RequestPayload extends RequestPayload {

    private BDRS2 bdrs2;

    private boolean verificationPerformed;

    private BDRS2VerificationReport verificationReport;

    @Builder.Default
    private int bdrs2FileVersion = 1;

    @Builder.Default
    private Map<UUID, String> verificationAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> bdrs2Attachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> bdrs2SectionsCompleted = new HashMap<>();

    public void incrementBdrs2FileVersion() {
        this.bdrs2FileVersion += 1;
    }
}
