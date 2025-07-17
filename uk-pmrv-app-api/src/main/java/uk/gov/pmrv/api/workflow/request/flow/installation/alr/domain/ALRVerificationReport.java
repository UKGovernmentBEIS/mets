package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ALRVerificationReport extends VerificationReport {

    @JsonUnwrapped
    @Valid
    private ALRVerificationData verificationData;


    @JsonIgnore
    public Set<UUID> getVerificationReportAttachments() {
        if (!hasOpinionStatement()) {
            return Collections.emptySet();
        }

        Set<UUID> attachments = new HashSet<>();

        Set<UUID> opinionFiles = verificationData.getOpinionStatement().getOpinionStatementFiles();
        if (ObjectUtils.isNotEmpty(opinionFiles)) {
            attachments.addAll(opinionFiles);
        }

        Set<UUID> supportingFiles = verificationData.getOpinionStatement().getSupportingFiles();
        if (ObjectUtils.isNotEmpty(supportingFiles)) {
            attachments.addAll(supportingFiles);
        }

        return attachments;
    }

    private boolean hasOpinionStatement() {
        return verificationData != null && verificationData.getOpinionStatement() != null;
    }
}
