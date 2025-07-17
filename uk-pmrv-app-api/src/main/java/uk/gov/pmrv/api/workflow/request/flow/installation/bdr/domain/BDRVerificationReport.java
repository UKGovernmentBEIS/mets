package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.verificationbody.domain.verificationreport.VerificationReport;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BDRVerificationReport extends VerificationReport {

    @JsonUnwrapped
    @Valid
    private BDRVerificationData verificationData;

    @JsonIgnore
    public Set<UUID> getVerificationReportAttachments() {
        if (fileAttachmentExists()) {
            return Collections.unmodifiableSet(verificationData.getOpinionStatement().getOpinionStatementFiles());
        }
        return Collections.emptySet();
    }

    private boolean fileAttachmentExists() {
        if (ObjectUtils.isEmpty(verificationData)){
            return false;
        }

        if (ObjectUtils.isEmpty(verificationData.getOpinionStatement())){
            return false;
        }

        return ObjectUtils.isNotEmpty(verificationData.getOpinionStatement().getOpinionStatementFiles());
    }
}
