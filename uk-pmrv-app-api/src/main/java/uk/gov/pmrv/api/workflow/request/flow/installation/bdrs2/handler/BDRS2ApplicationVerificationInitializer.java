package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper.BDRS2Mapper;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class BDRS2ApplicationVerificationInitializer implements InitializeRequestTaskHandler {

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;
    private static final BDRS2Mapper BDRS2_MAPPER = Mappers.getMapper(BDRS2Mapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());
        final BDRS2RequestPayload requestPayload = (BDRS2RequestPayload) request.getPayload();

        final Long requestVBId = request.getVerificationBodyId();
        final Long verificationReportVBId = Optional.ofNullable(requestPayload.getVerificationReport())
                .map(VerificationReport::getVerificationBodyId).orElse(null);

        // If VB id is changed clear verification report from request
        if(isVbChanged(requestVBId, verificationReportVBId)) {
            requestPayload.setVerificationReport(null);
            requestPayload.setVerificationSectionsCompleted(new HashMap<>());
            requestPayload.setVerificationAttachments(new HashMap<>());
        }

        final BDRS2VerificationReport latestVerificationReport = BDRS2VerificationReport.builder()
                .verificationBodyId(requestVBId)
                .verificationBodyDetails(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId).orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestVBId)))
                .verificationData(isVbChanged(requestVBId, verificationReportVBId)
                        ? BDRS2VerificationData.builder().build()
                        : requestPayload.getVerificationData())
                .build();

        return BDRS2_MAPPER.toBDRS2ApplicationVerificationRequestTaskPayload(
                (BDRS2RequestPayload) request.getPayload(),
                installationOperatorDetails,
                latestVerificationReport
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(
                RequestTaskType.BDRS2_APPLICATION_VERIFICATION_SUBMIT,
                RequestTaskType.BDRS2_AMEND_APPLICATION_VERIFICATION_SUBMIT
        );
    }

    private boolean isVbChanged(Long requestVBId, Long verificationReportVBId) {
        return !requestVBId.equals(verificationReportVBId);
    }
}
