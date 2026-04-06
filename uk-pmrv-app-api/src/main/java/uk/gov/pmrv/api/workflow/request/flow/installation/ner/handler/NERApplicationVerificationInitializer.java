package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NERMapper;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class NERApplicationVerificationInitializer implements InitializeRequestTaskHandler {

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final VerificationBodyDetailsQueryService verificationBodyDetailsQueryService;
    private static final NERMapper NER_MAPPER = Mappers.getMapper(NERMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());
        final NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();

        final Long requestVBId = request.getVerificationBodyId();
        final Long verificationReportVBId = Optional.ofNullable(requestPayload.getVerificationReport())
                .map(VerificationReport::getVerificationBodyId).orElse(null);

        // If VB id is changed clear verification report from request
        if(isVbChanged(requestVBId, verificationReportVBId)) {
            requestPayload.setVerificationReport(null);
            requestPayload.setVerificationSectionsCompleted(new HashMap<>());
            requestPayload.setVerificationAttachments(new HashMap<>());
        }

        final NERVerificationReport latestVerificationReport = NERVerificationReport.builder()
                .verificationBodyId(requestVBId)
                .verificationBodyDetails(verificationBodyDetailsQueryService.getVerificationBodyDetails(requestVBId).orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestVBId)))
                .verificationData(isVbChanged(requestVBId, verificationReportVBId)
                        ? NERVerificationData.builder().build()
                        : requestPayload.getVerificationData())
                .build();

        return NER_MAPPER.toNERApplicationVerificationRequestTaskPayload(
                (NerRequestPayload) request.getPayload(),
                installationOperatorDetails,
                latestVerificationReport
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.NER_APPLICATION_VERIFICATION_SUBMIT);
    }

    private boolean isVbChanged(Long requestVBId, Long verificationReportVBId) {
        return !requestVBId.equals(verificationReportVBId);
    }
}
