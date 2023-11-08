package uk.gov.pmrv.api.reporting.domain.verification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.verificationbody.domain.verificationreport.VerificationReport;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AerVerificationReport extends VerificationReport {

    @JsonUnwrapped
    @Valid
    private AerVerificationData verificationData;

    @JsonIgnore
    public Set<UUID> getVerificationReportAttachments() {
        if (fileAttachmentExists()) {
            return Collections.singleton(verificationData.getActivityLevelReport().getFile());
        }
        return Collections.emptySet();
    }

    private boolean fileAttachmentExists() {
        return verificationData != null
            && verificationData.getActivityLevelReport() != null
            && verificationData.getActivityLevelReport().getFile() != null;
    }
}
