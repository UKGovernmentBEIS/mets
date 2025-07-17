package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.verificationbody.domain.verificationreport.VerificationReport;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper.ALRMapper;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ALRApplicationVerificationInitializer implements InitializeRequestTaskHandler {

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;
    private static final ALRMapper ALR_MAPPER = Mappers.getMapper(ALRMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());
        final ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();

        final Long requestVBId = request.getVerificationBodyId();
        final Long verificationReportVBId = Optional.ofNullable(requestPayload.getVerificationReport())
                .map(VerificationReport::getVerificationBodyId).orElse(null);

        // If VB id is changed clear verification report from request
        if(isVbChanged(requestVBId, verificationReportVBId)) {
            requestPayload.setVerificationReport(null);
            requestPayload.setVerificationSectionsCompleted(new HashMap<>());
            requestPayload.setVerificationAttachments(new HashMap<>());
        }

        final ALRVerificationReport latestVerificationReport = ALRVerificationReport.builder()
                .verificationBodyId(requestVBId)
                .verificationBodyDetails(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId).orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestVBId)))
                .verificationData(isVbChanged(requestVBId, verificationReportVBId)
                        ? ALRVerificationData.builder().build()
                        : requestPayload.getVerificationData())
                .build();

        return ALR_MAPPER.toALRApplicationVerificationRequestTaskPayload(
                (ALRRequestPayload) request.getPayload(),
                installationOperatorDetails,
                latestVerificationReport
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.ALR_APPLICATION_VERIFICATION_SUBMIT);
    }

    private boolean isVbChanged(Long requestVBId, Long verificationReportVBId) {
        return !requestVBId.equals(verificationReportVBId);
    }
}
